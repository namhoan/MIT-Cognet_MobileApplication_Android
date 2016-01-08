package com.cognet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;

/**
 * Created by dimos on 10/19/15.
 */
public abstract class XMLListDataExtractor {
    File f;
    protected static final String LIST_ITEM_BEGIN = "<item>";
    protected static final String LIST_ITEM_END = "</item>";

    public abstract List<HashMap<String,String>> extract() throws FileNotFoundException, IOException;
}
