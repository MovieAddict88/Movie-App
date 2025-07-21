package com.cinecraze;

import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AdBlockerWebViewClient extends WebViewClient {

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (AdBlocker.isAd(url)) {
            return new WebResourceResponse("text/plain", "UTF-8", null);
        }
        return super.shouldInterceptRequest(view, url);
    }
}
