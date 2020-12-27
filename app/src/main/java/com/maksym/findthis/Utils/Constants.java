package com.maksym.findthis.Utils;

import org.opencv.features2d.AKAZE;
import org.opencv.features2d.BRISK;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.features2d.GFTTDetector;
import org.opencv.features2d.KAZE;
import org.opencv.features2d.MSER;
import org.opencv.features2d.ORB;
import org.opencv.features2d.SIFT;

public class Constants {
    public static final int AKAZE_DETECTOR_ID = 1;  // scale rot invariant
    public static final int BRISK_DETECTOR_ID = 2;  // scale rot affine (partially) invariant
    public static final int FAST_DETECTOR_ID = 3;
    public static final int GFTT_DETECTOR_ID = 4;
    public static final int KAZE_DETECTOR_ID = 5;
    public static final int MSER_DETECTOR_ID = 6;   // affine invariant
    public static final int ORB_DETECTOR_ID = 7;    // brief + fast. scale rot affine (partially) invariant
    public static final int SIFT_DETECTOR_ID  = 8;   // scale rot affine (partially) invariant

    public static final String [] BIG_DETECTORS = {"AKAZE","BRISK","MSER","ORB","SIFT"};
    public static final String [] SMALL_DETECTORS = {"ORB","GFTT"};


    public static final String DATA_DIR_NAME = "Objects/";


    public static final String EXTRA_OBJECT = "object here";
    public static final String EXTRA_BITMAP = "image here";
    public static final String EXTRA_DETECTOR = "detector array id here";


    public static final float RATIO_THRESHOLD = 0.75f;
    public static final double MATCHED_FEATURES_THRESHOLD = 0.2;
    public static final double MATCHED_FEATURES_TRACKING_THRESHOLD = 0.2;


}
