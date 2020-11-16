package com.maksym.findthis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Library extends AppCompatActivity {
    private Button cameraTest, addOwn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        cameraTest = findViewById(R.id.cameraTest);

        addOwn = findViewById(R.id.addOwnItemButton);


    }


    public void cameraTest(View view){
        Intent it = new Intent(this, CameraSearch.class);
        startActivity(it);
    }


    public void addOwnItem (View view){
        Intent it = new Intent(this, AddOwnItem.class);
        startActivity(it);
    }
}