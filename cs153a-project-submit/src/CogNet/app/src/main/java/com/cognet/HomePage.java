package com.cognet;

import android.app.Dialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.app.Activity;
import android.content.res.XmlResourceParser;

import com.cognet.login.RestClient;

import org.apache.http.client.ClientProtocolException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.io.InputStream;

public class HomePage extends Activity {

    private static int LOGIN_REQUEST_CODE = 1;
    private String cookie = null;
    private String token = null;

    private void configLoginBtn(){
        final Button btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent("android.intent.action.LOGIN"), LOGIN_REQUEST_CODE);
            }
        });
    }

    private void configBooksBtn(){
        final Button btnBooks = (Button)findViewById(R.id.btn_books);
        btnBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.BOOKS_LIST");
                Bundle bundle = new Bundle();
                bundle.putString("cookie", HomePage.this.cookie);
                bundle.putString("token", HomePage.this.token);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void configJournalsBtn(){
        final Button btnJournals = (Button)findViewById(R.id.btn_journals);
        btnJournals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.JOURNALS_LIST");
                Bundle bundle = new Bundle();
                bundle.putString("cookie", HomePage.this.cookie);
                bundle.putString("token", HomePage.this.token);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void configRefworksBtn(){
        final Button btnRefworks = (Button)findViewById(R.id.btn_reference_works);
        btnRefworks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.REFWORKS_LIST");
                Bundle bundle = new Bundle();
                bundle.putString("cookie", HomePage.this.cookie);
                bundle.putString("token", HomePage.this.token);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void configFavoritesBtn(){
        Button favoritesBtn = (Button) findViewById(R.id.btn_view_favorites);
        favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.FAVORITES_LIST");
                Bundle bundle = new Bundle();
                bundle.putString("cookie", HomePage.this.cookie);
                bundle.putString("token", HomePage.this.token);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        configLoginBtn();
        configBooksBtn();
        configJournalsBtn();
        configRefworksBtn();
        configFavoritesBtn();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return super.onCreateDialog(id);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK){
            Bundle sessionInfo = data.getExtras();
            this.cookie = (String) sessionInfo.get("cookie");
            this.token = (String) sessionInfo.get("token");
            Button login = (Button)findViewById(R.id.btn_login);
            login.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop()");
    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy(). Logging out...");
        // logout user if he is logged in
        new Thread(new Runnable() {
            @Override
            public void run() {
                RestClient client = new RestClient(getString(R.string.cognetRestURL));
                try{
                    client.logout(HomePage.this.cookie, HomePage.this.token);
                }
                catch(ClientProtocolException cpe){
                    cpe.printStackTrace();
                }
                catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }).start();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart()");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        System.out.println("onRestart()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}


