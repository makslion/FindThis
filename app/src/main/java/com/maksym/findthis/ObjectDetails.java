package com.maksym.findthis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maksym.findthis.Database.ObjectEntity;
import com.maksym.findthis.Database.ObjectViewModel;
import com.maksym.findthis.Utils.Constants;
import com.maksym.findthis.Utils.ImageSaver;

public class ObjectDetails extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private TextView objectNameView;
    private ImageView objectImageView;
    private ObjectEntity object;
    private Bitmap objectImage;
    private ObjectViewModel objectViewModel;
    private ImageSaver imageSaver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_details);

        initializeVariables();
    }

    private void initializeVariables(){
        imageSaver = new ImageSaver(this);
        objectViewModel = new ViewModelProvider(this).get(ObjectViewModel.class);


        Intent intent = getIntent();
        if (intent.hasExtra(Constants.EXTRA_OBJECT)) {
            object = (ObjectEntity) intent.getSerializableExtra(Constants.EXTRA_OBJECT);
            if (object != null) {
                Log.d(TAG, "found extra object: " + object.getObjectName());
                imageSaver.setFileName(object.getImageName());
            }
            else
                Log.d(TAG, "passed object is NULL!!!!!!!!!!!!!");
        }

        objectImageView = findViewById(R.id.objectDetailsImageView);
        objectNameView = findViewById(R.id.objectDetailsName);

        Log.d(TAG, "found references!");

        fillReferences();
    }


    private void fillReferences(){
        ImageSaver imageSaver = new ImageSaver(this);
        imageSaver.setFileName(object.getImageName());
        objectImage = imageSaver.load();

        objectImageView.setImageBitmap(objectImage);
        objectNameView.setText(object.getObjectName());
    }


    public void deleteObjectButtonListener(View view){
        Log.d(TAG,"deleting object: "+object.getObjectName());

        objectViewModel.deleteObjectById(object.getId());
        Log.d(TAG, "image deleted: " +imageSaver.deleteFile());

        Intent intent = new Intent(this, Library.class);
        startActivity(intent);
    }


    public void trackObjectButtonListener(View view){
        Intent intent = new Intent(this, CameraSearch.class);
        intent.putExtra(Constants.EXTRA_OBJECT, object);
        intent.putExtra(Constants.EXTRA_BITMAP, objectImage);
        startActivity(intent);
    }
}