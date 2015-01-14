package com.jada.jada;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jada.jada.database.RSSDatabaseHandler;
import com.jada.jada.helper.RSSParser;
import com.jada.jada.model.RSSFeed;
import com.jada.jada.model.WebSite;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddWebsiteFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    Button btnSubmit;
    Button btnCancel;
    EditText txtUrl;
    TextView lblMessage;

    RSSParser rssParser = new RSSParser();

    RSSFeed rssFeed;

    // Progress Dialog
    private ProgressDialog pDialog;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AddWebsiteFragment newInstance(int sectionNumber) {
        AddWebsiteFragment fragment = new AddWebsiteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_website, container, false);
        // buttons
        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        txtUrl = (EditText) rootView.findViewById(R.id.txtUrl);
        lblMessage = (TextView) rootView.findViewById(R.id.lblMessage);

        // Submit button click event
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String url = txtUrl.getText().toString();

                // Validation url
                Log.d("URL Length", "" + url.length());
                // check if user entered any data in EditText
                if (url.length() > 0) {
                    lblMessage.setText("");
                    String urlPattern = "^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
                    if (url.matches(urlPattern)) {
                        // valid url
                        new loadRSSFeed().execute(url);
                    } else {
                        // URL not valid
                        lblMessage.setText("Please enter a valid url");
                    }
                } else {
                    // Please enter url
                    lblMessage.setText("Please enter website url");
                }

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

    /**
     * Background Async Task to get RSS data from URL
     * */
    class loadRSSFeed extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching RSS Information");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Inbox JSON
         * */
        @Override
        protected String doInBackground(String... args) {
            String url = args[0];
            rssFeed = rssParser.getRSSFeed(url);
            Log.d("rssFeed", " " + rssFeed);
            if (rssFeed != null) {
                Log.e("RSS URL",
                        rssFeed.getTitle() + "" + rssFeed.getLink() + ""
                                + rssFeed.getDescription() + ""
                                + rssFeed.getLanguage());
                RSSDatabaseHandler rssDb = new RSSDatabaseHandler(
                        getActivity().getApplicationContext());
                WebSite site = new WebSite(rssFeed.getTitle(), rssFeed.getLink(), rssFeed.getRSSLink(),
                        rssFeed.getDescription(), getResources().getString(R.string.app_name));
                rssDb.addSite(site);
                Intent i = getActivity().getIntent();
                // send result code 100 to notify about product update
                getActivity().setResult(100, i);
                //finish();
            } else {
                // updating UI from Background Thread
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        lblMessage.setText("Rss url not found. Please check the url or try again");
                    }
                });
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String args) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (rssFeed != null) {

                    }

                }
            });

        }

    }
}
