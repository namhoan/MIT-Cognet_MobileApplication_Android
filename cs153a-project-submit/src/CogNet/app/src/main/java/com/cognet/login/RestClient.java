package com.cognet.login;

/**
 * Created by dimos on 10/18/15.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;

import android.os.Bundle;

/**
 * Creates a client to Drupal using the Rest API
 */
public class RestClient {
    private String endpointURL;

    // Supported commands
    private static final String SYSTEM_CONNECT_URL = "system/connect";
    private static final String USER_LOGIN_URL = "user/login";
    private static final String USER_LOGOUT_URL = "user/logout";

    private String sessName;
    private String sessid;
    private String token;
    private String cookie;

    public RestClient(String endpointURL){
        this.endpointURL = endpointURL;
    }

    public Intent connect() throws ClientProtocolException, IOException{
        HttpPost post = new HttpPost(this.endpointURL+"/"+SYSTEM_CONNECT_URL);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(post);
        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String lineStr="", responseStr="";
        while( (lineStr = br.readLine()) != null )
            responseStr+=lineStr;
        if(responseStr.contains("\"sessid\":")){
            this.sessid = responseStr.split("\"sessid\":\"")[1].split("\"")[0];
            System.out.println("Connected!");
            Bundle sessid = new Bundle();
            sessid.putString("sessid", this.sessid);
            Intent sessidIntent = new Intent();
            sessidIntent.putExtras(sessid);
            return sessidIntent;
        }
        else
            return null;
    }

    public Intent login(String username, String password) throws ClientProtocolException, IOException{
        HttpPost post = new HttpPost(this.endpointURL+"/"+USER_LOGIN_URL);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        HttpClient client = new DefaultHttpClient();
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = client.execute(post);
        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String lineStr="", responseStr="";
        while( (lineStr = br.readLine()) != null )
            responseStr+=lineStr;
        System.out.println(responseStr);
        if (responseStr.contains("\"sessid\":")){
            System.out.println("Got response as json");
            this.sessName = responseStr.split("\"session_name\":\"")[1].split("\"")[0];
            this.sessid = responseStr.split("\"sessid\":\"")[1].split("\"")[0];
            this.token = responseStr.split("\"token\":\"")[1].split("\"")[0];
            this.cookie = sessName+"="+sessid;
            System.out.println("Logged in!");
            System.out.println("Session name: "+sessName);
            System.out.println("Session ID: "+sessid);
            System.out.println("Token: "+token);
            System.out.println("Cookie: "+this.cookie);
            System.out.println(responseStr);
            Bundle sessionInfo = new Bundle();
            sessionInfo.putString("cookie", this.cookie);
            sessionInfo.putString("token", this.token);
            Intent sessionInfoIntent = new Intent();
            sessionInfoIntent.putExtras(sessionInfo);
            return sessionInfoIntent;
        }
        else if (responseStr.contains("<sessid>")){
            System.out.println("Got response as xml");
            this.sessName = responseStr.split("<session_name>")[1].split("</session_name>")[0];
            this.sessid = responseStr.split("<sessid>")[1].split("</sessid>")[0];
            this.token = responseStr.split("<token>")[1].split("</token>")[0];
            this.cookie = sessName+"="+sessid;
            System.out.println("Logged in!");
            System.out.println("Session name: "+sessName);
            System.out.println("Session ID: "+sessid);
            System.out.println("Token: "+token);
            System.out.println("Cookie: "+this.cookie);
            System.out.println(responseStr);
            Bundle sessionInfo = new Bundle();
            sessionInfo.putString("cookie", this.cookie);
            sessionInfo.putString("token", this.token);
            Intent sessionInfoIntent = new Intent();
            sessionInfoIntent.putExtras(sessionInfo);
            return sessionInfoIntent;
        }
        else
            return null;
    }

    public boolean logout(String cookie, String token) throws ClientProtocolException, IOException{
        this.cookie = cookie;
        this.token = token;
        return logout();
    }

    public boolean logout() throws ClientProtocolException, IOException{
        HttpPost post = new HttpPost(this.endpointURL+"/"+USER_LOGOUT_URL);
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Cookie", this.cookie);
        post.setHeader("X-CSRF-Token", this.token);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(post);
        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String lineStr="", responseStr="";
        while( (lineStr = br.readLine()) != null )
            responseStr+=lineStr;
        if(responseStr.contains("true")) {
            System.out.println("Logged out!");
            return true;
        }
        else
            return false;
    }
}