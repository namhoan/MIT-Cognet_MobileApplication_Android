package com.cognet;

import android.app.ProgressDialog;
import android.os.Bundle;


public class BooksListActivity extends ItemsListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        Bundle extras = getIntent().getExtras();
        this.cookie = extras.getString("cookie");
        this.token = extras.getString("token");

        final ProgressDialog dialog = ProgressDialog.show(this, "", "Retrieving books list...", true);
        new Thread(new Runnable(){
            public void run(){
                BooksListActivity.super.initializeItemsList(BooksListActivity.this, true);
                dialog.dismiss();
            }
        }).start();
    }
}

