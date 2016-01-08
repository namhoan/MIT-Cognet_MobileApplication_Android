package com.cognet;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.util.Log;

import com.cognet.XMLParser.BooksXMLListDataExtractor;
import com.cognet.XMLParser.JournalsXMLListDataExtractor;
import com.cognet.XMLParser.RefworksXMLListDataExtractor;
import com.cognet.XMLParser.XMLListDataExtractor;
import com.cognet.httprequests.HttpRequests;
import com.cognet.sqlite.SQLController;


public class ItemsListActivity extends ListActivity implements AbsListView.OnScrollListener {
    private SimpleAdapter adapter;
    private static final String JOURNALS_LIST_URL = "http://cognet-staging2.mit.edu/journal-list.xml";
    private static final String BOOKS_LIST_URL = "http://cognet-staging2.mit.edu/books-clone-list.xml";
    private static final String REFWORKS_LIST_URL = "http://cognet-staging2.mit.edu/eref-list.xml";

    private MyTask task;
    private TextView footer;
    private int TOTAL_ITEMS;
    private int number = 1;
    private int currentlistItemIndex = 0;
    private final int MAX_ITEMS_PER_PAGE = 20;
    private boolean isloading = false;
    private TextView header;

    EditText searchtext;
    ListView listView;

    int textlength = 0;

    protected String cookie;
    protected String token;

    ArrayList<String> text_sort = new ArrayList<String>();
    ArrayList<Integer> image_sort = new ArrayList<Integer>();

    List<HashMap<String, String>> items = null;
    List<HashMap<String, String>> loadedItems = null;
    List<HashMap<String, String>> loadedItemsBackground = null;
    List<HashMap<String, String>> resultItems = null;

    // Keys used in Hashmap
    final String[] from = {"tlt", "aut", "pud"};

    // Ids of views in listview_layout
    final int[] to = {R.id.text_tlt, R.id.text_aut, R.id.text_pud};



    //System.out.println("text tlt id: "+ R.id);

    public void refreshItemsList(final ItemsListActivity listActivity){
        SQLController sqlController = new SQLController(this).open();
        items = sqlController.getAllItems();
        sqlController.close();
        if (items == null){
            items = new ArrayList<HashMap<String, String>>();
        }
        TOTAL_ITEMS = items.size();
        System.out.println(" on refresh items list: Total Items: "+TOTAL_ITEMS);
        /*loadedItems = new ArrayList<HashMap<String, String>>();
        resultItems = new ArrayList<HashMap<String, String>>(items);
        // Getting a reference to listview of list_listview.xml layout file
        header = (TextView) listActivity.findViewById(R.id.text_items_list_header);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                footer = (TextView) inflater.inflate(R.layout.footer, null);
                listView = (ListView) listActivity.findViewById(android.R.id.list);
                listView.addFooterView(footer);
                adapter = new SimpleAdapter(getBaseContext(), loadedItems, R.layout.listview_layout, from, to);
                // Setting the adapter to the listView
                listView.setAdapter(adapter);
                listView.setOnScrollListener(listActivity);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println("List position clicked: "+ position);
                        HashMap<String, String> item = loadedItems.get(position);
                        System.out.println("Starting description activity for " + item.get("id") + " (pos "+position+")");
                        Intent intent = new Intent("android.intent.action.BOOK_DESCR");
                        Bundle extras = new Bundle();
                        extras.putString("title", item.get("tlt"));
                        extras.putString("author", item.get("aut"));
                        extras.putString("pubDate", item.get("pud"));
                        extras.putString("description", item.get("descr"));
                        extras.putString("cookie", ItemsListActivity.this.cookie);
                        extras.putString("token", ItemsListActivity.this.token);
                        extras.putString("image", item.get("image"));
                        extras.putString("isbn", item.get("isbn"));
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                });
            }
        });
        if(task != null && (task.getStatus() == AsyncTask.Status.FINISHED)) {
            task = new MyTask();
            task.execute();
        }*/
    }

