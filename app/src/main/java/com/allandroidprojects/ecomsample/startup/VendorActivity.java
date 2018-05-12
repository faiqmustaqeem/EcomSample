package com.allandroidprojects.ecomsample.startup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.allandroidprojects.ecomsample.R;

public class VendorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);

        WebView webView;
        webView = (WebView)findViewById(R.id.help_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://preloveddesigners.com/become-a-vendor/");
    }
}
