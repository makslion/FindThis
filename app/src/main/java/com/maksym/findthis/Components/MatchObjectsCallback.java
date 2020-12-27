package com.maksym.findthis.Components;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

public interface MatchObjectsCallback {
    void matchObjectsCallback(float[] sceneCorners, Mat frame, boolean found);
}
