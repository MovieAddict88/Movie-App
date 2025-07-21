package com.cinecraze;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.cinecraze.Ads.AppOpenManager;
import com.cinecraze.Ads.InterstitialAdManager;
import com.cinecraze.Ads.VideoRewardAdManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private AdView mAdView;
    private AppOpenManager appOpenManager;
    private InterstitialAdManager interstitialAdManager;
    private VideoRewardAdManager videoRewardAdManager;
    private static MainActivity instance;

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

        MobileAds.initialize(this, initializationStatus -> {
        });

        appOpenManager = new AppOpenManager(this);
        appOpenManager.fetchAd();

        interstitialAdManager = new InterstitialAdManager(this, this::finish);
        videoRewardAdManager = new VideoRewardAdManager(this, new VideoRewardAdManager.AdListener() {
            @Override
            public void onAdClosed() {
                // Handle ad closed event
            }

            @Override
            public void onUserEarnedReward() {
                // Handle user earned reward event
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
                    .onPositiveClicked(dialog -> interstitialAdManager.showAd(this))
                    .onNegativeClicked(dialog -> {
                        // do nothing
                    })
                    .build()
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appOpenManager.showAdIfAvailable();
    }

    public static MainActivity getInstance() {
        return (MainActivity) instance;
    }
}