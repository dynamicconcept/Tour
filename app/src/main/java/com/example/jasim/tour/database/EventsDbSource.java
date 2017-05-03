package com.example.jasim.tour.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.jasim.tour.model.Events;


public class EventsDbSource {
    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] eventsInfo = {DBHelper.COLUMN_EVENT_ID, DBHelper.COLUMN_EVENT_USER,
            DBHelper.COLUMN_EVENT_NAME, DBHelper.COLUMN_EVENT_LOCATION,
            DBHelper.COLUMN_EVENT_BUDGET, DBHelper.COLUMN_EVENT_START_DATE,
            DBHelper.COLUMN_EVENT_END_DATE};
    Context context;

    public EventsDbSource(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Events addEvent(Events event) {
        this.open();

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_EVENT_USER, event.getEventUser());
        values.put(DBHelper.COLUMN_EVENT_NAME, event.getEventName());
        values.put(DBHelper.COLUMN_EVENT_LOCATION, event.getEventLocation());
        values.put(DBHelper.COLUMN_EVENT_BUDGET, event.getBudget());
        values.put(DBHelper.COLUMN_EVENT_START_DATE, event.getEventStartDate());
        values.put(DBHelper.COLUMN_EVENT_END_DATE, event.getEventEndDate());
        long result = database.insert(DBHelper.TABLE_EVENT, null, values);
        this.close();

        if (result > 0) {
            Toast.makeText(context, "Event add successful",
                    Toast.LENGTH_SHORT).show();
            event.setEventID(result);
            return event;
        } else {
            Toast.makeText(context, "Event add Failed",
                    Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    public ArrayList<Events> getEvents(long userId) {
        this.open();
        ArrayList<Events> events = new ArrayList<>();

        //getting events for this user
        Cursor cursor = database.query(DBHelper.TABLE_EVENT,
                eventsInfo, DBHelper.COLUMN_EVENT_USER + " = ? ",
                new String[]{"" + userId}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Events event = cursorToEvent(cursor);
                events.add(event);
                cursor.moveToNext();
            }
        }

        if (cursor!=null)
        cursor.close();
        this.close();
        return events;

    }

//    public boolean updateEvent(Events event) {
//        this.open();
//
//        ContentValues values = new ContentValues();
//        values.put(DBHelper.COLUMN_EVENT_NAME, event.getEventName());
//        values.put(DBHelper.COLUMN_EVENT_LOCATION, event.getEventLocation());
//        values.put(DBHelper.COLUMN_EVENT_BUDGET, event.getBudget());
//        values.put(DBHelper.COLUMN_EVENT_START_DATE, event.getEventStartDate());
//        values.put(DBHelper.COLUMN_EVENT_END_DATE, event.getEventEndDate());
//        long result = database.update(DBHelper.TABLE_EVENT, values, DBHelper.COLUMN_EVENT_ID + " = ?", new String[]{"" + event.getEventID()});
//        this.close();
//
//        if (result > 0) {
//            Toast.makeText(context, "Event updates successful", Toast.LENGTH_SHORT).show();
//            return true;
//        } else {
//            Toast.makeText(context, "Event update Failed", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//    }

//    public boolean deleteEvent(int eventId) {
//        this.open();
//
//        long result = database.delete(DBHelper.TABLE_EVENT, DBHelper.COLUMN_EVENT_ID + " = ?", new String[]{"" + eventId});
//        this.close();
//
//        if (result > 0) {
//            Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show();
//            return true;
//        } else {
//            Toast.makeText(context, "Deletion failed", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//    }

    protected Events cursorToEvent(Cursor cursor) {
        Events event = new Events();
        event.setEventID(cursor.getLong(0));
        event.setEventUser(cursor.getInt(1));
        event.setEventName(cursor.getString(2));
        event.setEventLocation(cursor.getString(3));
        event.setBudget(cursor.getInt(4));
        event.setEventStartDate(cursor.getString(5));
        event.setEventEndDate(cursor.getString(6));
        return event;
    }

}
