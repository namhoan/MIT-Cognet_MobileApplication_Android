package com.cognet.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.cognet.R;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Intent;
import android.app.Activity;


import org.apache.http.client.ClientProtocolException;
import java.io.IOException;


public class Login extends Activity {

    private Dialog errorDialog;

    private void configLoginErrorDialog(){
        errorDialog = new Dialog(Login.this);
        errorDialog.setContentView(R.layout.login_error);
        errorDialog.setTitle("Error logging in");
        Button btnOK = (Button)errorDialog.findViewById(R.id.btn_login_error_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.cancel();
            }
        });
    }

    private void configLoginButton(){
        Button btnLogin = (Button)findViewById(R.id.btn_login_activity_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RestClient client = new RestClient(getString(R.string.cognetRestURL));
                        try {
                            EditText username = (EditText) findViewById(R.id.edit_text_username);
                            EditText password = (EditText) findViewById(R.id.edit_text_password);
                            Intent sessionInfo = client.login(username.getText().toString(), password.getText().toString());
                            if (sessionInfo != null) {
                                setResult(RESULT_OK, sessionInfo);
                                finish();
                            } else {
                                System.out.println("Session info is null!");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        errorDialog.show();
                                    }
                                });
                            }
                        } catch (ClientProtocolException cpe) {
                            cpe.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    errorDialog.show();
                                }
                            });
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    errorDialog.show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.configLoginErrorDialog();
        this.configLoginButton();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER){
            Button btnLogin = (Button)findViewById(R.id.btn_login_activity_login);
            btnLogin.performClick();
        }
        return super.onKeyDown(keyCode, event);
    }
}
