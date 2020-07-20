package com.erastus.orientate.student.web;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.erastus.orientate.R;
import com.erastus.orientate.databinding.ActivityWebBinding;

public class WebActivity extends AppCompatActivity {
    public static final String DEFAULT_PAGE = "https//:www.google.com";
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityWebBinding webBinding = ActivityWebBinding.inflate(getLayoutInflater());

        setContentView(webBinding.getRoot());

        mWebView = webBinding.webViewCustom;
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(DEFAULT_PAGE);
    }

    public void OnBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}