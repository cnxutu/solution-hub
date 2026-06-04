package com.cv.rtsp.core.manager;

import com.cv.rtsp.core.client.RtspStreamClient;
import com.cv.rtsp.core.listener.RtspFrameListener;
import com.cv.rtsp.core.model.RtspEndpoint;
import com.cv.rtsp.core.model.RtspPullSession;
import com.cv.rtsp.core.model.RtspSessionSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class RtspStreamManager {

    private final RtspStreamClient client;
    private final Map<String, ManagedStreamTask> tasks = new ConcurrentHashMap<String, ManagedStreamTask>();
    private final ScheduledExecutorService scheduler;

    public RtspStreamManager(RtspStreamClient client) {
        this.client = client;
        this.scheduler = Executors.newScheduledThreadPool(4, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "rtsp-stream-manager");
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public synchronized RtspSessionSnapshot start(RtspEndpoint endpoint, RtspFrameListener listener) {
        ManagedStreamTask existing = tasks.get(endpoint.getStreamId());
        if (existing != null) {
            return existing.snapshot();
        }
        ManagedStreamTask task = new ManagedStreamTask(endpoint, listener);
        tasks.put(endpoint.getStreamId(), task);
        try {
            task.openAndSchedule(false);
        } catch (RuntimeException exception) {
            tasks.remove(endpoint.getStreamId());
            throw exception;
        }
        return task.snapshot();
    }

    public synchronized boolean stop(String streamId) {
        ManagedStreamTask task = tasks.remove(streamId);
        if (task == null) {
            return false;
        }
        task.shutdown();
        return true;
    }

    public synchronized void shutdown() {
        for (ManagedStreamTask task : new ArrayList<ManagedStreamTask>(tasks.values())) {
            task.shutdown();
        }
        tasks.clear();
        scheduler.shutdownNow();
    }

    public Collection<RtspSessionSnapshot> listSessions() {
        List<RtspSessionSnapshot> snapshots = new ArrayList<RtspSessionSnapshot>();
        for (ManagedStreamTask task : tasks.values()) {
            snapshots.add(task.snapshot());
        }
        Collections.sort(snapshots, (a, b) -> a.getStreamId().compareTo(b.getStreamId()));
        return snapshots;
    }

    private class ManagedStreamTask {

        private final RtspEndpoint endpoint;
        private final RtspFrameListener listener;
        private volatile RtspPullSession session;
        private volatile boolean closed;
        private volatile ScheduledFuture<?> keepAliveFuture;
        private volatile ScheduledFuture<?> frameFuture;
        private volatile ScheduledFuture<?> reconnectFuture;

        private ManagedStreamTask(RtspEndpoint endpoint, RtspFrameListener listener) {
            this.endpoint = endpoint;
            this.listener = listener;
        }

        private synchronized void openAndSchedule(boolean reconnect) {
            if (closed) {
                return;
            }
            session = client.open(endpoint, listener);
            if (reconnect) {
                session.getMetrics().incrementReconnect();
            }
            scheduleKeepAlive();
            scheduleFramePull();
        }

        private void scheduleKeepAlive() {
            keepAliveFuture = scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (closed) {
                        return;
                    }
                    try {
                        client.keepAlive(session);
                    } catch (Exception exception) {
                        handleFailure(exception);
                    }
                }
            }, endpoint.getKeepAliveIntervalSeconds(), endpoint.getKeepAliveIntervalSeconds(), TimeUnit.SECONDS);
        }

        private void scheduleFramePull() {
            frameFuture = scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (closed) {
                        return;
                    }
                    try {
                        client.pullFrame(session, listener);
                    } catch (Exception exception) {
                        handleFailure(exception);
                    }
                }
            }, 0L, endpoint.getFramePullIntervalMillis(), TimeUnit.MILLISECONDS);
        }

        private synchronized void handleFailure(Exception exception) {
            cancelSchedules();
            if (session != null) {
                session.setLastError(exception.getMessage());
            }
            if (!endpoint.isAutoReconnect() || closed) {
                return;
            }
            reconnectFuture = scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        openAndSchedule(true);
                    } catch (Exception reconnectException) {
                        handleFailure(reconnectException);
                    }
                }
            }, 2L, TimeUnit.SECONDS);
        }

        private synchronized void shutdown() {
            closed = true;
            cancelSchedules();
            if (session != null) {
                client.close(session, listener);
            }
        }

        private void cancelSchedules() {
            if (keepAliveFuture != null) {
                keepAliveFuture.cancel(true);
            }
            if (frameFuture != null) {
                frameFuture.cancel(true);
            }
            if (reconnectFuture != null) {
                reconnectFuture.cancel(true);
            }
        }

        private RtspSessionSnapshot snapshot() {
            return session == null ? null : RtspSessionSnapshot.from(session);
        }
    }
}
