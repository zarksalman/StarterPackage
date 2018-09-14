package com.thetamobile.starter.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.thetamobile.starter.Application;
import com.thetamobile.starter.R;
import com.thetamobile.starter.dao.HouseAdsDao;
import com.thetamobile.starter.entities.HouseAds;


@Database(entities = {HouseAds.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase appDatabase;

    public abstract HouseAdsDao houseAdsDao();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }

    public static AppDatabase getAppDatabase() {
        if (appDatabase == null) {
            Context context = Application.getContext();
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, context.getString(R.string.app_name)).build();
        }
        return appDatabase;
    }
}
