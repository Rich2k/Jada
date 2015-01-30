package com.jada.jada;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jada.jada.database.RSSDatabaseHandler;
import com.jada.jada.model.WebSite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class WebsiteFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static String TAG_ID = "id";
    public static String TAG_TITLE = "title";
    public static String TAG_DESC = "desc";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static String FRAGMENT_CATEGORY = "fragment_category";
    // Array list for list view
    ArrayList<HashMap<String, String>> rssFeedList;
    // array to trace sqlite ids
    String[] sqliteIds;
    // List view
    ListView lv = null;
    // Progress Dialog
    private ProgressDialog pDialog;

    private LoadStoreSites loadStoreSites = null;

    public WebsiteFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WebsiteFragment newInstance(int sectionNumber) {
        WebsiteFragment fragment = new WebsiteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Response from AddNewSiteActivity.java
     * if response is 100 means new site is added to sqlite
     * reload this activity again to show
     * newly added website in listview
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // reload this screen again
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            startActivity(intent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        // Hashmap for ListView
        rssFeedList = new ArrayList<>();
        /**
         * Calling a background thread which will load
         * web sites stored in SQLite database
         * */
        loadStoreSites = new LoadStoreSites(this);
        loadStoreSites.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.site_list, container, false);

        String[] categories = getActivity().getResources().getStringArray(R.array.categories);
        FRAGMENT_CATEGORY = categories[getArguments().getInt(ARG_SECTION_NUMBER)];
        // selecting single ListView item
        lv = (ListView) rootView.findViewById(R.id.site_list);
        // Launching new screen on Selecting Single ListItem
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String sqlite_id = ((TextView) view.findViewById(R.id.sqlite_id)).getText().toString();
                // Starting new intent
                Intent in = new Intent(getActivity().getApplicationContext(), ListRSSItemsActivity.class);
                // passing sqlite row id
                in.putExtra(TAG_ID, sqlite_id);
                startActivity(in);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loadStoreSites.websiteFragment = null;
    }

    /**
     * Background Async Task to get RSS data from URL
     */
    static class LoadStoreSites extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        WebsiteFragment websiteFragment = null;

        public LoadStoreSites(WebsiteFragment websiteFragment) {
            super();
            this.websiteFragment = websiteFragment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();/*
            websiteFragment.pDialog = new ProgressDialog(websiteFragment.getActivity());
            websiteFragment.pDialog.setMessage("Fetching RSS Information");
            websiteFragment.pDialog.setIndeterminate(false);
            websiteFragment.pDialog.setCancelable(false);
            websiteFragment.pDialog.show();*/
        }

        /**
         * getting all stored website from SQLite
         */
        @Override
        protected String doInBackground(String... args) {
            // updating UI from Background Thread
            websiteFragment.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    RSSDatabaseHandler rssDb = new RSSDatabaseHandler(
                            websiteFragment.getActivity().getApplicationContext());
                    // listing all websites from SQLite
                    List<WebSite> siteList = rssDb.getAllCategorySites(FRAGMENT_CATEGORY);

                    websiteFragment.sqliteIds = new String[siteList.size()];

                    // loop through each website
                    for (int i = 0; i < siteList.size(); i++) {

                        WebSite s = siteList.get(i);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, s.getId().toString());
                        map.put(TAG_TITLE, s.getTitle());
                        map.put(TAG_DESC, Html.fromHtml(s.getDescription()).toString());

                        // adding HashList to ArrayList
                        websiteFragment.rssFeedList.add(map);

                        // add sqlite id to array
                        // used when deleting a website from sqlite
                        websiteFragment.sqliteIds[i] = s.getId().toString();
                    }
                }
            });
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(String args) {
            if (websiteFragment != null) {
                websiteFragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * Updating list view with websites
                         * */
                        ListAdapter adapter = new SimpleAdapter(
                                websiteFragment.getActivity(),
                                websiteFragment.rssFeedList, R.layout.site_list_row,
                                new String[]{TAG_ID, TAG_TITLE, TAG_DESC},
                                new int[]{R.id.sqlite_id, R.id.title, R.id.desc});
                        // updating listview
                        websiteFragment.lv.setAdapter(adapter);
                        //websiteFragment.pDialog.dismiss();
                    }
                });
            }
        }

    }
}
