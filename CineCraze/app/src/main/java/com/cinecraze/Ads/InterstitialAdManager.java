package com.cinecraze.Ads;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class InterstitialAdManager {
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private InterstitialAd mInterstitialAd;
    private final Context context;
    private final AdListener adListener;

    public interface AdListener {
        void onAdClosed();
    }

    public InterstitialAdManager(Context context, AdListener adListener) {
        this.context = context;
        this.adListener = adListener;
        loadAd();
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                context,
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                if (adListener != null) {
                                    adListener.onAdClosed();
                                }
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }

    public void showAd(Activity activity) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
        } else {
            if (adListener != null) {
                adListener.onAdClosed();
            }
        }
    }
}
