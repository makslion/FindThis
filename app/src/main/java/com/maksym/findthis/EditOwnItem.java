package com.maksym.findthis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.maksym.findthis.Database.ObjectEntity;
import com.maksym.findthis.Database.ObjectViewModel;
import com.maksym.findthis.Utils.Constants;
import com.maksym.findthis.Utils.ImageSaver;

public class EditOwnItem extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private EditText objectNameEditText;
    private Bitmap objectImage;
    private ObjectViewModel objectViewModel;
    private String detectorType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_own_item);

        Intent intent = getIntent();
        objectImage = intent.getParcelableExtra(Constants.EXTRA_BITMAP);
        detectorType = Constants.BIG_DETECTORS[intent.getIntExtra(Constants.EXTRA_DETECTOR, 4)]; ////////////////////// TODO default detector here
        objectViewModel = new ViewModelProvider(this).get(ObjectViewModel.class);

        findReferences();
    }


    private void findReferences(){
        objectNameEditText = findViewById(R.id.objectNameEditText);
    }

    public void editPreviewButtonListener (View view){
        ;//
    }

    public void retakeImageButtonListener (View view){
        Intent intent = new Intent(this, AddOwnItem.class);
        startActivity(intent);
    }


    public void addToCollectionButtonListener (View view){
        String objectName = objectNameEditText.getText().toString();

        if (!objectName.equals(""))
            storeObject();
        else {
            ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.red));
            objectNameEditText.setBackgroundTintList(colorStateList);
            Toast.makeText(this, "Enter object name", Toast.LENGTH_SHORT).show();
        }
    }


    public void discardButtonListener (View view){
        goToMain();
    }


    private void storeObject(){
        String objectName = objectNameEditText.getText().toString();
        String imageName = objectName+".png";                //////////////////////////////////////////// TODO image name here
        objectViewModel.insertObject(new ObjectEntity(
                objectName,
                detectorType,
                imageName
        ));

        Log.d(TAG,"saving image with dimensions: "+objectImage.getWidth()+"*"+objectImage.getHeight());
        ImageSaver imageSaver = new ImageSaver(this);
        imageSaver.setFileName(imageName);
        imageSaver.save(objectImage);

        goToMain();
    }

    private void goToMain(){
        Intent intent = new Intent(this, Library.class);
        startActivity(intent);
    }
}