package com.uds.urifia.smgenerator.smanet.managers.datastorage;

import android.provider.BaseColumns;

public final class SManetDataContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private SManetDataContract() {}

    /* Inner class that defines the table contents */
    public static class Subscription implements BaseColumns {
        public static final String TABLE_NAME = "subscription";
        public static final String COLUMN_NAME_ID = "s_id";
        public static final String COLUMN_NAME_CODE = "s_code";
        public static final String COLUMN_NAME_NAME = "s_name";
        public static final String COLUMN_NAME_DESCRIPTION = "s_description";
    }

    /* Inner class that defines the table contents */
    public static class Event implements BaseColumns {
        public static final String TABLE_NAME = "event";
        public static final String COLUMN_NAME_ID = "e_id";
        public static final String COLUMN_NAME_SID = "e_sid";
        public static final String COLUMN_NAME_PUBDATE = "e_pub_date";
        public static final String COLUMN_NAME_VALIDITY = "e_validity";
        public static final String COLUMN_NAME_DESCRIPTION = "e_description";
        public static final String COLUMN_NAME_PATH = "e_path";
    }

    // Some scripts to create and delete the database
    public static final String SQL_CREATE_SUBSCRIPTION_TABLE =
            "CREATE TABLE " + Subscription.TABLE_NAME + " (" +
                    Subscription._ID + " INTEGER PRIMARY KEY," +
                    Subscription.COLUMN_NAME_ID + " TEXT," +
                    Subscription.COLUMN_NAME_CODE + " TEXT," +
                    Subscription.COLUMN_NAME_NAME + " TEXT," +
                    Subscription.COLUMN_NAME_DESCRIPTION + " TEXT" +
            ");";
    public static final String SQL_CREATE_EVENT_TABLE =
            "CREATE TABLE " + Event.TABLE_NAME + " (" +
                    Event._ID + " INTEGER PRIMARY KEY," +
                    Event.COLUMN_NAME_ID + " TEXT," +
                    Event.COLUMN_NAME_SID + " TEXT," +
                    Event.COLUMN_NAME_PUBDATE + " TEXT," +
                    Event.COLUMN_NAME_VALIDITY + " INTEGER," +
                    Event.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    Event.COLUMN_NAME_PATH + " TEXT" +
             ");";

    public static final String SQL_DELETE_SUBSCRIPTION_TABLE = "DROP TABLE IF EXISTS " + Subscription.TABLE_NAME + ";";
    public static final String SQL_DELETE_EVENT_TABLE = "DROP TABLE IF EXISTS " + Event.TABLE_NAME + ";";

}

