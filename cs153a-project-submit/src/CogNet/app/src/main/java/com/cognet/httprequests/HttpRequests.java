package com.cognet.httprequests;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by dimos on 11/28/2015.
 */
public class HttpRequests {

    public HttpRequests(){

    }

    public static InputStream getInpuStream(String url) throws IOException {
        HttpGet getRequest = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(getRequest);
        InputStream is = response.getEntity().getContent();
        return is;
    }


    public static HttpEntity getPDFresponse(String url, String cookie) throws IOException{
        HttpGet getRequest = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        getRequest.setHeader("Cookie", cookie);
        getRequest.setHeader("Accept", "text/html,application/pdf,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        getRequest.setHeader("Connection", "keep-alive");
        getRequest.setHeader("Cache-Control", "max-age=0");
        getRequest.setHeader("Upgrade-Insecure-Requests", "1");
        getRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
        getRequest.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        getRequest.setHeader("Host", "cognet-staging2.mit.edu");
        getRequest.setHeader("Accept-Language", "en");


        HttpResponse response = client.execute(getRequest);
        //System.out.println("Encoding is: "+ response.getEntity().getContentEncoding());
        //System.out.println("Content type is: "+ response.getEntity().getContentType());

        return response.getEntity();
    }

    public static InputStream getPDF(String url, String cookie) throws IOException{
        HttpGet getRequest = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        getRequest.setHeader("Cookie", cookie);
        getRequest.setHeader("Accept", "text/html,application/pdf,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        getRequest.setHeader("Connection", "keep-alive");
        getRequest.setHeader("Cache-Control", "max-age=0");
        getRequest.setHeader("Upgrade-Insecure-Requests", "1");
        getRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
        getRequest.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        getRequest.setHeader("Host", "cognet-staging2.mit.edu");
        getRequest.setHeader("Accept-Language", "en");


        HttpResponse response = client.execute(getRequest);
        System.out.println("Encoding is: "+ response.getEntity().getContentEncoding());
        System.out.println("Content type is: "+ response.getEntity().getContentType());

        InputStream is = response.getEntity().getContent();
        return is;
    }
}
