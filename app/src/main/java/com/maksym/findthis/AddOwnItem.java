package com.maksym.findthis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.maksym.findthis.OpenCVmagic.DetectionEngine;
import com.maksym.findthis.OpenCVmagic.DetectionMagic;
import com.maksym.findthis.Utils.Constants;

public class AddOwnItem extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private ImageView imageView;
    private Bitmap rawImage, processedImage;
    private Spinner detectorsSpinner;
    private static final int CAMERA_REQUEST = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private DetectionMagic detectionMagic;
    private DetectionEngine detectionEngine;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_own_item);

        findReferences();
        takePhoto();
    }


    private void findReferences(){
        imageView = findViewById(R.id.capturedImage);
        detectorsSpinner = findViewById(R.id.detectorsSpinner);

        detectionMagic = new DetectionMagic();
        detectionEngine = DetectionEngine.getInstance();

        populateSpinner();
    }


    private void populateSpinner(){
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, Constants.BIG_DETECTORS);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        detectorsSpinner.setAdapter(arrayAdapter);
        detectorsSpinner.setSelection(4);//////////////////////////////////////////////////////////////////////////////////// TODO set default detector

        detectorsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        displayKeypoints(Constants.AKAZE_DETECTOR_ID);
                        break;
                    case 1:
                        displayKeypoints(Constants.BRISK_DETECTOR_ID);
                        break;
                    case 2:
                        displayKeypoints(Constants.MSER_DETECTOR_ID);
                        break;
                    case 3:
                        displayKeypoints(Constants.ORB_DETECTOR_ID);
                        break;
                    case 4:
                    default:
                        displayKeypoints(Constants.SIFT_DETECTOR_ID);/////////////////////////////////////////////////////// TODO set default detector
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }



    private void displayKeypoints(int detectorID){
        if (rawImage != null) {
            imageView.setImageBitmap(null);
            Log.d(TAG, "displaying keypoints with detector id "+detectorID);
            processedImage = rawImage.copy(rawImage.getConfig(), true);

            detectionEngine.drawKeypoints(detectorID, processedImage);
            imageView.setImageBitmap(processedImage);

        }
        else
            Log.d(TAG, "raw image is null");
    }


    private void takePhoto(){


        try {
            Log.d(TAG, "attempt to start camera");

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
            else
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }

            Log.d(TAG, "activity started");
        } catch (ActivityNotFoundException e) {

            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

            Log.d(TAG, e.toString());
        }
  }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "on activity result:");
        Log.d(TAG, "\t request code "+requestCode);
        Log.d(TAG, "\t result code "+resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            rawImage = (Bitmap) extras.get("data");

            Log.d(TAG, "captured image dimensions: "+rawImage.getWidth()+"*"+rawImage.getHeight());
            imageView.setImageBitmap(rawImage);
            displayKeypoints(Constants.SIFT_DETECTOR_ID);/////////////////////////////////////////////////////////////////////// TODO use default detector

        }
    }



    public void retakeButtonListener(View view){


        Log.d(TAG,"Received, setting!");

        //imageView.setImageBitmap(rawImage);
        takePhoto();


    }


    public void cropButtonListener(View view){

    }


    public void nextButtonListener(View view){
        Intent intent = new Intent(this, EditOwnItem.class);
        intent.putExtra(Constants.EXTRA_BITMAP, rawImage);
        intent.putExtra(Constants.EXTRA_DETECTOR, detectorsSpinner.getSelectedItemPosition());
        startActivity(intent);
    }



}