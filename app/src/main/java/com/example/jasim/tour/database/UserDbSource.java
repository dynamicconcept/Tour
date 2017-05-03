package com.example.jasim.tour.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.jasim.tour.model.User;


public class UserDbSource {

    // Database fields
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] userInfo = {DBHelper.COLUMN_USER_ID, DBHelper.COLUMN_USER_NAME,
            DBHelper.COLUMN_USER_PICTURE};
    Context context;

    public UserDbSource(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean userRegistration(User user) {
        this.open();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_USER_NAME, user.getUserName());
        values.put(DBHelper.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(DBHelper.COLUMN_USER_PICTURE, user.getUserImage());
        values.put(DBHelper.COLUMN_USER_MOBILE_NO, user.getMobileNo());
        values.put(DBHelper.COLUMN_USER_RECOVERY_DATA, user.getRecoveryInfo());
        long insertId = database.insert(DBHelper.TABLE_USER, null, values);
        this.close();

        if (insertId > 0) {
            Toast.makeText(context, "Registration Success", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public boolean checkingDuplicate(String userName) {
        this.open();

        Cursor cursor = database.query(DBHelper.TABLE_USER, null,
                DBHelper.COLUMN_USER_NAME + " = ?",
                new String[]{"" + userName}, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return false;
        }

        if (cursor != null) cursor.close();
        this.close();
        return true;

    }

    public User getUser(String userName, String password) {
        this.open();
        User user = new User();

        //checking user name
        Cursor cursor = database.query(DBHelper.TABLE_USER,
                null, DBHelper.COLUMN_USER_NAME + " = ? ",
                new String[]{"" + userName}, null, null, null);
        if (cursor.getCount() > 0) { //checking information
            cursor.close();
            Cursor cursor1 = database.query(DBHelper.TABLE_USER, userInfo,
                    DBHelper.COLUMN_USER_NAME + " = ? and " +
                            DBHelper.COLUMN_USER_PASSWORD + " = ?",
                    new String[]{"" + userName, "" + password}, null, null, null);
            cursor1.moveToFirst();

            if (cursor1.getCount() > 0)
                user = cursorToUser(cursor1);
            else user.setUserName(""); //password wrong
            cursor1.close();
            this.close();
            return user;
        }

        cursor.close();
        this.close();
        user.setUserName("@@");
        return user;
    }

    public User getUser(long userId) {
        this.open();
        User user = new User();
        Cursor cursor = database.query(DBHelper.TABLE_USER,
                new String[]{DBHelper.COLUMN_USER_NAME, DBHelper.COLUMN_USER_PASSWORD,
                        DBHelper.COLUMN_USER_MOBILE_NO, DBHelper.COLUMN_USER_RECOVERY_DATA,
                        DBHelper.COLUMN_USER_PICTURE},
                DBHelper.COLUMN_USER_ID + " = ?",
                new String[]{"" + userId}, null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            user.setUserName(cursor.getString(0));
            user.setPassword(cursor.getString(1));
            user.setMobileNo(cursor.getString(2));
            user.setRecoveryInfo(cursor.getString(3));
            user.setUserImage(cursor.getString(4));
        }

        cursor.close();
        this.close();
        return user;
    }


    public boolean updateUser(User user) {
        this.open();
        ContentValues values = new ContentValues();

        values.put(DBHelper.COLUMN_USER_NAME, user.getUserName());
        values.put(DBHelper.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(DBHelper.COLUMN_USER_PICTURE, user.getUserImage());
        values.put(DBHelper.COLUMN_USER_MOBILE_NO, user.getMobileNo());
        values.put(DBHelper.COLUMN_USER_RECOVERY_DATA, user.getRecoveryInfo());
        long result = database.update(DBHelper.TABLE_USER, values,
                DBHelper.COLUMN_USER_ID + " = ?",
                new String[]{"" + user.getUserID()});
        this.close();

        if (result > 0) {
            Toast.makeText(context, "Information updated successfully ", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Information update failed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public String getPassword(String userName, String recoveryInfo) {
        this.open();

        //checking user name
        Cursor cursor = database.query(DBHelper.TABLE_USER,
                null, DBHelper.COLUMN_USER_NAME + " = ? ",
                new String[]{"" + userName}, null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) { //user found
            cursor.close();
            Cursor cursor1 = database.query(DBHelper.TABLE_USER,
                    new String[]{DBHelper.COLUMN_USER_PASSWORD},
                    "(" + DBHelper.COLUMN_USER_NAME + " = ?) and (" +
                            DBHelper.COLUMN_USER_RECOVERY_DATA + " = ? or " +
                            DBHelper.COLUMN_USER_MOBILE_NO + " = ? )",
                    new String[]{"" + userName, "" + recoveryInfo, "" + recoveryInfo},
                    null, null, null);
            cursor1.moveToFirst();
            if (cursor1.getCount() > 0) {
                String password = cursor1.getString(0);
                cursor1.close();
                this.close();
                return password;
            } else {
                cursor1.close();
                this.close();
                return "wrong@data";
            }

        } else {  //user not found
            cursor.close();
            this.close();
            return "no@user";
        }

    }

    protected User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setUserID(cursor.getLong(0));
        user.setUserName(cursor.getString(1));
        user.setUserImage(cursor.getString(2));
        return user;
    }

}
