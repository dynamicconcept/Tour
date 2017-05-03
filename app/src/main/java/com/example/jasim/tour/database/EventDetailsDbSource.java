package com.example.jasim.tour.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.jasim.tour.model.EventDetails;


public class EventDetailsDbSource {

    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] eventDetailsInfo = {DBHelper.COLUMN_EVENT_DETAILS_ID,
            DBHelper.COLUMN_EVENT_DETAILS_EVENT, DBHelper.COLUMN_EVENT_DETAILS_DETAILS,
            DBHelper.COLUMN_EVENT_DETAILS_ADD_TIME, DBHelper.COLUMN_EVENT_DETAILS_PICTURE,
            DBHelper.COLUMN_EVENT_DETAILS_USED};
    Context context;

    public EventDetailsDbSource(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public EventDetails addEventDetails(EventDetails eventDetails) {
        this.open();

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_EVENT_DETAILS_EVENT, eventDetails.getEventId());
        values.put(DBHelper.COLUMN_EVENT_DETAILS_DETAILS, eventDetails.getDetails());
        values.put(DBHelper.COLUMN_EVENT_DETAILS_PICTURE, eventDetails.getPicture());
        values.put(DBHelper.COLUMN_EVENT_DETAILS_ADD_TIME, eventDetails.getAddingTime());
        values.put(DBHelper.COLUMN_EVENT_DETAILS_USED, eventDetails.getBudgetUsed());
        long result = database.insert(DBHelper.TABLE_EVENT_DETAILS, null, values);
        this.close();

        if (result > 0) {
            Toast.makeText(context, "Information added", Toast.LENGTH_SHORT).show();
            eventDetails.setId(result);
            return eventDetails;
        } else {
            Toast.makeText(context, "Adding Failed", Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    public ArrayList<EventDetails> getEventsDetails(long eventId) {
        this.open();
        ArrayList<EventDetails> eventDetails = new ArrayList<>();

        //getting event details for this event
        Cursor cursor = database.query(DBHelper.TABLE_EVENT_DETAILS,
                eventDetailsInfo, DBHelper.COLUMN_EVENT_DETAILS_EVENT + " = ? ",
                new String[]{"" + eventId}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                EventDetails eventDetail = cursorToEventDetails(cursor);
                eventDetails.add(eventDetail);
                cursor.moveToNext();
            }
        }

        if (cursor != null) cursor.close();
        this.close();
        return eventDetails;

    }

    public int getEventTotalUsed(long eventId) {
        this.open();
        int totalUsed = 0;

        //getting event details for this event
//        Cursor cursor = database.rawQuery("SELECT SUM ("+DBHelper.COLUMN_EVENT_BUDGET+") FROM myTable", null);
        Cursor cursor = database.query(DBHelper.TABLE_EVENT_DETAILS,
                new String[]{"SUM (" + DBHelper.COLUMN_EVENT_DETAILS_USED + ") "},
                DBHelper.COLUMN_EVENT_DETAILS_EVENT + " = ? ",
                new String[]{"" + eventId}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            totalUsed = cursor.getInt(0);
        }
        if (cursor != null)
            cursor.close();

        this.close();
        return totalUsed;
    }

    public boolean updateEventDetails(EventDetails eventDetails) {
        this.open();

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_EVENT_DETAILS_EVENT, eventDetails.getEventId());
        values.put(DBHelper.COLUMN_EVENT_DETAILS_DETAILS, eventDetails.getDetails());
        values.put(DBHelper.COLUMN_EVENT_DETAILS_PICTURE, eventDetails.getPicture());
        values.put(DBHelper.COLUMN_EVENT_DETAILS_ADD_TIME, eventDetails.getAddingTime());
        values.put(DBHelper.COLUMN_EVENT_DETAILS_USED, eventDetails.getBudgetUsed());
        long result = database.update(DBHelper.TABLE_EVENT_DETAILS, values, DBHelper.COLUMN_EVENT_DETAILS_ID + " = ?", new String[]{"" + eventDetails.getId()});
        this.close();

        if (result > 0) {
            Toast.makeText(context, "Information updated", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    protected EventDetails cursorToEventDetails(Cursor cursor) {
        EventDetails eventDetail = new EventDetails();
        eventDetail.setId(cursor.getLong(0));
        eventDetail.setEventId(cursor.getLong(1));
        eventDetail.setDetails(cursor.getString(2));
        eventDetail.setAddingTime(cursor.getString(3));
        eventDetail.setPicture(cursor.getString(4));
        eventDetail.setBudgetUsed(cursor.getInt(5));
        return eventDetail;
    }
}
