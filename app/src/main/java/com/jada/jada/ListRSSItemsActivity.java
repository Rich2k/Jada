package com.jada.jada;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jada.jada.database.RSSDatabaseHandler;
import com.jada.jada.helper.RSSParser;
import com.jada.jada.model.RSSFeed;
import com.jada.jada.model.RSSItem;
import com.jada.jada.model.WebSite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListRSSItemsActivity extends ActionBarActivity{
    String LOG_LISTRSSITEMSACTIVITY = "ListRSSItemsActivity";

    private static String TAG_ID = "id";
    private static String TAG_TITLE = "title";
    private static String TAG_LINK = "link";
    private static String TAG_DESRIPTION = "description";
    private static String TAG_PUB_DATE = "pubDate";
    private static String TAG_GUID = "guid"; // not used
    // Array list for list view
    ArrayList<HashMap<String, String>> rssItemList = new ArrayList<HashMap<String, String>>();
    RSSParser rssParser = new RSSParser();
    List<RSSItem> rssItems = new ArrayList<RSSItem>();
    RSSFeed rssFeed;
    // Progress Dialog
    private ProgressDialog pDialog;
    private ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rss_item_list);

        // get intent data
        Intent i = getIntent();

        // SQLite Row id
        Integer site_id = Integer.parseInt(i.getStringExtra(TAG_ID));

        // Getting Single website from SQLite
        RSSDatabaseHandler rssDB = new RSSDatabaseHandler(getApplicationContext());


        WebSite site = rssDB.getSite(site_id);
        String rss_link = site.getRSSLink();
        Log.d(LOG_LISTRSSITEMSACTIVITY, "RSS Link: " + rss_link);
        /**
         * Calling a backgroung thread will loads recent articles of a website
         * @param rss url of website
         * */
        new loadRSSFeedItems().execute(rss_link);

        // selecting single ListView item
        lv = (ListView)findViewById(R.id.rss_list);

        // Launching new screen on Selecting Single ListItem
        lv.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent in = new Intent(getApplicationContext(), DisplayWebPageActivity.class);

                // getting page url
                String page_url = ((TextView) view.findViewById(R.id.page_url)).getText().toString();
                Toast.makeText(getApplicationContext(), page_url, Toast.LENGTH_SHORT).show();
                in.putExtra("page_url", page_url);
                startActivity(in);
            }
        });
    }

    /**
     * Background Async Task to get RSS Feed Items data from URL
     */
    class loadRSSFeedItems extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(
                    ListRSSItemsActivity.this);
            pDialog.setMessage("Loading recent articles");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting all recent articles and showing them in listview
         */
        @Override
        protected String doInBackground(String... args) {
            // rss link url
            String rss_url = args[0];
            Log.d(LOG_LISTRSSITEMSACTIVITY, "RSS URL: " + rss_url);
            // list of rss items
            rssItems = rssParser.getRSSFeedItems(rss_url);

            // looping through each item
            for (RSSItem item : rssItems) {
                // creating new HashMap
                HashMap<String, String> map = new HashMap<String, String>();

                // adding each child node to HashMap key => value
                map.put(TAG_TITLE, item.getTitle());
                map.put(TAG_LINK, item.getLink());
                map.put(TAG_PUB_DATE, item.getPubdate());
                String description = item.getDescription();
                // taking only 200 chars from description
                if (description.length() > 100) {
                    description = description.substring(0, 97) + "..";
                }
                map.put(TAG_DESRIPTION, description);

                // adding HashList to ArrayList
                rssItemList.add(map);
            }

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed items into listview
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            ListRSSItemsActivity.this,
                            rssItemList, R.layout.rss_item_list_row,
                            new String[]{TAG_LINK, TAG_TITLE, TAG_PUB_DATE, TAG_DESRIPTION},
                            new int[]{R.id.page_url, R.id.title, R.id.pub_date, R.id.link});

                    // updating listview
                    lv.setAdapter(adapter);
                }
            });
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String args) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }
    }
}