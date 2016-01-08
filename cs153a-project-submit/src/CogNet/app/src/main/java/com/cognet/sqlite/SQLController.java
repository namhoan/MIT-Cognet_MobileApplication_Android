package com.cognet.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.apache.http.impl.DefaultBHttpClientConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public void insertItem(String type, String title, String author, String description, String pubDate, String imageURL, String pdfURL, String ISBN) {
        // first check if item already exists in DB
        boolean itemExists = exists(title, ISBN);
        if (itemExists) {
            System.out.println("Item exists. Do not insert!");
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(DBhelper.TYPE_COLUMN, type);
        title = title.replace("\"", "");
        cv.put(DBhelper.TITLE_COLUMN, title);
        cv.put(DBhelper.AUTHOR_COLUMN, author);
        cv.put(DBhelper.DESCRIPTION_COLUMN, description);
        cv.put(DBhelper.PUBDATE_COLUMN, pubDate);
        cv.put(DBhelper.IMAGE_URL_COLUMN, imageURL);
        cv.put(DBhelper.PDF_URL_COLUMN, pdfURL);
        cv.put(DBhelper.ISBN_COLUMN, ISBN);
        System.out.println("Inserting "+title+ " to DB!");
        database.insert(DBhelper.FAVORITES_TABLE, null, cv);
    }

    public boolean exists(String title, String ISBN){
        System.out.println("Checking if exists: "+ title + ", "+ ISBN);
        String[] columns = new String[] { DBhelper.TITLE_COLUMN, DBhelper.ISBN_COLUMN};
        String selection = DBhelper.TITLE_COLUMN+" = \""+ title.replace("\"", "") + "\"";
        if (ISBN != null && !ISBN.isEmpty()){
            selection += " AND " + DBhelper.ISBN_COLUMN + " = " + ISBN;
        }
        Cursor c = database.query(DBhelper.FAVORITES_TABLE, columns, selection,
                null, null, null, null);
        if (c != null) {
            if (c.moveToFirst()){
                c.close();
                return true;
            }
            c.close();
        }
        return false;
    }

    //Getting Cursor to read data from table
    public List<HashMap<String, String>> getAllItems() {
        List<HashMap<String, String>> items = null;
        String[] allColumns = new String[] { DBhelper.TYPE_COLUMN, DBhelper.TITLE_COLUMN, DBhelper.AUTHOR_COLUMN, DBhelper.DESCRIPTION_COLUMN,
                DBhelper.PUBDATE_COLUMN, DBhelper.IMAGE_URL_COLUMN, DBhelper.PDF_URL_COLUMN, DBhelper.ISBN_COLUMN};
        Cursor c = database.query(DBhelper.FAVORITES_TABLE, allColumns, null,
                null, null, null, null);
        if (c != null) {
            if( c.moveToFirst() ){
                items = new ArrayList<HashMap<String, String>>();
                do {
                    String type = c.getString(c.getColumnIndex(DBhelper.TYPE_COLUMN));
                    String title = c.getString(c.getColumnIndex(DBhelper.TITLE_COLUMN));
                    title.replace("\"", "");
                    String author = c.getString(c.getColumnIndex(DBhelper.AUTHOR_COLUMN));
                    String description = c.getString(c.getColumnIndex(DBhelper.DESCRIPTION_COLUMN));
                    String pubDate = c.getString(c.getColumnIndex(DBhelper.PUBDATE_COLUMN));
                    String imageURL = c.getString(c.getColumnIndex(DBhelper.IMAGE_URL_COLUMN));
                    String pdfURL = c.getString(c.getColumnIndex(DBhelper.PDF_URL_COLUMN));
                    String isbn = c.getString(c.getColumnIndex(DBhelper.ISBN_COLUMN));
                    HashMap<String, String> item = new HashMap<String, String>();
                    item.put("type", type);
                    item.put("tlt", title);
                    item.put("aut", author);
                    item.put("pud", pubDate);
                    item.put("image", imageURL);
                    item.put("pdf", pdfURL);
                    item.put("isbn", isbn);
                    item.put("descr", description);
                    items.add(item);
                } while(c.moveToNext());
            }
            c.close();
        }
        return items;
    }


    public void deleteItem(String title, String ISBN) {
        System.out.println("Deleting item: "+ title + ", "+ ISBN);
        String[] columns = new String[] { DBhelper.TITLE_COLUMN, DBhelper.ISBN_COLUMN};
        String selection = DBhelper.TITLE_COLUMN+" = \""+ title.replace("\"", "") + "\"";
        if (ISBN != null && !ISBN.isEmpty()){
            selection += " AND " + DBhelper.ISBN_COLUMN + " = " + ISBN;
        }
        database.delete(DBhelper.FAVORITES_TABLE, selection, null );
    }
}