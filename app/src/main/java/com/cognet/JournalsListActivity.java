package com.cognet;

import android.app.ProgressDialog;
import android.os.Bundle;


public class JournalsListActivity extends ItemsListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        System.out.println("journal search view: " + super.findViewById(R.id.edit_text_item_search));
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Retrieving journals list...", true);
        new Thread(new Runnable(){
            public void run(){
                JournalsListActivity.super.initializeItemsList(JournalsListActivity.this);
                dialog.dismiss();
            }
        }).start();
    }
}
