package demo.treasure.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

/**
 * Created by dx on 2023/12/19.
 */
@Dao
public interface RecordDao {
    // 插入一个Record
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrUpdate(Record record);

    @Query("SELECT * FROM record ORDER BY date DESC")
    Flowable<List<Record>> getAllRecord();

    @Delete
    Completable deleteRecord(Record record);
}
