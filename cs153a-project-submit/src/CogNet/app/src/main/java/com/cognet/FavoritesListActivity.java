package com.cognet;

import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * Created by dimos on 12/2/15.
 */
public class FavoritesListActivity extends ItemsListActivity {

    private boolean stopped = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);

        Bundle extras = getIntent().getExtras();
        this.cookie = extras.getString("cookie");
        this.token = extras.getString("token");
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Retrieving favorites list...", true);
        new Thread(new Runnable(){
            public void run(){
                FavoritesListActivity.super.initializeItemsList(FavoritesListActivity.this, true);
                dialog.dismiss();
            }
        }).start();

    }

    @Override
    public void onStart(){
        super.onStart();
        if (stopped) {
            System.out.println("Favorites List Activity onStart()");
            final ProgressDialog dialog = ProgressDialog.show(this, "", "Retrieving favorites list...", true);
            new Thread(new Runnable() {
                public void run() {
                    FavoritesListActivity.super.initializeItemsList(FavoritesListActivity.this, false);
                    dialog.dismiss();
                }
            }).start();
        }
    }

    @Override
    public void onStop(){
        stopped = true;
        super.onStop();
    }


}
