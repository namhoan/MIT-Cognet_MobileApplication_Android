package com.cognet.XMLParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dimos on 10/19/15.
 */
public class BooksXMLListDataExtractor extends XMLListDataExtractor {

    //private static final String
    InputStream is;

    private static final String TITLE_BEGIN = "<title>";
    private static final String TITLE_END = "</title>";
    private static final String AUTHOR_BEGIN = "<author>";
    private static final String AUTHOR_END = "</author>";
    private static final String PUBDATE_BEGIN = "<pubDate>";
    private static final String PUBDATE_END = "</pubDate>";
    private static final String DESCR_BEGIN = "<description>";
    private static final String DESCR_END = "</description>";
    private static final String IMAGE_BEGIN = "<media:content";

    public BooksXMLListDataExtractor(InputStream is){
        this.is = is;
    }

    public List<HashMap<String,String>> extract() throws FileNotFoundException, IOException{
        HashMap<String,String> bookMap = new HashMap<String,String>();
        List<HashMap<String,String>> books = new ArrayList<HashMap<String,String>>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        boolean gettingDescription = false;
        String description = "";


        while ( (line = br.readLine()) != null  ) {
            if (line.contains(LIST_ITEM_BEGIN)){
                bookMap = new HashMap<String,String>();
            }
            if (line.contains(TITLE_BEGIN)){
                String title = line.split(TITLE_BEGIN)[1].split(TITLE_END)[0];
                //System.out.println(title);
                bookMap.put("tlt", title);
            }
            if (line.contains(AUTHOR_BEGIN)){
                String author = line.split(AUTHOR_BEGIN)[1].split(AUTHOR_END)[0];
                //System.out.println(author);
                bookMap.put("aut", author);
            }
            if (line.contains(PUBDATE_BEGIN)){
                String pubDate = line.split(PUBDATE_BEGIN)[1].split(PUBDATE_END)[0];
                //System.out.println(pubDate);
                bookMap.put("pud", pubDate);
            }
            if(gettingDescription){
                if (line.split(DESCR_END).length > 0) {
                    description += line.split(DESCR_END)[0];
                }
                else if (!(line.contains(DESCR_END))){
                    description += line;
                }
            }
            if(line.contains(DESCR_BEGIN)){
                gettingDescription = true;
                if (line.split(DESCR_BEGIN).length > 1  ) {
                    description += line.split(DESCR_BEGIN)[1];
                }
            }
            if (line.contains(DESCR_END)){
                if (line.split(DESCR_END).length > 0 ) {
                    description += line.split(DESCR_END)[0];
                }
                bookMap.put("descr", description);
                description = "";
                gettingDescription = false;
            }
            if (line.contains(IMAGE_BEGIN)){
                String ISBN = "";
                if (line.split("url=").length > 1 && line.split("fileSize").length > 0){
                    String imageURL = line.split("url=")[1].split("fileSize")[0].replace("\"", "");
                    bookMap.put("image", imageURL);
                    // extract PDF link from jpg name (ISBN)
                    imageURL = imageURL.toLowerCase();
                    if(imageURL.split(".jpg").length > 0 && imageURL.split("/").length > 1 ){
                        ISBN = imageURL.split(".jpg")[0].split("/")[imageURL.split(".jpg")[0].split("/").length-1];
                        if (ISBN.split("_").length > 1 ){
                            ISBN = ISBN.split("_")[0];
                        }
                        //System.out.println("ISBN : " + ISBN);
                    }
                    if (ISBN != ""){
                        bookMap.put("isbn", ISBN);
                    }
                }
            }
            if (line.contains(LIST_ITEM_END)){
                bookMap.put("id", "" + books.size());
                books.add(bookMap);
            }
        }
        return books;
    }
}
