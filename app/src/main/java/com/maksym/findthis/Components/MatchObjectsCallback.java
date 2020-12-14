package com.maksym.findthis.Components;

import org.opencv.core.Mat;

public interface MatchObjectsCallback {
    void matchObjectsCallback(float[] sceneCorners, Mat frame, boolean found);
}
