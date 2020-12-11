package com.maksym.findthis.Components;

import org.opencv.core.Mat;

public interface MatchObjectsCallback {
    void matchObjectsCallback(Mat homography, boolean found);
}
