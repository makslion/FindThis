package com.maksym.findthis.OpenCVmagic;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.SIFT;
import org.opencv.imgproc.Imgproc;

public class DetectionMagic {

    private static final String TAG = "THE_THING";


    static {
        System.loadLibrary("opencv_java3");

//        //System.loadLibrary("nonfree");
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    public DetectionMagic() {

    }

    private SIFT detector = SIFT.create();
    //private ORB detector = ORB.create();

    public Bitmap sift(Bitmap inputImage) {
        Mat rgba = new Mat();
        Utils.bitmapToMat(inputImage, rgba);
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY);
        detector.detect(rgba, keyPoints);
        Features2d.drawKeypoints(rgba, keyPoints, rgba);
        Utils.matToBitmap(rgba, inputImage);
        Log.d(TAG, "Done sift draw features");

        return inputImage;

//        int whichDescriptor = siftDescriptor; //freakDescriptor;
//        // Features SEARCH
//        int detectorType = FeatureDetector.SIFT;
//        FeatureDetector detector = FeatureDetector.create(detectorType);
//        Mat mask = new Mat();
//        MatOfKeyPoint keypoints = new MatOfKeyPoint();
//        detector.detect(image, keypoints , mask);
//        if (!detector.empty()){
//            // Draw kewpoints
//            Mat outputImage = new Mat();
//            Scalar color = new Scalar(0, 0, 255); // BGR
//            int flags = Features2d.DRAW_RICH_KEYPOINTS; // For each keypoint, the circle around keypoint with keypoint size and orientation will be drawn.
//            Features2d.drawKeypoints(image, keypoints, outputImage, color , flags);
//            displayImage(Mat2BufferedImage(outputImage), "Feautures_"+detectorType);
//        }



    }

}
