package com.example.jasim.tour.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    //database information
    private static final String DATABASE_NAME = "tourMate.db";
    private static final int DATABASE_VERSION = 1;

    // columns of the user table
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_PICTURE = "picture";
    public static final String COLUMN_USER_MOBILE_NO = "mobile_no";
    public static final String COLUMN_USER_RECOVERY_DATA = "recovery_data";

    // columns of the event table
    public static final String TABLE_EVENT = "event";
    public static final String COLUMN_EVENT_ID = "id";
    public static final String COLUMN_EVENT_USER = "user_id";
    public static final String COLUMN_EVENT_NAME = "name";
    public static final String COLUMN_EVENT_LOCATION = "location";
    public static final String COLUMN_EVENT_BUDGET = "budget_tk";
    public static final String COLUMN_EVENT_START_DATE = "start_date";
    public static final String COLUMN_EVENT_END_DATE = "end_date";

    //column of the event details table
    public static final String TABLE_EVENT_DETAILS = "event_details";
    public static final String COLUMN_EVENT_DETAILS_ID = "id";
    public static final String COLUMN_EVENT_DETAILS_EVENT = "event_id";
    public static final String COLUMN_EVENT_DETAILS_DETAILS = "description";
    public static final String COLUMN_EVENT_DETAILS_PICTURE = "picture";
    public static final String COLUMN_EVENT_DETAILS_ADD_TIME = "addi_time";
    public static final String COLUMN_EVENT_DETAILS_USED = "used_tk";


    // SQL statement of the user table creation
    private static final String SQL_CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + "("
                    + COLUMN_USER_ID + " INTEGER PRIMARY KEY, "
                    + COLUMN_USER_NAME + " TEXT NOT NULL, "
                    + COLUMN_USER_PASSWORD + " TEXT NOT NULL, "
                    + COLUMN_USER_PICTURE + " TEXT, "
                    + COLUMN_USER_MOBILE_NO + " TEXT NOT NULL, "
                    + COLUMN_USER_RECOVERY_DATA + " TEXT NOT NULL );";

    // SQL statement of the event table creation
    private static final String SQL_CREATE_TABLE_EVENT =
            "CREATE TABLE " + TABLE_EVENT + "("
                    + COLUMN_EVENT_ID + " INTEGER PRIMARY KEY, "
                    + COLUMN_EVENT_USER + " INTEGER NOT NULL, "
                    + COLUMN_EVENT_NAME + " TEXT NOT NULL, "
                    + COLUMN_EVENT_LOCATION + " TEXT NOT NULL, "
                    + COLUMN_EVENT_BUDGET + " INTEGER, "
                    + COLUMN_EVENT_START_DATE + " TEXT NOT NULL, "
                    + COLUMN_EVENT_END_DATE + " TEXT NOT NULL );";

    // SQL statement of the event details table creation
    private static final String SQL_CREATE_TABLE_EVENT_DETAILS =
            "CREATE TABLE " + TABLE_EVENT_DETAILS + "("
                    + COLUMN_EVENT_DETAILS_ID + " INTEGER PRIMARY KEY, "
                    + COLUMN_EVENT_DETAILS_EVENT + " INTEGER NOT NULL, "
                    + COLUMN_EVENT_DETAILS_DETAILS + " TEXT NOT NULL, "
                    + COLUMN_EVENT_DETAILS_PICTURE + " TEXT, "
                    + COLUMN_EVENT_DETAILS_ADD_TIME + " TEXT NOT NULL, "
                    + COLUMN_EVENT_DETAILS_USED + " INTEGER );";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE_USER);
        database.execSQL(SQL_CREATE_TABLE_EVENT);
        database.execSQL(SQL_CREATE_TABLE_EVENT_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_DETAILS);
        onCreate(db);
    }

}
