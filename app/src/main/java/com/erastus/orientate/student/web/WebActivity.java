package com.erastus.orientate.student.web;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.erastus.orientate.databinding.ActivityWebBinding;

public class WebActivity extends AppCompatActivity {
    public static final String DEFAULT_PAGE = "https://www.google.com";
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityWebBinding webBinding = ActivityWebBinding.inflate(getLayoutInflater());

        setContentView(webBinding.getRoot());

        mWebView = webBinding.webViewCustom;
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                mWebView.loadUrl(request.getUrl().toString());
                return false;
            }
        });
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