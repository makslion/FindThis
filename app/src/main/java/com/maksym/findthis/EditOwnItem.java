package com.maksym.findthis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditOwnItem extends AppCompatActivity {

    private EditText objectNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_own_item);

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
        // TODO database things
        goToMain();
    }

    private void goToMain(){
        Intent intent = new Intent(this, Library.class);
        startActivity(intent);
    }
}