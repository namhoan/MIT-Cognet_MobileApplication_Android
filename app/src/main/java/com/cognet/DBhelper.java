package com.cognet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhelper extends SQLiteOpenHelper {

    // TABLE INFORMATTION
    public static final String DATABASE_TABLE = "favorites";
    public static final String KEY_COLUMN = "_id";
    public static final String ID_COLUMN = "id";
    public static final String TITLE_COLUMN = "title";
    public static final String AUTHOR_COLUMN = "author";
    public static final String PUBDATE_COLUMN = "pubDate";
    public static final String IMAGE_URL_COLUMN = "imageURL";
    public static final String PDF_URL_COLUMN = "pdfURL";




    // DATABASE INFORMATION
    static final String DB_NAME = "favorites.db";
    static final int DB_VERSION = 1;

    // TABLE CREATION STATEMENT

    private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE +" (" +
            KEY_COLUMN+" integer primary key autoincrement, "+
            TITLE_COLUMN+" text not null," +
            AUTHOR_COLUMN+" text not null," +
            PUBDATE_COLUMN+" text not null" +
            IMAGE_URL_COLUMN+" text not null" +
            PDF_URL_COLUMN+" text not null" +
            ");";

    public DBhelper(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("TaskDBAdapter", "Upgrading from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("drop table if exists " + DATABASE_TABLE);
        onCreate(db);
    }
}