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
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAdView;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.ui.activities.MovieActivity;
import my.cinemax.app.free.ui.activities.SerieActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class PosterAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Poster> posterList;
    private List<Channel> channelList;
    private Activity activity;
    private Boolean deletable = false;
    private LinearLayoutManager linearLayoutManagerChannelAdapter;
    private ChannelAdapter channelAdapter;
    private Integer position_selected;
    private Integer code_selected;
    private View view_selected;
    private InterstitialAd admobInterstitialAd;

    public PosterAdapter(List<Poster> posterList,List<Channel> channelList, Activity activity) {
        this.posterList = posterList;
        this.channelList = channelList;
        this.activity = activity;
    }
    public PosterAdapter(List<Poster> posterList, Activity activity) {
        this.posterList = posterList;
        this.activity = activity;
    }
    public PosterAdapter(List<Poster> posterList, Activity activity,boolean deletable) {
        this.posterList = posterList;
        this.activity = activity;
        this.deletable = deletable;
    }
    public PosterAdapter(List<Poster> posterList,List<Channel> channelList_, Activity activity,boolean deletable) {
        this.channelList = channelList_;
        this.posterList = posterList;
        this.activity = activity;
        this.deletable = deletable;
    }
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v1 = inflater.inflate(R.layout.item_poster,null);
                viewHolder = new PosterHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_empty, parent, false);
                viewHolder = new EmptyHolder(v2);
                break;
            }
            case 3: {
                View v3 = inflater.inflate(R.layout.item_channels_search, parent, false);
                viewHolder = new ChannelsHolder(v3);
                break;
            }
            case 4: {
                View v3 = inflater.inflate(R.layout.item_admob_native_ads, parent, false);
                viewHolder = new AdmobNativeHolder(v3);
                break;
            }
            case 5: {
                View v4 = inflater.inflate(R.layout.item_admob_native_ads, parent, false);
                viewHolder = new AdmobNativeHolder(v4);
                break;
            }
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        switch (getItemViewType(position)) {
            case 1:

                final PosterHolder holder = (PosterHolder) viewHolder;
                Picasso.with(activity).load(posterList.get(position).getImage()).placeholder(R.drawable.poster_placeholder).into(holder.image_view_item_poster_image);
                if (deletable)
                    holder.relative_layout_item_poster_delete.setVisibility(View.VISIBLE);
                else
                    holder.relative_layout_item_poster_delete.setVisibility(View.GONE);


                if (posterList.get(position).getLabel() != null){
                    if (posterList.get(position).getLabel().length()>0) {
                        holder.text_view_item_poster_label.setText(posterList.get(position).getLabel());
                        holder.text_view_item_poster_label.setVisibility(View.VISIBLE);
                    }else{
                        holder.text_view_item_poster_label.setVisibility(View.GONE);
                    }
                }else{
                    holder.text_view_item_poster_label.setVisibility(View.GONE);
                }


                if (posterList.get(position).getSublabel() != null){
                    if (posterList.get(position).getSublabel().length()>0) {
                        holder.text_view_item_poster_sub_label.setText(posterList.get(position).getSublabel());
                        holder.text_view_item_poster_sub_label.setVisibility(View.VISIBLE);
                    }else{
                        holder.text_view_item_poster_sub_label.setVisibility(View.GONE);
                    }
                }else{
                    holder.text_view_item_poster_sub_label.setVisibility(View.GONE);
                }

                holder.image_view_item_poster_image.setOnClickListener(v -> {


                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, holder.image_view_item_poster_image, "imageMain");
                    Intent intent = new Intent(activity, MovieActivity.class);
                    if (posterList.get(position).getType().equals("movie")) {
                        intent = new Intent(activity, MovieActivity.class);
                    } else if (posterList.get(position).getType().equals("serie")) {
                        intent = new Intent(activity, SerieActivity.class);
                    }
                    intent.putExtra("poster", posterList.get(holder.getAdapterPosition()));
                    final Intent intent1 = intent;

                    PrefManager prefManager= new PrefManager(activity);

                    if(checkSUBSCRIBED()){
                        activity.startActivity(intent1, activityOptionsCompat.toBundle());
                    }else{
                        if( !prefManager.getString("ADMIN_INTERSTITIAL_TYPE").equals("FALSE")){

                            requestAdmobInterstitial();
                            if(prefManager.getInt("ADMIN_INTERSTITIAL_CLICKS")==prefManager.getInt("ADMOB_INTERSTITIAL_COUNT_CLICKS")){
                                if (admobInterstitialAd != null) {
                                    prefManager.setInt("ADMOB_INTERSTITIAL_COUNT_CLICKS",0);
                                    admobInterstitialAd.show(activity);
                                    position_selected = holder.getAdapterPosition();
                                    code_selected = 1;
                                    view_selected = holder.image_view_item_poster_image;
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
                holder.image_view_item_poster_delete.setOnClickListener(v->{
                    final PrefManager prefManager = new PrefManager(activity);
                    Integer id_user=  Integer.parseInt(prefManager.getString("ID_USER"));
                    String   key_user=  prefManager.getString("TOKEN_USER");
                    Retrofit retrofit = apiClient.getClient();
                    apiRest service = retrofit.create(apiRest.class);
                    Call<Integer> call = service.AddMyList(posterList.get(position).getId(),id_user,key_user,"poster");
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                            if (response.isSuccessful()){
                                if (response.body() == 202){
                                    Toasty.warning(activity, "This movie has been removed from your list", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                        }
                    });
                    posterList.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();

                });
                break;
            case 2:

                break;
            case 3:
                final ChannelsHolder holder_channel = (ChannelsHolder) viewHolder;
                this.linearLayoutManagerChannelAdapter=  new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                this.channelAdapter =new ChannelAdapter(channelList,activity,deletable);
                holder_channel.recycle_view_channels_item.setHasFixedSize(true);
                holder_channel.recycle_view_channels_item.setAdapter(channelAdapter);
                holder_channel.recycle_view_channels_item.setLayoutManager(linearLayoutManagerChannelAdapter);
                channelAdapter.notifyDataSetChanged();
                break;
            case 5:{
                final AdmobNativeHolder holder_admob = (AdmobNativeHolder) viewHolder;

                holder_admob.adLoader.loadAd(new AdRequest.Builder().build());

                break;
            }
        }

    }
    @Override
    public int getItemCount() {
        return posterList.size();
    }
    public class PosterHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_item_poster_label;
        private final TextView text_view_item_poster_sub_label;
        private ImageView image_view_item_poster_delete;
        public ImageView image_view_item_poster_image ;
        public RelativeLayout relative_layout_item_poster_delete ;
        public PosterHolder(View itemView) {
            super(itemView);
            this.image_view_item_poster_image =  (ImageView) itemView.findViewById(R.id.image_view_item_poster_image);
            this.relative_layout_item_poster_delete =  (RelativeLayout) itemView.findViewById(R.id.relative_layout_item_poster_delete);
            this.image_view_item_poster_delete =  (ImageView) itemView.findViewById(R.id.image_view_item_poster_delete);
            this.text_view_item_poster_sub_label =  (TextView) itemView.findViewById(R.id.text_view_item_poster_sub_label);
            this.text_view_item_poster_label =  (TextView) itemView.findViewById(R.id.text_view_item_poster_label);
        }
    }
    public class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if ((posterList.get(position).getTypeView())==0){
            return 1;
        }
        return   posterList.get(position).getTypeView();
    }

    private class ChannelsHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_channels_item;

        public ChannelsHolder(View v3) {
            super(v3);
            this.recycle_view_channels_item=(RecyclerView) itemView.findViewById(R.id.recycle_view_channels_item);
        }
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
                    new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
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


    public void selectOperation(Integer position,Integer code,View vew){
        switch (code){
            case 1:
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,vew, "imageMain");
                Intent intent = new Intent(activity, MovieActivity.class);
                if (posterList.get(position).getType().equals("movie")) {
                    intent = new Intent(activity, MovieActivity.class);
                } else if (posterList.get(position).getType().equals("serie")) {
                    intent = new Intent(activity, SerieActivity.class);
                }
                intent.putExtra("poster", posterList.get(position));
                activity.startActivity(intent);
                break;
        }
    }

    private void requestAdmobInterstitial() {
        if (admobInterstitialAd==null){
            PrefManager prefManager= new PrefManager(activity);
            AdRequest adRequest = new AdRequest.Builder().build();
            admobInterstitialAd.load(activity.getApplicationContext(), prefManager.getString("ADMIN_INTERSTITIAL_ADMOB_ID"), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    admobInterstitialAd = interstitialAd;


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
                    admobInterstitialAd = null;

                }
            });

        }


    }
    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(activity);
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE") && !prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE")) {
            return false;
        }
        return true;
    }
}
