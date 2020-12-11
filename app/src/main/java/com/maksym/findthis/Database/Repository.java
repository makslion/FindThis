package com.maksym.findthis.Database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Repository {
    private ObjectDAO objectDAO;
    private String TAG = getClass().getSimpleName();


    public Repository(Application application) {
        Log.d(TAG, "constructor");
        ObjectRoomDatabase db = ObjectRoomDatabase.getDatabase(application);
        objectDAO = db.objectDao();
    }


    // general
    public void insertObject(ObjectEntity object) {
        ObjectRoomDatabase.databaseWriteExecutor.execute(() -> objectDAO.insert(object));
    }

    public void deleteAll(){
        ObjectRoomDatabase.databaseWriteExecutor.execute(() -> objectDAO.deleteAll());
    }


    // getters
    public LiveData<List<ObjectEntity>> getAllObjects(){
        return objectDAO.getAllObjects();
    }

    public LiveData<List<ObjectEntity>> getObjectById(int objectId){
        return objectDAO.getObjectById(objectId);
    }

    public LiveData<List<ObjectEntity>> getObjectByName(String objectName){
        return objectDAO.getObjectByName(objectName);
    }


    // deletion
    public void deleteObjectById(int objectId){
        ObjectRoomDatabase.databaseWriteExecutor.execute(() -> objectDAO.deleteObjectById(objectId));
    }

    public void deleteObjectByName(String objectName){
        ObjectRoomDatabase.databaseWriteExecutor.execute(() -> objectDAO.deleteObjectByName(objectName));
    }


    // updates
    public void updateNameAndDetector (String newObjectName, String newDetectorType){
        objectDAO.updateNameAndDetector(newObjectName, newDetectorType);
    }

    public void updateImage(String newImageName){
        objectDAO.updateImage(newImageName);
    }



}
