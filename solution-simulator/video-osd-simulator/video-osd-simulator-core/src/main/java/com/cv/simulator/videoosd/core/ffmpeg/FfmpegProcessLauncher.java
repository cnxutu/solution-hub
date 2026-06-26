package com.cv.simulator.videoosd.core.ffmpeg;

import java.util.List;

public interface FfmpegProcessLauncher {

    FfmpegProcessHandle launch(List<String> command);
}
