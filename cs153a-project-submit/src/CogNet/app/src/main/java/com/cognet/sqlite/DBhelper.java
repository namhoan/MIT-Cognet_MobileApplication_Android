package com.cognet.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhelper extends SQLiteOpenHelper {


    // DATABASE INFORMATION
    static final String DB_NAME = "favorites.db";
    static final int DB_VERSION = 9;

    // TABLE INFORMATTION
    public static final String FAVORITES_TABLE = "favorites";
    public static final String KEY_COLUMN = "_id";
    public static final String ID_COLUMN = "id";
    public static final String TYPE_COLUMN = "type";
    public static final String TITLE_COLUMN = "title";
    public static final String AUTHOR_COLUMN = "author";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String PUBDATE_COLUMN = "pubDate";
    public static final String IMAGE_URL_COLUMN = "imageURL";
    public static final String PDF_URL_COLUMN = "pdfURL";
    public static final String ISBN_COLUMN = "isbn";


    // TABLE CREATION STATEMENT

    private static final String DATABASE_CREATE = "create table " + FAVORITES_TABLE +" (" +
            KEY_COLUMN+" integer primary key autoincrement, "+
            TYPE_COLUMN + " text not null, "+
            TITLE_COLUMN+" text not null, " +
            AUTHOR_COLUMN+" text not null, " +
            DESCRIPTION_COLUMN + " text, " +
            PUBDATE_COLUMN+" text, " +
            IMAGE_URL_COLUMN+" text, " +
            PDF_URL_COLUMN+" text, " +
            ISBN_COLUMN+" text" +
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
        db.execSQL("drop table if exists " + FAVORITES_TABLE);
        onCreate(db);
    }
}