package my.cinemax.app.free.utils;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

public class AdManager {
    private static final String ADMOB_JSON_FILE = "admob.json";

    private String admobAppId;
    private String admobBannerAdUnitId;
    private String admobInterstitialAdUnitId;
    private String admobRewardedAdUnitId;
    private String admobNativeAdUnitId;


    public AdManager(Context context) {
        try {
            InputStream is = context.getAssets().open(ADMOB_JSON_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
            admobAppId = jsonObject.getString("admob_app_id");
            admobBannerAdUnitId = jsonObject.getString("admob_banner_ad_unit_id");
            admobInterstitialAdUnitId = jsonObject.getString("admob_interstitial_ad_unit_id");
            admobRewardedAdUnitId = jsonObject.getString("admob_rewarded_ad_unit_id");
            admobNativeAdUnitId = jsonObject.getString("admob_native_ad_unit_id");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAdmobAppId() {
        return admobAppId;
    }

    public String getAdmobBannerAdUnitId() {
        return admobBannerAdUnitId;
    }

    public String getAdmobInterstitialAdUnitId() {
        return admobInterstitialAdUnitId;
    }

    public String getAdmobRewardedAdUnitId() {
        return admobRewardedAdUnitId;
    }
    public String getAdmobNativeId() {
        return admobNativeAdUnitId;
    }
}
