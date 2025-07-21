package com.cinecraze;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class VideoWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        // Allow all video player URLs to load
        view.loadUrl(request.getUrl().toString());
        return true;
    }
}