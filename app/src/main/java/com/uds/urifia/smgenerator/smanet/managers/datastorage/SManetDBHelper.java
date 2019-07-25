package com.uds.urifia.smgenerator.smanet.managers.datastorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SManetDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    // The database name
    private static final String DATABASE_NAME = "smanet_db";

    public SManetDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SManetDataContract.SQL_CREATE_SUBSCRIPTION_TABLE);
        db.execSQL(SManetDataContract.SQL_CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        clearDatabase(db);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void clearDatabase(SQLiteDatabase db) {
        db.execSQL(SManetDataContract.SQL_DELETE_SUBSCRIPTION_TABLE);
        db.execSQL(SManetDataContract.SQL_DELETE_EVENT_TABLE);
    }

}
