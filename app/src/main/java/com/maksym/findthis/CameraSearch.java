package com.maksym.findthis;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.maksym.findthis.Components.MatchObjectsCallback;
import com.maksym.findthis.Database.ObjectEntity;
import com.maksym.findthis.OpenCVmagic.CustomCameraBridgeViewBase;
import com.maksym.findthis.OpenCVmagic.DetectionEngine;
import com.maksym.findthis.OpenCVmagic.DetectionMagic;
import com.maksym.findthis.Utils.Constants;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class CameraSearch extends AppCompatActivity implements CustomCameraBridgeViewBase.CvCameraViewListener, MatchObjectsCallback {

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    /* Now enable camera view to start receiving frames */
                    mOpenCvCameraView.enableView();
                    Log.d(TAG, "enabled view");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    private String TAG = "fucking_fuck_fuck";
    private final int CAMERA_PERMISSION_CODE = 100;
    private CustomCameraBridgeViewBase mOpenCvCameraView;

    private DetectionMagic detectionMagic = new DetectionMagic();

    private ObjectEntity objectEntity;
    private Bitmap objectPhoto;
    private boolean detectorOperating, objectReceived = false;
    private Mat inputFrameWithObject;
    private DetectionEngine detectionEngine = DetectionEngine.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera_search);
        mOpenCvCameraView = (CustomCameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG,"no permissions");
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
        else
        {
            Log.d(TAG, "had permissions");
            mOpenCvCameraView.setCameraPermissionGranted();
        }

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        initializeVariables();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.d(TAG, "finally granted");
                mOpenCvCameraView.setCameraPermissionGranted();
            }
            else
            {
                Log.d(TAG, "not granted after request");
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        //Log.d(TAG,"on camera frame");
//        DetectionEngine.getInstance().drawKeypoints(Constants.AKAZE_DETECTOR_ID, inputFrame);
//        DetectionEngine.getInstance().drawKeypoints(Constants.BRISK_DETECTOR_ID, inputFrame);
//        DetectionEngine.getInstance().drawKeypoints(Constants.FAST_DETECTOR_ID, inputFrame);
//        DetectionEngine.getInstance().drawKeypoints(Constants.GFTT_DETECTOR_ID, inputFrame);
//        DetectionEngine.getInstance().drawKeypoints(Constants.KAZE_DETECTOR_ID, inputFrame);
//        DetectionEngine.getInstance().drawKeypoints(Constants.MSER_DETECTOR_ID, inputFrame);
//        DetectionEngine.getInstance().drawKeypoints(Constants.ORB_DETECTOR_ID, inputFrame);
//        DetectionEngine.getInstance().drawKeypoints(Constants.SIFT_DETECTOR_ID, inputFrame);

        long start = System.nanoTime();
        if (!detectorOperating){
            detectorOperating = true;
            Mat objectMat = new Mat();
            Utils.bitmapToMat(objectPhoto, objectMat);
            detectionEngine.matchObjectsThread(objectMat, inputFrame, detectionEngine.selectDetector(objectEntity.getDetectorType()), this);
        }
        if (objectReceived) {

            detectionEngine.matchObjects(inputFrameWithObject, inputFrame, detectionEngine.selectTracker(Constants.ORB_DETECTOR_ID));

        }

//        Mat objectMat = new Mat();
//        Utils.bitmapToMat(objectPhoto, objectMat);
//        Utils.bitmapToMat(objectPhoto, objectMat);
//        detectionEngine.matchObjects(objectMat, inputFrame, detectionEngine.selectDetector(objectEntity.getDetectorType()));

        long end = System.nanoTime();
        double frameDelivered = (double) (end - start)/1000000000;
        double framesPerSecond = 1/frameDelivered;

        if (framesPerSecond < 200) {
            Log.d("FPS", "Frame in: " + frameDelivered);
            Log.d("FPS", "Frames per second: " + framesPerSecond);
        }
        return inputFrame;
    }



    private void initializeVariables(){
        Intent intent = getIntent();

        if (intent.hasExtra(Constants.EXTRA_BITMAP) && intent.hasExtra(Constants.EXTRA_OBJECT)){
            Log.d(TAG, "retrieving object details");
            objectEntity = (ObjectEntity) intent.getSerializableExtra(Constants.EXTRA_OBJECT);
            objectPhoto = intent.getParcelableExtra(Constants.EXTRA_BITMAP);
        }

    }

    @Override
    public void matchObjectsCallback(float [] sceneCornersData, Mat frame, boolean found) {
        if (found) {
            Log.d(TAG, "match found!");

            int rightTopX = Math.round(sceneCornersData[2]);
            int rightTopY = Math.round(sceneCornersData[3]);
            int letBotX =  Math.round(sceneCornersData[6]);
            int letBotY =  Math.round(sceneCornersData[7]);

            //if out of frame set to 0
            if (rightTopX < 0)
                rightTopX = 0;
            if (rightTopY < 0)
                rightTopY = 0;
            if (letBotX < 0)
                letBotX = 0;
            if (letBotY < 0)
                letBotY = 0;

            int width = letBotX - rightTopX;
            int height = letBotY - rightTopY;

            // check if trying to crop out of bounds
            if (width + rightTopX > frame.cols())
                width = frame.cols()-rightTopX;
            if (height + rightTopY > frame.rows() - rightTopY)
                height = frame.rows()-rightTopY;

            Log.d(TAG, "Frame dimensions: "+frame.cols()+"*"+frame.rows());
            Log.d(TAG, "r t x: "+rightTopX);
            Log.d(TAG, "r t y: "+rightTopY);
            Log.d(TAG, "l b x: "+letBotX);
            Log.d(TAG, "l b y: "+letBotY);
            Log.d(TAG, "width: "+width);
            Log.d(TAG, "height: "+height);


            Rect roi = new Rect(rightTopX, rightTopY, width, height);
            inputFrameWithObject = new Mat(frame, roi);

            objectReceived = true;
        }
        else {
            Log.d(TAG, "No match!");
            objectReceived = false;
        }
        detectorOperating = false;
    }
}