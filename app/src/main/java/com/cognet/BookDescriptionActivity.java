package com.cognet;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cognet.httprequests.HttpRequests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;

public class BookDescriptionActivity extends Activity {

    private String cookie;
    private String token;
    private String pdfUrl = "http://cognet-staging2.mit.edu/system/cogfiles/books/9780262286688/pdfs/9780262286688_chap1.pdf";
    private String pdfNameLocal = "9780262286688_chap1.pdf";
    private String pdfUrlLocalAbsolute;
    private String imageURL;

    ImageView imageView;

    private void viewPDF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    File directory = new File(Environment.getExternalStorageDirectory()+"/cognetTMP");
                    directory.mkdirs();


                    File file = new File(directory, BookDescriptionActivity.this.pdfNameLocal);
                    //File file = new File(directory, BookDescriptionActivity.this.pdfNameLocal);
                    BookDescriptionActivity.this.pdfUrlLocalAbsolute = file.getPath();
                    System.out.println("PDF file path: "+ BookDescriptionActivity.this.pdfUrlLocalAbsolute);
                    InputStream is = HttpRequests.getPDF(BookDescriptionActivity.this.pdfUrl, BookDescriptionActivity.this.cookie);


                    FileOutputStream fos = new FileOutputStream(file);
                    //FileOutputStream fos = BookDescriptionActivity.this.openFileOutput(BookDescriptionActivity.this.pdfNameLocal, Context.MODE_WORLD_WRITEABLE);
                    InputStream isr = new BufferedInputStream(is);
                    int bufferSize = 4096;
                    byte [] buffer = new byte[bufferSize];
                    int bytesRead;
                    int bytesReadTotal = 0;

                    while( (bytesRead = isr.read(buffer, 0, bufferSize)) != -1 ){
                        fos.write(buffer, 0, bytesRead);
                        bytesReadTotal += bytesRead;
                        buffer = new byte[bufferSize];
                    }
                    System.out.println("Wrote " + bytesReadTotal + " bytes.");
                    fos.flush();
                    fos.close();
                    isr.close();

                    //File pdf = new File( dir, BookDescriptionActivity.this.pdfNameLocal);
                    //File pdf = new File(BookDescriptionActivity.this.getApplicationContext().getFilesDir(), BookDescriptionActivity.this.pdfNameLocal);
                    //File dir = new File(Environment.getExternalStorageDirectory()+"/cognetTMP");
                    File pdf = new File(directory, BookDescriptionActivity.this.pdfNameLocal);
                    Uri path = Uri.fromFile(pdf);
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(path, "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try{
                        startActivity(pdfIntent);
                    }
                    catch(ActivityNotFoundException e){
                        System.out.println("No Application available to view pdf");
                        //Toast.makeText(BookDescriptionActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG);
                    }
                }
                catch (IOException e){
                    System.err.println("Exception while getting PDF: "+e);
                }
            }
        }).start();
    }

    private void configViewPDFButton(){
        Button viewPDFBtn = (Button) findViewById(R.id.btn_view_pdf);
        viewPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPDF();
            }
        });

    }

    private void initializeImageView(){
        imageView = (ImageView) findViewById(R.id.image_book_img);
        // Create an object for subclass of AsyncTask
        ImageRetrieverAsync task = new ImageRetrieverAsync();
        // Execute the task
        task.execute(new String[]{imageURL});

    }

    private void initializeTextViews(Bundle extras){
        TextView titleTextView = (TextView) findViewById(R.id.text_book_title);
        TextView authorTextView = (TextView) findViewById(R.id.text_book_author);
        TextView pubDateTextView = (TextView) findViewById(R.id.text_book_pubdate);
        TextView descriptionTextView = (TextView) findViewById(R.id.text_book_description);

        String title = extras.getString("title");
        String author = extras.getString("author");
        String pubDate = extras.getString("pubDate");
        String description = extras.getString("description");


        titleTextView.setText(title);
        authorTextView.setText(author);
        pubDateTextView.setText(pubDate);
        descriptionTextView.setText(description);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_description);
        Bundle extras = getIntent().getExtras();
        // display the book description in the appropriate views
        if (extras != null) {
            this.imageURL = extras.getString("image");
            this.cookie = extras.getString("cookie");
            this.token = extras.getString("token");
            initializeImageView();
        }
        configViewPDFButton();



    }


    private class ImageRetrieverAsync extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            Bundle extras = getIntent().getExtras();
            // display the book description in the appropriate views
            initializeTextViews(extras);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }

}
