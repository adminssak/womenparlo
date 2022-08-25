package com.smartwebarts.rogrows;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.smartwebarts.rogrows.dashboard.DashboardActivity;
import com.smartwebarts.rogrows.database.DatabaseClient;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.utils.ApplicationConstants;

import java.util.Objects;

public class InstaPayWebViewActivity extends AppCompatActivity {

    public static final String KEY_URL = "url";

    private WebView webView;
    private WebSettings settings;
    private ProgressBar progressBar;
    private Uri uri;
    private String mSuccessUrl = ApplicationConstants.INSTANCE.SUCCESS_URL;
    private String mFailedUrl = ApplicationConstants.INSTANCE.FAILED_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_pay_web_view);

        String url = Objects.requireNonNull(getIntent().getExtras()).getString(KEY_URL, "");
        Log.e("Url",url);
        uri = Uri.parse(url);
        init();
        loadUrl();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadUrl() {
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
            webView.setWebViewClient(new MyBrowser());
            settings.setLoadsImagesAutomatically(true);
            settings.setJavaScriptEnabled(true);
            settings.setAllowFileAccess(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.clearCache(true);
            webView.loadUrl(uri.toString());
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    private void init() {
        webView = findViewById(R.id.webview);
        settings = webView.getSettings();
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("WebViewUrl", url);

            if (url.contains(mSuccessUrl)) {

                String oid = url.split("=")[1];
                Intent intent = new Intent();
                intent.putExtra("MESSAGE","success");
                intent.putExtra("oid", oid);
                setResult(1234,intent);
                finish();
            } else if (url.contains(mFailedUrl)) {
                Intent intent = new Intent();
                intent.putExtra("MESSAGE","failed");
                setResult(1234,intent);
                finish();
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    private void deleteAll() {
        class DeleteAll extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                DatabaseClient.
                        getmInstance(InstaPayWebViewActivity.this)
                        .getAppDatabase()
                        .taskDao()
                        .deleteAll();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                startActivity( new Intent(InstaPayWebViewActivity.this, DashboardActivity.class));
                finishAffinity();
            }
        }

        new DeleteAll().execute();
    }
}