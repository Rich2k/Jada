package com.jada.jada;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DisplayWebPageActivity extends ActionBarActivity {

    WebView webview;
    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Intent in = getIntent();
        String page_url = in.getStringExtra("page_url");
        String page_title = in.getStringExtra("page_title");
        getSupportActionBar().setTitle(page_title);
        webview = (WebView) findViewById(R.id.webpage);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.loadUrl(page_url);
        webview.setWebViewClient(new DisPlayWebPageActivityClient());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display_web_page, menu);

        // Set up ShareActionProvider's default share intent
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setShareIntent(getDefaultIntent());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_open_browser:
                Uri webpage = Uri.parse(webview.getUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(webIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Defines a default (dummy) share intent to initialize the action provider.
     * However, as soon as the actual content to be used in the intent
     * is known or changes, you must update the share intent by again calling
     * mShareActionProvider.setShareIntent()
     */
    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, webview.getUrl());
        intent.setType("text/plain");
        return intent;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class DisPlayWebPageActivityClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}