package com.maksym.findthis.Database;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//SQLite thing for chord table
@Entity(tableName = "object_table")
public class ObjectEntity implements Serializable { //TODO implement Parcelable instead

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "objectName")
    private String objectName;

    @NonNull
    @ColumnInfo(name = "detectorType")
    private String detectorType;

    @NonNull
    @ColumnInfo(name = "imageName")
    private String imageName;

    public ObjectEntity(@NonNull String objectName, @NonNull String detectorType, @NonNull String imageName) {
        this.objectName = objectName;
        this.detectorType = detectorType;
        this.imageName = imageName;
    }


    @NonNull
    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(@NonNull String objectName) {
        this.objectName = objectName;
    }

    @NonNull
    public String getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(@NonNull String detectorType) {
        this.detectorType = detectorType;
    }

    @NonNull
    public String getImageName() {
        return imageName;
    }

    public void setImageName(@NonNull String imageName) {
        this.imageName = imageName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
