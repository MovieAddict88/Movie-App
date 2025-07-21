package com.cinecraze.Ads;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class VideoRewardAdManager {
    private RewardedAd mRewardedAd;
    private final Context context;
    private final AdListener adListener;

    public interface AdListener {
        void onAdClosed();
        void onUserEarnedReward();
    }

    public VideoRewardAdManager(Context context, AdListener adListener) {
        this.context = context;
        this.adListener = adListener;
        loadAd();
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(
                context,
                AdIdManager.getRewardedVideoAdUnitId(),
                adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
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
                        mRewardedAd = null;
                    }
                });
    }

    public void showAd(Activity activity) {
        if (mRewardedAd != null) {
            mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(com.google.android.gms.ads.rewarded.RewardItem rewardItem) {
                    if (adListener != null) {
                        adListener.onUserEarnedReward();
                    }
                }
            });
        } else {
            if (adListener != null) {
                adListener.onAdClosed();
            }
        }
    }
}
