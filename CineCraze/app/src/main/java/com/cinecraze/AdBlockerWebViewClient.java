package com.cinecraze;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdBlockerWebViewClient extends WebViewClient {

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(@NonNull WebView view, @NonNull WebResourceRequest request) {
        String url = request.getUrl().toString();
        if (AdBlocker.isAd(url)) {
            return new WebResourceResponse("text/plain", "UTF-8", null);
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        injectVideoSupportScript(view);
    }

    private void injectVideoSupportScript(WebView view) {
        view.evaluateJavascript(
            "(function() {" +
            "   // Remove ad elements without touching video containers" +
            "   const adSelectors = [" +
            "       'div[class*=\"ad\"]', " +
            "       'div[id*=\"ad\"]'," +
            "       'div[class*=\"banner\"]'," +
            "       'iframe[src*=\"ad\"]'" +
            "   ];" +
            "   adSelectors.forEach(selector => {" +
            "       document.querySelectorAll(selector).forEach(el => {" +
            "           if (!el.closest('.video-container')) {" +
            "               el.remove();" +
            "           }" +
            "       });" +
            "   });" +
            
            "   // Ensure video containers are visible" +
            "   document.querySelectorAll('.video-container').forEach(container => {" +
            "       container.style.zIndex = '9999';" +
            "       container.style.display = 'block';" +
            "   });" +
            
            "   // Fix for video players" +
            "   const videoPlayers = document.querySelectorAll('video');" +
            "   videoPlayers.forEach(player => {" +
            "       player.style.zIndex = '10000';" +
            "       player.style.position = 'relative';" +
            "   });" +
            "})();", 
            null
        );
    }
}