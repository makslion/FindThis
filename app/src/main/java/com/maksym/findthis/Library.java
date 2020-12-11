package com.maksym.findthis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.maksym.findthis.Components.ObjectsRecyclerAdapter;
import com.maksym.findthis.Database.ObjectViewModel;

public class Library extends AppCompatActivity {
    private ObjectViewModel objectViewModel;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        initializeVariables();
    }

    private void initializeVariables(){
        objectViewModel = new ViewModelProvider(this).get(ObjectViewModel.class);

        recyclerView = findViewById(R.id.collectionRecycler);
        ObjectsRecyclerAdapter adapter = new ObjectsRecyclerAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        objectViewModel.getAllObjects().observe(this, objectEntities -> {
            // Update the cached copy of the objects in the adapter.
            adapter.setObjects(objectEntities);
        });
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