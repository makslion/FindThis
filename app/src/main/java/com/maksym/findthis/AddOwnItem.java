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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.maksym.findthis.OpenCVmagic.DetectionMagic;

public class AddOwnItem extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    ImageView imageView;
    Bitmap rawImage, processedImage;
    private static final int CAMERA_REQUEST = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    DetectionMagic detectionMagic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_own_item);

        findReferences();
        takePhoto();
    }


    private void findReferences(){
        imageView = findViewById(R.id.capturedImage);


        detectionMagic = new DetectionMagic();
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

            imageView.setImageBitmap(rawImage);

            processedImage = detectionMagic.sift(rawImage);
        }
    }



    public void magicButtonListener(View view){
        if (rawImage != null) {

            Log.d("THE_THING","Received, setting!");

            //imageView.setImageBitmap(rawImage);
            takePhoto();

        }
    }
}