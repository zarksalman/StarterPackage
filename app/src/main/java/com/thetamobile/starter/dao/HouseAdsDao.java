package com.thetamobile.starter.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.thetamobile.starter.entities.HouseAds;

import java.util.List;


@Dao
public interface HouseAdsDao {

    @Query("SELECT COUNT(*) FROM HouseAds")
    int getCount();

    @Query("SELECT * FROM HouseAds ORDER BY priority DESC")
    List<HouseAds> getAll();

    @Query("SELECT * FROM HOUSEADS WHERE id = :id LIMIT 1")
    HouseAds findById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(HouseAds houseAds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<HouseAds> houseAds);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(HouseAds houseAds);

    @Delete
    void delete(HouseAds houseAds);
}
