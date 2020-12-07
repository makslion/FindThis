package com.maksym.findthis.OpenCVmagic;

import android.graphics.Bitmap;
import android.util.Log;

import com.maksym.findthis.Utils.Constants;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.AKAZE;
import org.opencv.features2d.BRISK;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.GFTTDetector;
import org.opencv.features2d.KAZE;
import org.opencv.features2d.MSER;
import org.opencv.features2d.ORB;
import org.opencv.features2d.SIFT;
import org.opencv.imgproc.Imgproc;

public class DetectionEngine {
    private static final String TAG = DetectionEngine.class.getSimpleName();


    static {
        System.loadLibrary("opencv_java3");

        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }


    private static DetectionEngine instance;

    public synchronized static DetectionEngine getInstance() {
        if (instance == null)
            instance = new DetectionEngine();
        return instance;
    }



    public void drawKeypoints(int detector_id, Mat inputImage){
        MatOfKeyPoint keyPoints = detectFeatures(detector_id, inputImage);

        Features2d.drawKeypoints(inputImage, keyPoints, inputImage);
    }
    public void drawKeypoints(int detector_id, Bitmap inputImage){
        MatOfKeyPoint keyPoints = detectFeatures(detector_id, inputImage);

        Mat mat = new Mat();
        Utils.bitmapToMat(inputImage, mat);
        Features2d.drawKeypoints(mat, keyPoints, mat);
        Utils.matToBitmap(mat, inputImage);
    }





    public MatOfKeyPoint detectFeatures(int detector_id, Mat inputImage){
        return doDetection(selectDetector(detector_id), inputImage);
    }

    public MatOfKeyPoint detectFeatures(int detector_id, Bitmap inputImage){
        Mat mat = new Mat();
        Utils.bitmapToMat(inputImage, mat);

        return doDetection(selectDetector(detector_id), mat);
    }




    private Feature2D selectDetector(int detector_id){
        switch (detector_id){
            case Constants.AKAZE_DETECTOR_ID:
                return AKAZE.create();
            case Constants.BRISK_DETECTOR_ID:
                return BRISK.create();
            case Constants.FAST_DETECTOR_ID:
                return FastFeatureDetector.create();
            case Constants.GFTT_DETECTOR_ID:
                return GFTTDetector.create();
            case Constants.KAZE_DETECTOR_ID:
                return KAZE.create();
            case Constants.MSER_DETECTOR_ID:
                return MSER.create();
            case Constants.ORB_DETECTOR_ID:
                return ORB.create();
            case Constants.SIFT_DETECTOR_ID:
                return SIFT.create();
            default:
                return null;

        }
    }



    private MatOfKeyPoint doDetection(Feature2D detector, Mat inputImage){

        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        Imgproc.cvtColor(inputImage, inputImage, Imgproc.COLOR_RGBA2GRAY);
        detector.detect(inputImage, keyPoints);

        return keyPoints;
    }







}
