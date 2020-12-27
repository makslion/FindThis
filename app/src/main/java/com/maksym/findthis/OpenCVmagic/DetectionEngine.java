package com.maksym.findthis.OpenCVmagic;

import android.graphics.Bitmap;
import android.util.Log;

import com.maksym.findthis.Components.MatchObjectsCallback;
import com.maksym.findthis.Utils.Constants;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.AKAZE;
import org.opencv.features2d.BRISK;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.GFTTDetector;
import org.opencv.features2d.KAZE;
import org.opencv.features2d.MSER;
import org.opencv.features2d.ORB;
import org.opencv.features2d.SIFT;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class DetectionEngine extends Thread {
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
                return null; /////////////////////////////////////////////// TODO default detector!!

        }
    }
    // {"AKAZE","BRISK","MSER","ORB","SIFT"};
    public Feature2D selectDetector (String detectorName){

        if (detectorName.equals(Constants.BIG_DETECTORS[0]))
            return AKAZE.create();
        else if(detectorName.equals(Constants.BIG_DETECTORS[1]))
            return BRISK.create();
        else if(detectorName.equals(Constants.BIG_DETECTORS[2]))
            return MSER.create();
        else if(detectorName.equals(Constants.BIG_DETECTORS[3]))
            return ORB.create();
        else if(detectorName.equals(Constants.BIG_DETECTORS[4]))
            return SIFT.create();
        else
            return SIFT.create();///////////////////// TODO default detector
    }

    // {"FAST","GFTT"};
    public Feature2D selectTracker(int trackerId){
        switch (trackerId){
            case Constants.GFTT_DETECTOR_ID:
                return GFTTDetector.create();
            case Constants.FAST_DETECTOR_ID:
                return FastFeatureDetector.create();
            case Constants.ORB_DETECTOR_ID:
            default:
                return ORB.create();
        }
    }



    private MatOfKeyPoint doDetection(Feature2D detector, Mat inputImage){

        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        Imgproc.cvtColor(inputImage, inputImage, Imgproc.COLOR_RGBA2GRAY);
        detector.detect(inputImage, keyPoints);

        return keyPoints;
    }



    public void matchObjects(Mat object, MatOfKeyPoint _keypointsObject, Mat _descriptorsObject, Mat objectInScene, /*Feature2D detector*/ Feature2D extractor, Feature2D descriptor){
        Log.d(TAG, "trying to match...");


        //-- Step 1: Detect the keypoints using detector, compute the descriptors
        MatOfKeyPoint keypointsObject = new MatOfKeyPoint();
        MatOfKeyPoint keypointsScene = new MatOfKeyPoint();

        extractor.detect(objectInScene, keypointsScene);
        extractor.detect(object, keypointsObject);

        Mat descriptorsObject = new Mat();
        Mat descriptorsScene = new Mat();

        descriptor.compute(objectInScene, keypointsScene, descriptorsScene);
        descriptor.compute(object, keypointsObject, descriptorsObject);
//        detector.detectAndCompute(object, new Mat(), keypointsObject, descriptorsObject);
//        detector.detectAndCompute(objectInScene, new Mat(), keypointsScene, descriptorsScene);


        //-- Step 2: Matching descriptor vectors with a FLANN based matcher
        // Since SURF is a floating-point descriptor NORM_L2 is used
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
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
        if (mathcesRatio > Constants.MATCHED_FEATURES_TRACKING_THRESHOLD) {

            MatOfDMatch goodMatches = new MatOfDMatch();
            goodMatches.fromList(listOfGoodMatches);

        //-- Draw matches
        //        Features2d.drawMatches(object, keypointsObject, objectInScene, keypointsScene, goodMatches, imgMatches, Scalar.all(-1),
//                Scalar.all(-1), new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS);

            //-- Localize the object
            List<Point> obj = new ArrayList<>();
            List<Point> scene = new ArrayList<>();
            List<KeyPoint> listOfKeypointsObject = keypointsObject.toList();
            List<KeyPoint> listOfKeypointsScene = keypointsScene.toList();

            List<KeyPoint> _listOfKeypointsObject = _keypointsObject.toList();


//            Log.d(TAG, "knnMatches size: "+knnMatches.size()+" listOfKeypointsScene size: "+listOfKeypointsScene.size());
            Log.d(TAG, "knnMatches size: "+knnMatches.size()+" _listOfKeypointsObject size: "+_listOfKeypointsObject.size());
            Log.d(TAG, "knnMatches size: "+knnMatches.size()+" listOfKeypointsObject size: "+listOfKeypointsObject.size());
            for (int i = 0; i < listOfGoodMatches.size(); i++) {
                //-- Get the keypoints from the good matches
//                Log.d(TAG, "listOfGoodMatchesSize: "+listOfGoodMatches.size()+" listOfKeypointsObject size: "+listOfKeypointsObject.size()+" index: "+i+" listOfGoodMatches.get(i).queryIdx: "+listOfGoodMatches.get(i).queryIdx);
                obj.add(listOfKeypointsObject.get(listOfGoodMatches.get(i).queryIdx).pt);
                scene.add(listOfKeypointsScene.get(listOfGoodMatches.get(i).trainIdx).pt);
            }

            MatOfPoint2f objMat = new MatOfPoint2f(), sceneMat = new MatOfPoint2f();
            objMat.fromList(obj);
            sceneMat.fromList(scene);
            double ransacReprojThreshold = 3.0;
            Mat H = Calib3d.findHomography(objMat, sceneMat, Calib3d.RANSAC, ransacReprojThreshold);
//            Mat fundamental = Calib3d.findFundamentalMat(objMat, sceneMat,Calib3d.RANSAC, ransacReprojThreshold );

            Log.d(TAG, "Done calculations!");
            Log.d(TAG, "good matches size: " + goodMatches.toList().size());



            //-- Get the corners from the image_1 ( the object to be "detected" )
            Mat objCorners = new Mat(4, 1, CvType.CV_32FC2), sceneCorners = new Mat();
            float[] objCornersData = new float[(int) (objCorners.total() * objCorners.channels())];
            objCorners.get(0, 0, objCornersData);
            objCornersData[0] = 0;
            objCornersData[1] = 0;
            objCornersData[2] = object.cols();
            objCornersData[3] = 0;
            objCornersData[4] = object.cols();
            objCornersData[5] = object.rows();
            objCornersData[6] = 0;
            objCornersData[7] = object.rows();
            objCorners.put(0, 0, objCornersData);
            Core.perspectiveTransform(objCorners, sceneCorners, H);
            float[] sceneCornersData = new float[(int) (sceneCorners.total() * sceneCorners.channels())];
            sceneCorners.get(0, 0, sceneCornersData);
            //-- Draw lines between the corners (the mapped object in the scene - image_2 )
            Imgproc.line(objectInScene, new Point(sceneCornersData[0], sceneCornersData[1]),
                    new Point(sceneCornersData[2], sceneCornersData[3]), new Scalar(0, 255, 0), 4);
            Imgproc.line(objectInScene, new Point(sceneCornersData[2], sceneCornersData[3]),
                    new Point(sceneCornersData[4], sceneCornersData[5]), new Scalar(0, 255, 0), 4);
            Imgproc.line(objectInScene, new Point(sceneCornersData[4], sceneCornersData[5]),
                    new Point(sceneCornersData[6], sceneCornersData[7]), new Scalar(0, 255, 0), 4);
            Imgproc.line(objectInScene, new Point(sceneCornersData[6], sceneCornersData[7]),
                    new Point(sceneCornersData[0], sceneCornersData[1]), new Scalar(0, 255, 0), 4);



//            testing
//            Imgproc.line(imgMatches,
//                    new Point(0,0),
//                    new Point(sceneCornersData[6], sceneCornersData[7] ),
//                    new Scalar(255, 255, 0), 4);

//            sceneCornersData[0], sceneCornersData[1]                                 // left top x left top y
//            sceneCornersData[2], sceneCornersData[3]                                 // right top x right top y
//
//            sceneCornersData[2], sceneCornersData[3]                                 //  r t x, r t y
//            sceneCornersData[4], sceneCornersData[5]                                 //  r b x, r b y
//
//            sceneCornersData[4], sceneCornersData[5]                                  //  r b x, r b y
//            sceneCornersData[6], sceneCornersData[7]                                  //  l b x, l b y
//
//            sceneCornersData[6], sceneCornersData[7]                                  //  l b x, l b y
//            sceneCornersData[0], sceneCornersData[1]                                  // l t x, l t y

//            Log.d(TAG, "drawing imgMatches");
//
            Log.d(TAG, "objects cols: "+object.cols()+" rows: "+object.rows());
            Log.d(TAG, "scene cols: "+objectInScene.cols()+" rows: "+objectInScene.rows());
            Log.d(TAG, "matches cols: "+ objectInScene.cols()+" rows: "+ objectInScene.rows());
            Log.d(TAG, "scene corners data: "+sceneCornersData[0]);
            Log.d(TAG, "scene corners data: "+sceneCornersData[1]);
            Log.d(TAG, "scene corners data: "+sceneCornersData[2]);
            Log.d(TAG, "scene corners data: "+sceneCornersData[3]);
            Log.d(TAG, "scene corners data: "+sceneCornersData[4]);
            Log.d(TAG, "scene corners data: "+sceneCornersData[5]);
            Log.d(TAG, "scene corners data: "+sceneCornersData[6]);
            Log.d(TAG, "scene corners data: "+sceneCornersData[7]);
        }
    }

    public void matchObjectsThread(Mat object, MatOfKeyPoint keypointsObject, Mat descriptorsObject, Mat objectInScene, Feature2D detector, MatchObjectsCallback callback){
        new MatchObjectThread(object, keypointsObject, descriptorsObject, objectInScene, detector, callback).start();
    }
}
