package com.dathuynh.kishop.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.dathuynh.kishop.Model.CartModel;
import com.dathuynh.kishop.Model.UserModel;

import java.util.ArrayList;

public class DBApp extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Kishop2.db";

    private static final String TABLEUSER = "tbluser";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_EMAIL = "user_email";

    private static final int DB_VERSION = 1;

    public DBApp(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    public DBApp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
/*        db.execSQL("DROP TABLE " + TABLECART);*/

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLEUSER
                + "(" + COLUMN_ID + " Text PRIMARY KEY, "
                + COLUMN_USER_EMAIL + " Text "
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void addTemporaryUser(UserModel user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_EMAIL, user.getUser_email());
        long success = db.insert(TABLEUSER, null, contentValues);
        db.close();
    }

    @SuppressLint("Range")
    public UserModel getUserProfile() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLEUSER, null);
            UserModel user = new UserModel();
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    user.setUser_email(res.getString(res.getColumnIndex(COLUMN_USER_EMAIL)));
                    break;
                }
                res.close();
            }
            db.close();

            if (!TextUtils.isEmpty(user.getUser_email())) {
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}
