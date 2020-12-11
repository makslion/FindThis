package com.maksym.findthis.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ObjectDAO {

    @Insert
    void insert(ObjectEntity objectEntity);

    @Query("DELETE FROM object_table")
    void deleteAll();


    // getters
    @Query("SELECT * FROM object_table")
    LiveData<List<ObjectEntity>> getAllObjects();

    @Query("SELECT * FROM object_table WHERE id = :objectId")
    LiveData<List<ObjectEntity>> getObjectById(int objectId);

    @Query("SELECT * FROM object_table WHERE objectName = :objectName")
    LiveData<List<ObjectEntity>> getObjectByName(String objectName);


    // deletion
    @Query("DELETE FROM object_table WHERE id = :objectId")
    void deleteObjectById(int objectId);

    @Query("DELETE FROM object_table WHERE objectName = :objectName")
    void deleteObjectByName(String objectName);


    // updates
    @Query("UPDATE object_table SET objectName = :newObjectName, detectorType = :newDetectorType")
    void updateNameAndDetector (String newObjectName, String newDetectorType);

    @Query("UPDATE object_table SET imageName = :newImageName")
    void updateImage(String newImageName);

}
