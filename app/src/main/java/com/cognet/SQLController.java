package com.cognet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SQLController {

    private DBhelper dbhelper;
    private Context ourcontext;
    private SQLiteDatabase database;

    public SQLController(Context c) {
        ourcontext = c;
    }

    public SQLController open() throws SQLException {
        dbhelper = new DBhelper(ourcontext);
        database = dbhelper.getWritableDatabase();
        return this;

    }

    public void close() {
        dbhelper.close();
    }

    //Inserting Data into table
    public void insertData(String name) {
        ContentValues cv = new ContentValues();
        //cv.put(DBhelper., name);
        //database.insert(DBhelper.TABLE_ITEM, null, cv);
    }

    //Getting Cursor to read data from table
    /*public Cursor readData() {
        String[] allColumns = new String[] { DBhelper.ITEM_ID,
                DBhelper.ITEM_NAME };
        Cursor c = database.query(DBhelper.TABLE_ITEM, allColumns, null,
                null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }*/


    // Deleting record data from table by id
    public void deleteData(long itemID) {
    //    database.delete(DBhelper.TABLE_ITEM, DBhelper.ITEM_ID + "="
     //           + itemID, null);
    }

}