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

    private static class FakeLauncher implements FfmpegProcessLauncher {
        private final FakeHandle handle = new FakeHandle();

        @Override
        public FfmpegProcessHandle launch(List<String> command) {
            return handle;
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
