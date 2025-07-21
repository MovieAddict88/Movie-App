package com.cinecraze;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class CustomWebChromeClient extends WebChromeClient {

    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    protected FrameLayout fullscreenContainer;
    private int originalOrientation;
    private int originalSystemUiVisibility;

    public CustomWebChromeClient() {}

    @Override
    public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
        if (this.customView != null) {
            onHideCustomView();
            return;
        }
        this.customView = paramView;
        this.originalSystemUiVisibility = MainActivity.getInstance().getWindow().getDecorView().getSystemUiVisibility();
        this.originalOrientation = MainActivity.getInstance().getRequestedOrientation();
        this.customViewCallback = paramCustomViewCallback;
        ((FrameLayout) MainActivity.getInstance().getWindow().getDecorView()).addView(this.customView, new FrameLayout.LayoutParams(-1, -1));
        MainActivity.getInstance().getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        MainActivity.getInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onHideCustomView() {
        if (customView == null) return;
        ((FrameLayout) MainActivity.getInstance().getWindow().getDecorView()).removeView(this.customView);
        this.customView = null;
        MainActivity.getInstance().getWindow().getDecorView().setSystemUiVisibility(this.originalSystemUiVisibility);
        MainActivity.getInstance().setRequestedOrientation(this.originalOrientation);
        if (this.customViewCallback != null) {
            this.customViewCallback.onCustomViewHidden();
        }
        this.customViewCallback = null;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        // Allow video popups but block others
        WebView.HitTestResult result = view.getHitTestResult();
        if (result.getType() == WebView.HitTestResult.UNKNOWN_TYPE) {
            return false; // Block ad popups
        }
        
        // Handle video player windows
        WebView newWebView = new WebView(view.getContext());
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.video_dialog);
        WebView videoWebView = dialog.findViewById(R.id.video_webview);
        videoWebView.setWebViewClient(new VideoWebViewClient());
        videoWebView.getSettings().setJavaScriptEnabled(true);
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(videoWebView);
        resultMsg.sendToTarget();
        dialog.show();
        dialog.findViewById(R.id.close_button).setOnClickListener(v -> dialog.dismiss());
        return true;
    }
}