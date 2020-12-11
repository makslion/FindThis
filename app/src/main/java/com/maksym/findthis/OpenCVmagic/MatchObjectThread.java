package com.maksym.findthis.OpenCVmagic;

import android.util.Log;

import com.maksym.findthis.Components.MatchObjectsCallback;
import com.maksym.findthis.Utils.Constants;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Feature2D;

import java.util.ArrayList;
import java.util.List;

public class MatchObjectThread extends Thread {
    private String TAG = getClass().getSimpleName();
    private Mat object, objectInScene;
    private Feature2D detector;
    private MatchObjectsCallback callback;

    public MatchObjectThread(Mat object, Mat objectInScene, Feature2D detector, MatchObjectsCallback callback) {
        this.object = object;
        this.objectInScene = objectInScene;
        this.detector = detector;
        this.callback = callback;
    }

    @Override
    public void run() {
        Log.d(TAG, "trying to match...");

        //-- Step 1: Detect the keypoints using detector, compute the descriptors
        MatOfKeyPoint keypointsObject = new MatOfKeyPoint();
        MatOfKeyPoint keypointsScene = new MatOfKeyPoint();

        Mat descriptorsObject = new Mat();
        Mat descriptorsScene = new Mat();

        detector.detectAndCompute(object, new Mat(), keypointsObject, descriptorsObject);
        detector.detectAndCompute(objectInScene, new Mat(), keypointsScene, descriptorsScene);


        //-- Step 2: Matching descriptor vectors with a FLANN based matcher
        // Since SURF is a floating-point descriptor NORM_L2 is used
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        List<MatOfDMatch> knnMatches = new ArrayList<>();

        matcher.knnMatch(descriptorsObject, descriptorsScene, knnMatches, 2);

        //-- Filter matches using the Lowe's ratio test
        List<DMatch> listOfGoodMatches = new ArrayList<>();

        for (int i = 0; i < knnMatches.size(); i++) {
            if (knnMatches.get(i).rows() > 1) {
                DMatch[] matches = knnMatches.get(i).toArray();
                if (matches[0].distance < Constants.RATIO_THRESHOLD * matches[1].distance) {
                    listOfGoodMatches.add(matches[0]);
                }
            }
        }
        double mathcesRatio = (double) listOfGoodMatches.size() / knnMatches.size();
        if (mathcesRatio > Constants.MATCHED_FEATURES_THRESHOLD) {
            MatOfDMatch goodMatches = new MatOfDMatch();
            goodMatches.fromList(listOfGoodMatches);

//        //-- Draw matches
//        Mat imgMatches = new Mat();
//        Features2d.drawMatches(imgObject, keypointsObject, imgScene, keypointsScene, goodMatches, imgMatches, Scalar.all(-1),
//                Scalar.all(-1), new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS);

            //-- Localize the object
            List<Point> obj = new ArrayList<>();
            List<Point> scene = new ArrayList<>();
            List<KeyPoint> listOfKeypointsObject = keypointsObject.toList();
            List<KeyPoint> listOfKeypointsScene = keypointsScene.toList();

            for (int i = 0; i < listOfGoodMatches.size(); i++) {
                //-- Get the keypoints from the good matches
                obj.add(listOfKeypointsObject.get(listOfGoodMatches.get(i).queryIdx).pt);
                scene.add(listOfKeypointsScene.get(listOfGoodMatches.get(i).trainIdx).pt);
            }

            MatOfPoint2f objMat = new MatOfPoint2f(), sceneMat = new MatOfPoint2f();
            objMat.fromList(obj);
            sceneMat.fromList(scene);
            double ransacReprojThreshold = 3.0;
            Mat H = Calib3d.findHomography(objMat, sceneMat, Calib3d.RANSAC, ransacReprojThreshold);

            Log.d(TAG, "Done calculations!");
            Log.d(TAG, "good matches size: " + goodMatches.toList().size());

            callback.matchObjectsCallback(H, true);
        }
        else {
            callback.matchObjectsCallback(null, false);
        }
    }
}
