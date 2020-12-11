package com.maksym.findthis.Database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.maksym.findthis.Utils.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ObjectEntity.class}, version = 1, exportSchema = false)
public abstract class ObjectRoomDatabase extends RoomDatabase {

    public abstract ObjectDAO objectDao();

    private static volatile ObjectRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static ObjectRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ObjectRoomDatabase.class) {
                if (INSTANCE == null) {
                    Log.d("ObjectRoomDatabase","building database");
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ObjectRoomDatabase.class, "objects_database")
                            // comment next line to avoid dummy data at install
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }



    /**
     * Override the onCreate method to populate the database.
     * For this sample, we clear the database every time it is created.
     */
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        private String TAG = "DBCallback";
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.d(TAG, "on create");

            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                ObjectDAO dao = INSTANCE.objectDao();
                dao.deleteAll();
                Log.d(TAG,"wiping database");

                // dummy objects to test layout and database queries
                ObjectEntity object = new ObjectEntity("Dummy object 1", Constants.BIG_DETECTORS[4],"non_existing_image.png");
                dao.insert(object);
                object =  new ObjectEntity("Dummy object 2", Constants.BIG_DETECTORS[3],"another_existing_image.png");
                dao.insert(object);
            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            Log.d(TAG, "on open");


//            databaseWriteExecutor.execute(() -> {
//                // Populate the database in the background.
//                ObjectDAO dao = INSTANCE.objectDao();
//                dao.deleteAll();
//                Log.d(TAG,"wiping database");
//
//                // dummy objects to test layout and database queries
//                ObjectEntity object = new ObjectEntity("Dummy object 1", Constants.BIG_DETECTORS[4],"non_existing_image.png");
//                dao.insert(object);
//                object =  new ObjectEntity("Dummy object 2", Constants.BIG_DETECTORS[3],"another_existing_image.png");
//                dao.insert(object);
//            });
        }
    };
}



