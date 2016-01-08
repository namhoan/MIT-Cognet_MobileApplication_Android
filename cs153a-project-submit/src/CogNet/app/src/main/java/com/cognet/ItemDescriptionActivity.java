package com.cognet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cognet.httprequests.HttpRequests;
import com.cognet.sqlite.SQLController;

import org.apache.http.HttpEntity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;

public class ItemDescriptionActivity extends Activity {

    private String cookie;
    private String token;
    private String pdfNameLocal = "tmp.pdf";
    private String imageURL;
    private String ISBN;

    private String title ;
    private String author;
    private String pubDate;
    private String description;
    private String type;
    private String pdfURL;



    ImageView imageView;

    private void showAlert(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(ItemDescriptionActivity.this)
                        .setTitle("Error opening pdf")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    private void downloadAndViewPDF(String pdfURL){
        try {
            File directory = new File(Environment.getExternalStorageDirectory()+"/cognetTMP");
            directory.mkdirs();
            File file = new File(directory, this.pdfNameLocal);
            // get specified PDF's InputStream
            InputStream is = HttpRequests.getPDF(pdfURL, this.cookie);
            FileOutputStream fos = new FileOutputStream(file);
            InputStream isr = new BufferedInputStream(is);
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            int bytesReadTotal = 0;
            // write PDF to file
            while ((bytesRead = isr.read(buffer, 0, bufferSize)) != -1) {
                fos.write(buffer, 0, bytesRead);
                bytesReadTotal += bytesRead;
                buffer = new byte[bufferSize];
            }
            System.out.println("Wrote " + bytesReadTotal + " bytes.");
            fos.flush();
            fos.close();
            isr.close();
            Uri path = Uri.fromFile(file);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                showAlert("No application installed for viewing PDF. Please install one and try again");
            }
        }
        catch(IOException e){
            showAlert("There was an error opening the specified PDF");
        }
    }

    private void configAddFavButton(){
        final Button btn = (Button) findViewById(R.id.btn_favorite);
        btn.setText("Add favorite");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title == null || title.isEmpty()){
                    // item description is still loading. Ignore the button press!
                    return;
                }
                SQLController sqlController = new SQLController(ItemDescriptionActivity.this).open();
                sqlController.insertItem(type, title, author, description, pubDate, imageURL, pdfURL, ISBN);
                sqlController.close();
                configDeleteFavButton();
            }
        });
    }

    private void configDeleteFavButton(){
        final Button btn = (Button) findViewById(R.id.btn_favorite);
        btn.setText("Delete favorite");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title == null || title.isEmpty()){
                    // item description is still loading. Ignore the button press!
                    return;
                }
                SQLController sqlController = new SQLController(ItemDescriptionActivity.this).open();
                sqlController.deleteItem(title, ISBN);
                configAddFavButton();
            }
        });
    }

    private void configViewPDFButton(){
        Button viewPDFBtn = (Button) findViewById(R.id.btn_view_pdf);
        viewPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ItemDescriptionActivity.this.title == null || ItemDescriptionActivity.this.title.isEmpty()){
                    // item description is still loading. Ignore the button press!
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /*if (cookie == null || cookie.isEmpty()){
                            showAlert("You must be logged in to view PDFs");
                            return;
                        }*/
                        if (ISBN == null || ISBN.isEmpty()) {
                            showAlert("There is no PDF associated with this item");
                            return;
                        }
                        String [] possiblePdfURLs = {"http://cognet-staging2.mit.edu/system/cogfiles/books/" + ISBN + "/pdfs/" + ISBN + "_chap1.pdf",
                                "http://cognet-staging2.mit.edu/system/cogfiles/books/" + ISBN + "/pdfs/" + ISBN + "_chpt1.pdf",
                                "http://cognet-staging2.mit.edu/system/cogfiles/books/" + ISBN + "/pdfs/" + ISBN + "_chap14.pdf",
                                "http://cognet-staging2.mit.edu/system/cogfiles/books/" + ISBN + "/pdfs/" + ISBN + "_chapa.pdf",
                                "http://cognet-staging2.mit.edu/system/cogfiles/books/" + ISBN + "/pdfs/" + ISBN + "_chpti.1.pdf",
                                "http://cognet-staging2.mit.edu/system/cogfiles/books/" + ISBN + "/pdfs/" + ISBN + "_chap2.pdf",
                                "http://cognet-staging2.mit.edu/system/cogfiles/books/" + ISBN + "/pdfs/" + ISBN + "_1.pdf",
                                "http://cognet-staging2.mit.edu/system/cogfiles/books/" + ISBN + "/pdfs/" + ISBN + "_partI.pdf",
                        };
                        try {
                            int i=0;
                            boolean success = false;
                            while(i < possiblePdfURLs.length) {
                                HttpEntity entity = HttpRequests.getPDFresponse(possiblePdfURLs[i], ItemDescriptionActivity.this.cookie);
                                String contentType = entity.getContentType().getValue();
                                //System.out.println(contentType);
                                if (contentType.equals("application/pdf")) {
                                    downloadAndViewPDF(possiblePdfURLs[i]);
                                    ItemDescriptionActivity.this.pdfURL = possiblePdfURLs[i];
                                    success = true;
                                    break;
                                }
                                i++;
                            }
                            if (!success){
                                showAlert("Error viewing PDF, please make sure you are logged in!");
                            }
                        } catch (IOException e) {
                            showAlert("Error viewing PDF");
                            e.printStackTrace();
                        }
                    }
                }).start();
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

        title = extras.getString("title");
        author = extras.getString("author");
        pubDate = extras.getString("pubDate");
        description = extras.getString("description");

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
            this.ISBN = extras.getString("isbn");
            this.type = "book";
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
            // now that the image is loaded it is time to set up the rest views!
            // display the book description in the appropriate views
            initializeTextViews(extras);
            // check whether item exists in favorite list in DB
            SQLController sqlController = new SQLController(ItemDescriptionActivity.this).open();
            if (sqlController.exists(title, ISBN))
                configDeleteFavButton();
            else
                configAddFavButton();
            sqlController.close();
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
