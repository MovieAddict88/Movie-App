package com.cinecraze.Ads;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AdIdManager {

    private static final String JSON_FILE_NAME = "ads.json";

    private static String appOpenAdUnitId;
    private static String interstitialAdUnitId;
    private static String rewardedVideoAdUnitId;
    private static String bannerAdUnitId;

    public static void loadAdIds(Context context) {
        try {
            InputStream is = context.getAssets().open(JSON_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);
            appOpenAdUnitId = jsonObject.getString("app_open_ad_unit_id");
            interstitialAdUnitId = jsonObject.getString("interstitial_ad_unit_id");
            rewardedVideoAdUnitId = jsonObject.getString("rewarded_video_ad_unit_id");
            bannerAdUnitId = jsonObject.getString("banner_ad_unit_id");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getAppOpenAdUnitId() {
        return appOpenAdUnitId;
    }

    public static String getInterstitialAdUnitId() {
        return interstitialAdUnitId;
    }

    public static String getRewardedVideoAdUnitId() {
        return rewardedVideoAdUnitId;
    }

    public static String getBannerAdUnitId() {
        return bannerAdUnitId;
    }
}
