package demo.treasure.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Created by dx on 2023/12/19.
 */
@Database(entities = {Record.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "treasure";

    public abstract RecordDao recordDao();

    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
