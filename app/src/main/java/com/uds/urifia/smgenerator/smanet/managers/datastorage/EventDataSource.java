package com.uds.urifia.smgenerator.smanet.managers.datastorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.uds.urifia.smgenerator.smanet.model.Event;
import com.uds.urifia.smgenerator.smanet.model.Subject;
import com.uds.urifia.smgenerator.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;

public class EventDataSource {
    private SQLiteDatabase database;
    private SManetDBHelper dbHelper;

    public EventDataSource(Context ctx) {
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

    public long insert(Event e) {
        ContentValues values = new ContentValues();
        values.put(SManetDataContract.Event.COLUMN_NAME_ID, e.getEventId());
        values.put(SManetDataContract.Event.COLUMN_NAME_SID, e.getSubjectId());
        values.put(SManetDataContract.Event.COLUMN_NAME_PUBDATE, e.getEventDate().toString());
        values.put(SManetDataContract.Event.COLUMN_NAME_VALIDITY, e.getValidity());
        values.put(SManetDataContract.Event.COLUMN_NAME_DESCRIPTION, e.getDescription());
        values.put(SManetDataContract.Event.COLUMN_NAME_PATH, e.getPath());

        // Insert the new row, returning the primary key value of the new row
        long id = database.insert(SManetDataContract.Event.TABLE_NAME, null, values);
        return id;
    }

    public Event find(String id) {
        // Filter results WHERE "e_id" = id
        String selection = SManetDataContract.Event.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = { id };
        Cursor cursor =
                database.query(SManetDataContract.Event.TABLE_NAME, null, selection, selectionArgs,null,null, null);
        Event event = null;
        if (cursor.moveToFirst()) {
            event = cursorToEvent(cursor);
        }
        cursor.close();

        return event;
    }

    public ArrayList<Event> findAllBySubject(Subject s) {
        // Filter results WHERE "e_sid" = s.getId()
        String selection = SManetDataContract.Event.COLUMN_NAME_SID + " = ?";
        String[] selectionArgs = { s.getId() };
        Cursor cursor =
                database.query(SManetDataContract.Event.TABLE_NAME, null, selection, selectionArgs,null,null, null);

        ArrayList<Event> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(cursorToEvent(cursor));
        }
        cursor.close();

        return results;
    }

    public ArrayList<Event> findAll() {
        Cursor cursor =
                database.query(SManetDataContract.Event.TABLE_NAME, null, null, null,null,null, null);

        ArrayList<Event> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(cursorToEvent(cursor));
        }
        cursor.close();

        return results;
    }

    public int delete(Event e) {
        // Define 'where' part of query.
        String selection = SManetDataContract.Event.COLUMN_NAME_ID+ " = ?";
        String[] selectionArgs = { e.getEventId() };
        // Issue SQL statement.
        int deletedRows = database.delete(SManetDataContract.Event.TABLE_NAME, selection, selectionArgs);
        return deletedRows;
    }

    private Event cursorToEvent(Cursor cursor) {
        String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(SManetDataContract.Event.COLUMN_NAME_PUBDATE));
        Date date = DateUtil.dateFromString(dateStr);
        int indexId = cursor.getColumnIndexOrThrow(SManetDataContract.Event.COLUMN_NAME_ID);
        int indexSId = cursor.getColumnIndexOrThrow(SManetDataContract.Event.COLUMN_NAME_SID);
        int indexVal = cursor.getColumnIndexOrThrow(SManetDataContract.Event.COLUMN_NAME_VALIDITY);
        int indexDescr = cursor.getColumnIndexOrThrow(SManetDataContract.Event.COLUMN_NAME_DESCRIPTION);
        int indexPath = cursor.getColumnIndexOrThrow(SManetDataContract.Event.COLUMN_NAME_PATH);
        Event e = new Event(
                cursor.getString(indexId),
                cursor.getString(indexSId),
                date,
                cursor.getLong(indexVal),
                cursor.getString(indexDescr),
                cursor.getString(indexPath)
        );
        return e;
    }
}
