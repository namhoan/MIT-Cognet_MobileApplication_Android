package com.cognet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by namhoan on 11/29/15.
 */
public class ItemsListSimpleAdapter extends SimpleAdapter{
/**
 * Constructor
 *
 * @param context  The context where the View associated with this SimpleAdapter is running
 * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
 * Maps contain the data for each row, and should include all the entries specified in
 * "from"
 * @param resource Resource identifier of a view layout that defines the views for this list
 * item. The layout file should include at least those named views defined in "to"
 * @param from     A list of column names that will be added to the Map associated with each
 * item.
 * @param to       The views that should display column in the "from" parameter. These should all be
 * TextViews. The first N views in this list are given the values of the first N columns
 */

    private List<HashMap<String, String>> listItems;
    private Context context;

    public ItemsListSimpleAdapter(Context context,List<?extends Map<String, ?>>data,int resource,String[]from,int[]to){
        super(context,data,resource,from,to);
        this.listItems = (List<HashMap<String, String>>) data;
        this.context = context;
    }

    public View getView(final int position,View convertView,ViewGroup parent){
        View view = super.getView(position, convertView, parent);

        if (convertView == null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> item = listItems.get(position);
                    System.out.println("Starting description activity for " + item.get("id") + " (pos "+position+")");
                    Intent intent = new Intent("android.intent.action.BOOK_DESCR");
                    Bundle extras = new Bundle();
                    extras.putString("title", item.get("tlt"));
                    extras.putString("author", item.get("aut"));
                    extras.putString("pubDate", item.get("pud"));
                    extras.putString("description", item.get("descr"));
                    intent.putExtras(extras);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });


        } else{
            view = convertView;
        }
        return view;
    }
}

