package my.cinemax.app.free.ui.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAdView;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.ui.activities.ChannelActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ChannelAdapter  extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private  Boolean deletable = false;
    private List<Channel> channelList;
    private Activity activity;

    private com.google.android.gms.ads.interstitial.InterstitialAd admobInterstitialAd;
    private Integer position_selected;
    private Integer code_selected;
    private View view_selected;


    public ChannelAdapter(List<Channel> channelList, Activity activity) {
        this.channelList = channelList;
        this.activity = activity;
    }
    public ChannelAdapter(List<Channel> channelList, Activity activity,Boolean _deletable) {
        this.channelList = channelList;
        this.activity = activity;
        this.deletable =  _deletable;
    }
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v1 = inflater.inflate(R.layout.item_channel,null);
                viewHolder = new ChannelHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_empty, parent, false);
                viewHolder = new EmptyHolder(v2);
                break;
            }
            case 3: {
                View v3 = inflater.inflate(R.layout.item_admob_native_ads, parent, false);
                viewHolder = new AdmobNativeHolder(v3);
                break;
            }
            case 4: {
                View v4 = inflater.inflate(R.layout.item_admob_native_ads, parent, false);
                viewHolder = new AdmobNativeHolder(v4);
                break;
            }
        }
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder,final int position) {
        switch (getItemViewType(position)) {
            case 1:
                final ChannelHolder holder = (ChannelHolder) viewHolder;

                if (deletable)
                    holder.relative_layout_item_channel_delete.setVisibility(View.VISIBLE);
                else
                    holder.relative_layout_item_channel_delete.setVisibility(View.GONE);

                if (channelList.get(position).getLabel() != null){
                    if (channelList.get(position).getLabel().length()>0) {
                        holder.text_view_item_channel_label.setText(channelList.get(position).getLabel());
                        holder.text_view_item_channel_label.setVisibility(View.VISIBLE);
                    }else{
                        holder.text_view_item_channel_label.setVisibility(View.GONE);
                    }
                }else{
                    holder.text_view_item_channel_label.setVisibility(View.GONE);
                }


                if (channelList.get(position).getSublabel() != null){
                    if (channelList.get(position).getSublabel().length()>0) {
                        holder.text_view_item_channel_sub_label.setText(channelList.get(position).getSublabel());
                        holder.text_view_item_channel_sub_label.setVisibility(View.VISIBLE);
                    }else{
                        holder.text_view_item_channel_sub_label.setVisibility(View.GONE);
                    }
                }else{
                    holder.text_view_item_channel_sub_label.setVisibility(View.GONE);
                }
                holder.image_view_item_channel_delete.setOnClickListener(v->{
                    final PrefManager prefManager = new PrefManager(activity);
                    Integer id_user=  Integer.parseInt(prefManager.getString("ID_USER"));
                    String   key_user=  prefManager.getString("TOKEN_USER");
                    Retrofit retrofit = apiClient.getClient();
                    apiRest service = retrofit.create(apiRest.class);
                    Call<Integer> call = service.AddMyList(channelList.get(position).getId(),id_user,key_user,"channel");
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                            if (response.isSuccessful()){
                                if (response.body() == 202){
                                    Toasty.warning(activity, "This channel has been removed from your list", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                        }
                    });
                    channelList.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();

                });
                Picasso.with(activity).load(channelList.get(position).getImage()).placeholder(R.drawable.place_holder_channel).into(holder.image_view_item_channel);
                holder.image_view_item_channel.setOnClickListener(v -> {
                    PrefManager prefManager= new PrefManager(activity);


                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, holder.image_view_item_channel, "imageMain");
                    Intent in = new Intent(activity, ChannelActivity.class);
                    in.putExtra("channel", channelList.get(holder.getAdapterPosition()));
                    Intent intent = in;

                    if(checkSUBSCRIBED()){
                        activity.startActivity(intent, activityOptionsCompat.toBundle());
                    }else{
                        if( !prefManager.getString("ADMIN_INTERSTITIAL_TYPE").equals("FALSE")){

                            requestAdmobInterstitial();
                            if(prefManager.getInt("ADMIN_INTERSTITIAL_CLICKS")==prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")){
                                if (admobInterstitialAd != null) {
                                    prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",0);
                                    admobInterstitialAd.show(activity);
                                    position_selected = holder.getAdapterPosition();
                                    code_selected = 1;
                                    view_selected = holder.image_view_item_channel;
                                }else{
                                    activity.startActivity(intent, activityOptionsCompat.toBundle());
                                    requestAdmobInterstitial();
                                }
                            }else{


                                activity.startActivity(intent, activityOptionsCompat.toBundle());
                                prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")+1);
                            }
                        }else{
                            activity.startActivity(intent, activityOptionsCompat.toBundle());
                        }
                    }


                });
                break;
            case 4:{
                final AdmobNativeHolder holder_admob = (AdmobNativeHolder) viewHolder;
                holder_admob.adLoader.loadAd(new AdRequest.Builder().build());
                break;
            }
        }
    }
    @Override
    public int getItemCount() {
        return channelList.size();
    }
    public class ChannelHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_item_channel_label;
        private final TextView text_view_item_channel_sub_label;
        private final ImageView image_view_item_channel_delete;
        private final RelativeLayout relative_layout_item_channel_delete;
        public ImageView image_view_item_channel ;
        public ChannelHolder(View itemView) {
            super(itemView);
            this.image_view_item_channel =  (ImageView) itemView.findViewById(R.id.image_view_item_channel);
            this.image_view_item_channel =  (ImageView) itemView.findViewById(R.id.image_view_item_channel);
            this.text_view_item_channel_label =  (TextView) itemView.findViewById(R.id.text_view_item_channel_label);
            this.text_view_item_channel_sub_label =  (TextView) itemView.findViewById(R.id.text_view_item_channel_sub_label);
            this.relative_layout_item_channel_delete =  (RelativeLayout) itemView.findViewById(R.id.relative_layout_item_channel_delete);
            this.image_view_item_channel_delete =  (ImageView) itemView.findViewById(R.id.image_view_item_channel_delete);

        }
    }
    public class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if ((channelList.get(position).getTypeView())==0){
            return 1;
        }
        return   channelList.get(position).getTypeView();
    }

    public class AdmobNativeHolder extends RecyclerView.ViewHolder {
        private final AdLoader adLoader;
        private com.google.android.gms.ads.nativead.NativeAd nativeAd;
        private FrameLayout frameLayout;

        public AdmobNativeHolder(@NonNull View itemView) {
            super(itemView);

            PrefManager prefManager= new PrefManager(activity);

            frameLayout = (FrameLayout) itemView.findViewById(R.id.fl_adplaceholder);

            AdLoader.Builder builder = new AdLoader.Builder(activity, prefManager.getString("ADMIN_NATIVE_ADMOB_ID"));



            builder.forNativeAd(
                    nativeAd -> {
                        if (nativeAd == null) {
                            nativeAd.destroy();
                            return;
                        }

                         AdmobNativeHolder.this.nativeAd = nativeAd;
                        FrameLayout frameLayout = activity.findViewById(R.id.fl_adplaceholder);
                        NativeAdView adView =
                                (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_unified, null);
                        populateNativeAdView(nativeAd, adView);
                        if(frameLayout != null){
                            frameLayout.removeAllViews();
                            frameLayout.addView(adView);
                        }

                    });

            VideoOptions videoOptions =
                    new VideoOptions.Builder().setStartMuted(true).build();

            NativeAdOptions adOptions =
                    new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            builder.withNativeAdOptions(adOptions);

            adLoader =
                    builder
                            .withAdListener(
                                    new AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(LoadAdError loadAdError) {

                                        }
                                    })
                            .build();

            adLoader.loadAd(new AdRequest.Builder().build());

        }
    }



    /**
     * Populates a {@link NativeAdView} object with data from a given {@link com.google.android.gms.ads.nativead.NativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView the view to be populated
     */
    private void populateNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((com.google.android.gms.ads.nativead.MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.

                    super.onVideoEnd();
                }
            });
        } else {

        }
    }

    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(activity);
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE") && !prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE")) {
            return false;
        }
        return true;
    }

    public void selectOperation(Integer position,Integer code,View vew){
        switch (code){
            case 1:
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, vew, "imageMain");
                Intent in = new Intent(activity, ChannelActivity.class);
                in.putExtra("channel", channelList.get(position));
                activity.startActivity(in);
                break;
        }
    }
    private void requestAdmobInterstitial() {

        if (admobInterstitialAd==null){
            PrefManager prefManager= new PrefManager(activity);
            AdRequest adRequest = new AdRequest.Builder().build();
            admobInterstitialAd.load(activity.getApplicationContext(), prefManager.getString("ADMIN_INTERSTITIAL_ADMOB_ID"), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    admobInterstitialAd = interstitialAd;
                    Log.d("TAG", "The ad onAdLoaded.");

                    admobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            selectOperation(position_selected,code_selected,view_selected);

                            Log.d("TAG", "The ad was dismissed.");
                        }


                        @Override
                        public void onAdShowedFullScreenContent() {
                            admobInterstitialAd = null;
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                }
            });

        }
    }
}
