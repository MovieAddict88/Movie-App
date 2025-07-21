package com.cinecraze;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Animation;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static MainActivity instance;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the app fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);
        instance = this;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Initialize ad blocker before WebView
        AdBlocker.init(this);

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new AdBlockerWebViewClient());
        webView.setWebChromeClient(new CustomWebChromeClient());
        
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportMultipleWindows(true); // Changed to true for video popups
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setLoadsImagesAutomatically(true);
        
        // Important for video playback
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        webView.loadUrl("https://movie-fcs.fwh.is/cini/");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            FancyAlertDialog.Builder.with(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeBtnText("Cancel")
                    .setPositiveBtnText("Exit")
                    .setPositiveBtnBackgroundRes(R.color.colorPrimary)
                    .setNegativeBtnBackgroundRes(R.color.colorPrimaryDark)
                    .setAnimation(Animation.POP)
                    .isCancellable(true)
                    .setIcon(R.drawable.ic_baseline_exit_to_app_24, View.VISIBLE)
                    .onPositiveClicked(dialog -> finish())
                    .onNegativeClicked(dialog -> {
                        // do nothing
                    })
                    .build()
                    .show();
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }
}