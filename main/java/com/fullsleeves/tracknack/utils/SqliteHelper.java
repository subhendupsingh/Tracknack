package com.fullsleeves.tracknack.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by enigma-pc on 3/1/16.
 */
public class SqliteHelper extends SQLiteOpenHelper {

    public static final String TABLE_MEDIA = "image_upload_list";
    public static final String KEY_ID = "id";
    public static final String KEY_URI = "uri";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_UPLOAD_FLAG = "upload_flag";
    private static final String DATABASE_NAME = "Tracknack.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_MEDIA + "(" + KEY_ID
            + " integer primary key autoincrement," + KEY_URI
            + " text not null,"+KEY_TITLE+" text,"+KEY_DESCRIPTION+" text,"+KEY_UPLOAD_FLAG+" integer"+");";

    public SqliteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
        onCreate(db);
    }
}
