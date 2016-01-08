package com.cognet;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cognet.httprequests.HttpRequests;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PDFViewer extends ActionBarActivity {

    private String pdfUrl;
    private String cookie;

    /*
        This approach did not work! The google doc plugin for viewing the pdf does not redirect the cookie header to the cognet website,
        thus the request is not authenticated and it is rejected. Download the pdf and open it locally via intents instead!
     */
    private void configWebViewToViewDirectly(){
        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);
        System.out.println("pdf url: " + pdfUrl + ", cookie: " + cookie);
        //webView.loadUrl("https://docs.google.com/gview?embedded=true&url=http://www.cs.brandeis.edu/~olga/publications/clouddb14-admission.pdf");
        //setContentView(R.layout.activity_pdfviewer);
        WebViewClient wvc = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", cookie);
                view.loadUrl(url, headers);
                return false;
            }
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                try {
                    System.out.println("Got Cookie?: "+request.getRequestHeaders().get("Cookie"));
                    DefaultHttpClient client = new DefaultHttpClient();
                    Uri url = request.getUrl();
                    //Uri url = Uri.parse("https://docs.google.com/gview?embedded=true&url=http://www.cs.brandeis.edu/~olga/publications/clouddb14-admission.pdf");
                    HttpGet httpGet = new HttpGet(url.toString());
                    httpGet.setHeader("Cookie", PDFViewer.this.cookie);
                    httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
                    HttpResponse httpReponse = client.execute(httpGet);

                    Header contentType = httpReponse.getEntity().getContentType();
                    Header encoding = httpReponse.getEntity().getContentEncoding();


                    InputStream responseInputStream ;
                    //responseInputStream  = httpReponse.getEntity().getContent();

                    if (request.getUrl().toString().equals("https://docs.google.com/favicon.ico")){
                        responseInputStream  = httpReponse.getEntity().getContent();
                    }
                    else {
                        responseInputStream = HttpRequests.getPDF(PDFViewer.this.pdfUrl, PDFViewer.this.cookie);
                    }


                    String contentTypeValue = null;
                    String encodingValue = null;
                    if (contentType != null) {
                        contentTypeValue = contentType.getValue();
                    }
                    contentTypeValue = "application/pdf";
                    System.out.println("ContentType: "+contentTypeValue);
                    System.out.println("encoding: "+encodingValue);


                    if (encoding != null) {
                        encodingValue = encoding.getValue();
                    }
                    return new WebResourceResponse(contentTypeValue, encodingValue, responseInputStream);
                } catch (ClientProtocolException e) {
                    //return null to tell WebView we failed to fetch it WebView should try again.
                    return null;
                } catch (IOException e) {
                    //return null to tell WebView we failed to fetch it WebView should try again.
                    return null;
                }
            }
        };
        webView.setWebViewClient(wvc);
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfUrl, headers);
        setContentView(webView);
        System.out.println("Done~!");
    }


    private void startIntentToViewPDF(){
        File pdf = new File(this.pdfUrl);
        Uri path = Uri.fromFile(pdf);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try{
            startActivity(pdfIntent);
        }
        catch(ActivityNotFoundException e){
            Toast.makeText(this, "No Application available to view pdf", Toast.LENGTH_LONG);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        this.pdfUrl = extras.getString("pdfUrl");
        System.out.println("PDF URL: "+this.pdfUrl);
        //configWebViewToViewDirectly();
        startIntentToViewPDF();
    }

}
