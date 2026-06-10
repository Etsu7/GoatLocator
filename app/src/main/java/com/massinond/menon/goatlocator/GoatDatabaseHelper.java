package com.massinond.menon.goatlocator;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class GoatDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "goatlocator.db";
    private static final int    DB_VERSION = 1;

    public static final String TABLE      = "goats";
    public static final String COL_ID     = "id";
    public static final String COL_PHOTO  = "photo_path";
    public static final String COL_LAT    = "latitude";
    public static final String COL_LON    = "longitude";
    public static final String COL_DATE   = "date";

    public GoatDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                COL_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PHOTO + " TEXT, " +
                COL_LAT   + " REAL, " +
                COL_LON   + " REAL, " +
                COL_DATE  + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long insertGoat(String photoPath, double latitude, double longitude, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PHOTO, photoPath);
        values.put(COL_LAT,   latitude);
        values.put(COL_LON,   longitude);
        values.put(COL_DATE,  date);
        return db.insert(TABLE, null, values);
    }

    public List<Goat> getAllGoats() {
        List<Goat> goats = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, null, null, null, null, COL_ID + " DESC");

        while (cursor.moveToNext()) {
            goats.add(new Goat(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PHOTO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LAT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LON)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE))
            ));
        }
        cursor.close();
        return goats;
    }
}