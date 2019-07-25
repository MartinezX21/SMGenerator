package com.uds.urifia.smgenerator.smanet.managers.datastorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.uds.urifia.smgenerator.smanet.model.Subject;

import java.util.ArrayList;

public class SubscriptionDataSource {
    private SQLiteDatabase database;
    private SManetDBHelper dbHelper;

    public SubscriptionDataSource(Context ctx) {
        this.database = null;
        this.dbHelper = new SManetDBHelper(ctx);
    }

    public void open() throws SQLException {
        if (this.database == null) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        database.close();
    }

    public long insert(Subject s) {
        ContentValues values = new ContentValues();
        values.put(SManetDataContract.Subscription.COLUMN_NAME_ID, s.getId());
        values.put(SManetDataContract.Subscription.COLUMN_NAME_CODE, s.getCode());
        values.put(SManetDataContract.Subscription.COLUMN_NAME_NAME, s.getName());
        values.put(SManetDataContract.Subscription.COLUMN_NAME_DESCRIPTION, s.getDescription());

        // Insert the new row, returning the primary key value of the new row
        long id = database.insert(SManetDataContract.Subscription.TABLE_NAME, null, values);
        return id;
    }

    public Subject find(String id) {
        // Filter results WHERE "s_id" = id
        String selection = SManetDataContract.Subscription.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { id };
        Cursor cursor =
                database.query(SManetDataContract.Subscription.TABLE_NAME, null, selection, selectionArgs,null,null, null);
        Subject s = null;
        if (cursor.moveToFirst()) {
            s = cursorToSubject(cursor);
        }
        cursor.close();

        return s;
    }

    public ArrayList<Subject> findAll() {
        Cursor cursor =
                database.query(SManetDataContract.Subscription.TABLE_NAME, null, null, null,null,null, null);

        ArrayList<Subject> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(cursorToSubject(cursor));
        }
        cursor.close();

        return results;
    }

    public int delete(Subject s) {
        // Define 'where' part of query.
        String selection = SManetDataContract.Subscription.COLUMN_NAME_ID+ " = ?";
        String[] selectionArgs = { s.getId() };
        // Issue SQL statement.
        int deletedRows = database.delete(SManetDataContract.Subscription.TABLE_NAME, selection, selectionArgs);
        return deletedRows;
    }

    private Subject cursorToSubject(Cursor cursor) {
        Subject s = new Subject(
                cursor.getString(cursor.getColumnIndexOrThrow(SManetDataContract.Subscription.COLUMN_NAME_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(SManetDataContract.Subscription.COLUMN_NAME_CODE)),
                cursor.getString(cursor.getColumnIndexOrThrow(SManetDataContract.Subscription.COLUMN_NAME_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(SManetDataContract.Subscription.COLUMN_NAME_DESCRIPTION))
        );
        return s;
    }
}
