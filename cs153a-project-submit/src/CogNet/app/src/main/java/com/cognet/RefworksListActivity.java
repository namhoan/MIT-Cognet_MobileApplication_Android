package com.cognet;

import android.app.ProgressDialog;
import android.os.Bundle;


public class RefworksListActivity extends ItemsListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        Bundle extras = getIntent().getExtras();
        this.cookie = extras.getString("cookie");
        this.token = extras.getString("token");

        System.out.println("item search view: " + super.findViewById(R.id.edit_text_item_search));
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Retrieving reference works list...", true);
        new Thread(new Runnable(){
            public void run(){
                RefworksListActivity.super.initializeItemsList(RefworksListActivity.this, true);
                dialog.dismiss();
            }
        }).start();
    }
}