    public void initializeItemsList(final ItemsListActivity listActivity, boolean firstCall) {
        Log.d("initializeItemsList", "initializeItemsList starting...");
        // read local .xml
        //InputStream is = getResources().openRawResource(R.raw.books_list);
        // retrieve books list from server
        InputStream is = null;
        try {
            if (listActivity instanceof BooksListActivity) {
                is = HttpRequests.getInpuStream(BOOKS_LIST_URL);
            }
            else if (listActivity instanceof JournalsListActivity){
                is = HttpRequests.getInpuStream(JOURNALS_LIST_URL);
            }
            else if (listActivity instanceof RefworksListActivity){
                is = HttpRequests.getInpuStream(REFWORKS_LIST_URL);
            }
            System.out.println(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is != null) {
            XMLListDataExtractor extractor = null;
            if (listActivity instanceof BooksListActivity){
                extractor = new BooksXMLListDataExtractor(is);
            }
            else if(listActivity instanceof JournalsListActivity){
                extractor = new JournalsXMLListDataExtractor(is);
            }
            else if (listActivity instanceof RefworksListActivity){
                extractor = new RefworksXMLListDataExtractor(is);
            }

            try {
                items = extractor.extract();
                TOTAL_ITEMS = items.size();
                is.close();
            } catch (Exception e) {
                System.err.println("Exception while reading .xml file");
            }
        }



        // get contents from DB if current list activity is instance of Favorites list activity
        if (listActivity instanceof FavoritesListActivity){
            refreshItemsList(listActivity);
        }



        loadedItems = new ArrayList<HashMap<String, String>>();
        resultItems = new ArrayList<HashMap<String, String>>(items);
        // Getting a reference to listview of list_listview.xml layout file
        header = (TextView) listActivity.findViewById(R.id.text_items_list_header);
        currentlistItemIndex = 0;

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                footer = (TextView) inflater.inflate(R.layout.footer, null);
                listView = (ListView) listActivity.findViewById(android.R.id.list);
                listView.addFooterView(footer);
                adapter = new SimpleAdapter(getBaseContext(), loadedItems, R.layout.listview_layout, from, to);
                // Setting the adapter to the listView
                listView.setAdapter(adapter);
                listView.setOnScrollListener(listActivity);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println("List position clicked: "+ position);
                        HashMap<String, String> item = loadedItems.get(position);
                        System.out.println("Starting description activity for " + item.get("id") + " (pos "+position+")");
                        Intent intent = new Intent("android.intent.action.BOOK_DESCR");
                        Bundle extras = new Bundle();
                        extras.putString("title", item.get("tlt"));
                        extras.putString("author", item.get("aut"));
                        extras.putString("pubDate", item.get("pud"));
                        extras.putString("description", item.get("descr"));
                        extras.putString("cookie", ItemsListActivity.this.cookie);
                        extras.putString("token", ItemsListActivity.this.token);
                        extras.putString("image", item.get("image"));
                        extras.putString("isbn", item.get("isbn"));
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                });
            }
        });

        Log.d("initializeItemsList", "initializeItemsList is calling background task");
        if(firstCall) {
            task = new MyTask();
            task.execute();
        }
        else{
            if (task != null && (task.getStatus() == AsyncTask.Status.FINISHED)) {
                task = new MyTask();
                task.execute();
            }
        }

        searchtext = (EditText) listActivity.findViewById(R.id.edit_text_item_search);

        runOnUiThread(new Runnable() {
            public void run() {
                searchtext.clearFocus();
            }
        });

        searchtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*if (s.length() < 3) {
                    return;
                }*/
                boolean inserted;

                textlength = searchtext.getText().length();
                text_sort.clear();
                image_sort.clear();

                ArrayList<String> titles = new ArrayList<String>();
                ArrayList<String> authors = new ArrayList<String>();

                loadedItems = new ArrayList<HashMap<String, String>>();
                resultItems = new ArrayList<HashMap<String, String>>();
                currentlistItemIndex = 0;

                for (HashMap<String, String> item : items) {
                    inserted = false;
                    String title = item.get("tlt");
                    String author = item.get("aut");
                    String searchText = searchtext.getText()
                            .toString();
                    // search for title
                    if (textlength <= title.length()) {
                        if (title.toLowerCase().indexOf
                                (searchText.toLowerCase()) != -1) {
                            resultItems.add(item);
                            inserted = true;
                        }
                    }
                    // for now Journals don't have author
                    if (author != null) {
                        // search for author
                        if (textlength <= title.length() && !inserted) {
                            if (author.toLowerCase().indexOf
                                    (searchText.toLowerCase()) != -1) {
                                resultItems.add(item);
                            }
                        }
                    }
                }

                TOTAL_ITEMS = resultItems.size();
                System.out.println("New text: "+ s + ", Total items: " + TOTAL_ITEMS);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //listView.addFooterView(footer);
                        listView.setOnScrollListener(listActivity);
                        adapter = new SimpleAdapter(getBaseContext(), loadedItems, R.layout.listview_layout, from, to);
                        listView.setAdapter(adapter);
                        //adapter.notifyDataSetChanged();
                        //listView.requestLayout();
                    }
                });

                if(task != null && (task.getStatus() == AsyncTask.Status.FINISHED)) {
                    Log.d("onTextChanged", "onTextChanged is calling background task");
                    task = new MyTask();
                    task.execute();
                }
            }
        });

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int loadedItems = firstVisibleItem + visibleItemCount;
        Log.d("onScroll", "loaded items: " + loadedItems + ", total item count: " + totalItemCount + ", visible item count: " + visibleItemCount);
        if((loadedItems == totalItemCount) && /*loadedItems < resultBooks.size() &&*/ !isloading ){
            if(task != null && (task.getStatus() == AsyncTask.Status.FINISHED)){
                Log.d("onScroll", "onScroll starts a background task, task status: "+task.getStatus());
                task = new MyTask();
                task.execute();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        /* loads next items for the listview */
        @Override
        protected Void doInBackground(Void... params) {
            isloading = true;
            loadedItemsBackground = new ArrayList<HashMap<String, String>>(loadedItems);
            if (TOTAL_ITEMS > currentlistItemIndex) {
                for (int i = 1; i <= MAX_ITEMS_PER_PAGE && currentlistItemIndex< resultItems.size(); i++) {
                    Log.d("doInBackground", "adding book from index " + currentlistItemIndex);
                    loadedItemsBackground.add(resultItems.get(currentlistItemIndex));
                    currentlistItemIndex += 1;
                }
            }
            return null;
        }
        /* event to notify that the background thread is done. It is running in the foreground */
        @Override
        protected void onPostExecute(Void result) {
            isloading = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadedItems.clear();
                    loadedItems.addAll(loadedItemsBackground);
                    adapter.notifyDataSetChanged();
                    if (adapter.getCount() == TOTAL_ITEMS) {
                        header.setText("All " + adapter.getCount() + " Items are loaded.");
                        listView.setOnScrollListener(null);
                        listView.removeFooterView(footer);
                    } else {
                        header.setText("Loaded items - " + adapter.getCount() + " out of " + TOTAL_ITEMS);
                    }
                }
            });

        }
    }
}

