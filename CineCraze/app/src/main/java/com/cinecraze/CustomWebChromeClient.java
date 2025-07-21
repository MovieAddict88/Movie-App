package com.cinecraze;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class CustomWebChromeClient extends WebChromeClient {

    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    protected FrameLayout fullscreenContainer;
    private int originalOrientation;
    private int originalSystemUiVisibility;

    public CustomWebChromeClient() {}

    @Override
    public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
    {
        if (this.customView != null)
        {
            onHideCustomView();
            return;
        }
        this.customView = paramView;
        this.originalSystemUiVisibility = MainActivity.getInstance().getWindow().getDecorView().getSystemUiVisibility();
        this.originalOrientation = MainActivity.getInstance().getRequestedOrientation();
        this.customViewCallback = paramCustomViewCallback;
        ((FrameLayout)MainActivity.getInstance().getWindow().getDecorView()).addView(this.customView, new FrameLayout.LayoutParams(-1, -1));
        MainActivity.getInstance().getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        MainActivity.getInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onHideCustomView()
    {
        ((FrameLayout)MainActivity.getInstance().getWindow().getDecorView()).removeView(this.customView);
        this.customView = null;
        MainActivity.getInstance().getWindow().getDecorView().setSystemUiVisibility(this.originalSystemUiVisibility);
        MainActivity.getInstance().setRequestedOrientation(this.originalOrientation);
        this.customViewCallback.onCustomViewHidden();
        this.customViewCallback = null;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        WebView newWebView = new WebView(view.getContext());
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.ad_dialog);
        WebView adWebView = dialog.findViewById(R.id.ad_webview);
        adWebView.setWebViewClient(new WebViewClient());
        adWebView.getSettings().setJavaScriptEnabled(true);
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(adWebView);
        resultMsg.sendToTarget();
        dialog.show();
        dialog.findViewById(R.id.close_button).setOnClickListener(v -> dialog.dismiss());
        return true;
    }
}
