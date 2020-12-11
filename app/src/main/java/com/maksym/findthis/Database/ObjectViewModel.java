package com.maksym.findthis.Database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ObjectViewModel extends AndroidViewModel {

    private Repository repository;


    public ObjectViewModel (Application application) {
        super(application);
        repository = new Repository(application);
    }

    // general
    public void insertObject(ObjectEntity object) {
        repository.insertObject(object);
    }

    public void deleteAll(){
        repository.deleteAll();
    }


    // getters
    public LiveData<List<ObjectEntity>> getAllObjects(){
        return repository.getAllObjects();
    }

    public LiveData<List<ObjectEntity>> getObjectById(int objectId){
        return repository.getObjectById(objectId);
    }

    public LiveData<List<ObjectEntity>> getObjectByName(String objectName){
        return repository.getObjectByName(objectName);
    }


    // deletion
    public void deleteObjectById(int objectId){
        repository.deleteObjectById(objectId);
    }

    public void deleteObjectByName(String objectName){
        repository.deleteObjectByName(objectName);
    }


    // updates
    public void updateNameAndDetector (String newObjectName, String newDetectorType){
        repository.updateNameAndDetector(newObjectName, newDetectorType);
    }

    public void updateImage(String newImageName){
        repository.updateImage(newImageName);
    }
}