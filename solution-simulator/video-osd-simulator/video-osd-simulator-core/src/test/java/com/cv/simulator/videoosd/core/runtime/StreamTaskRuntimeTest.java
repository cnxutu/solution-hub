package com.cv.simulator.videoosd.core.runtime;

import com.cv.simulator.videoosd.core.enums.TaskStatus;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegProcessHandle;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegProcessLauncher;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamTaskRuntimeTest {

    @Test
    void startsAndStopsManagedProcess() {
        FakeLauncher launcher = new FakeLauncher();
        StreamTaskRuntime runtime = new StreamTaskRuntime(launcher);

        StreamTaskSnapshot started = runtime.start(1L, List.of("ffmpeg", "-version"));
        StreamTaskSnapshot stopped = runtime.stop(1L);

        assertEquals(TaskStatus.RUNNING, started.getStatus());
        assertEquals(TaskStatus.STOPPED, stopped.getStatus());
        assertEquals(true, launcher.handle.destroyed);
    }

    @Test
    void stopsManagedSidecarProcessesTogetherWithPrimaryProcess() {
        FakeLauncher launcher = new FakeLauncher();
        StreamTaskRuntime runtime = new StreamTaskRuntime(launcher);

        runtime.start(1L, List.of("ffmpeg", "-version"));
        runtime.startSidecar(1L, List.of("cmd.exe", "/c", "D:\\drc-osd-collector\\回放.bat"), "mqtt-sender");
        runtime.stop(1L);

        assertEquals(2, launcher.launchCount);
        assertEquals(true, launcher.handle.destroyed);
        assertEquals(true, launcher.sidecarHandle.destroyed);
    }

    private static class FakeLauncher implements FfmpegProcessLauncher {
        private final FakeHandle handle = new FakeHandle();
        private final FakeHandle sidecarHandle = new FakeHandle();
        private int launchCount;

        @Override
        public FfmpegProcessHandle launch(List<String> command) {
            launchCount++;
            return launchCount == 1 ? handle : sidecarHandle;
        }
    }

    private static class FakeHandle implements FfmpegProcessHandle {
        private boolean destroyed;

        @Override
        public boolean isAlive() {
            return !destroyed;
        }

        @Override
        public void destroy() {
            destroyed = true;
        }
    }
}
