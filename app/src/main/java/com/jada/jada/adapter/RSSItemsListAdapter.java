package com.jada.jada.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jada.jada.helper.CustomFontLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jaimin on 31-01-2015.
 */
public class RSSItemsListAdapter extends BaseAdapter {
    Context context;
    ArrayList<HashMap<String, String>> rssItemList;
    int rss_item_list_row;
    String []keys;
    int []viewIds;
    public RSSItemsListAdapter(Context context, ArrayList<HashMap<String,String>> rssItemList, int rss_item_list_row, String[] keys, int[] viewIds) {
        this.context = context;
        this.rssItemList = rssItemList;
        this.rss_item_list_row = rss_item_list_row;
        this.keys = keys;
        this.viewIds = viewIds;
    }

    @Override
    public int getCount() {
        return rssItemList.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return rssItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(rss_item_list_row, null);
        }
        for(int i=0;i<viewIds.length;i++){
            TextView txtView = (TextView)convertView.findViewById(viewIds[i]);
            txtView.setText(rssItemList.get(position).get(keys[i]).trim());
            if(keys[i] == "title"){
                Typeface tf = CustomFontLoader.getTypeface(context,
                        CustomFontLoader.ROBOTO_MEDIUM);
                txtView.setTypeface(tf);
            }
        }
        return convertView;
    }
}
