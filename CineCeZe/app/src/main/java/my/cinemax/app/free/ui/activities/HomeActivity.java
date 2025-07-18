package my.cinemax.app.free.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.applovin.sdk.AppLovinPrivacySettings;
import com.congle7997.google_iap.BillingSubs;
import com.congle7997.google_iap.CallBackBilling;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jackandphantom.blurimage.BlurImage;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.config.Global;
import my.cinemax.app.free.entity.ApiResponse;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.ui.fragments.DownloadsFragment;
import my.cinemax.app.free.ui.fragments.HomeFragment;
import my.cinemax.app.free.ui.fragments.MoviesFragment;
import my.cinemax.app.free.ui.fragments.SeriesFragment;
import my.cinemax.app.free.ui.fragments.TvFragment;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import my.cinemax.app.free.entity.Actress;
import my.cinemax.app.free.*;
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private NavigationView navigationView;
    private TextView text_view_name_nave_header;
    private CircleImageView circle_image_view_profile_nav_header;
    private ImageView image_view_profile_nav_header_bg;
    private Dialog rateDialog;
    private boolean FromLogin;
    private RelativeLayout relative_layout_home_activity_search_section;
    private EditText edit_text_home_activity_search;
    private ImageView image_view_activity_home_close_search;
    private ImageView image_view_activity_home_search;
    private ImageView image_view_activity_actors_back;
    private Dialog dialog;
    ConsentForm form;


    public static final String PREF_FILE= "MyPref";
    public static final String SUBSCRIBE_KEY= "subscribe";
    public static final String ITEM_SKU_SUBSCRIBE= "sub_example";




    private String payment_methode_id = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getGenreList();
        initViews();
        initActions();
        firebaseSubscribe();
        initGDPR();
        initBuy();
    }

    BillingSubs billingSubs;
    public void initBuy(){
        List<String> listSkuStoreSubs = new ArrayList<>();
        listSkuStoreSubs.add(Global.SUBSCRIPTION_ID);
        billingSubs = new BillingSubs(this, listSkuStoreSubs, new CallBackBilling() {
            @Override
            public void onPurchase() {
                PrefManager prefManager= new PrefManager(getApplicationContext());
                prefManager.setString("SUBSCRIBED","TRUE");
                Toasty.success(HomeActivity.this, "you have successfully subscribed ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotPurchase() {
                Toasty.warning(HomeActivity.this, "Operation has been cancelled  ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotLogin() {
            }
        });
    }

    public void subscribe(){
        billingSubs.purchase(Global.SUBSCRIPTION_ID);
    }



    private void initActions() {
        image_view_activity_actors_back.setOnClickListener(v->{
            relative_layout_home_activity_search_section.setVisibility(View.GONE);
            edit_text_home_activity_search.setText("");
        });
        edit_text_home_activity_search.setOnEditorActionListener((v,actionId,event) -> {
            if (edit_text_home_activity_search.getText().length()>0){
                Intent intent =  new Intent(HomeActivity.this,SearchActivity.class);
                intent.putExtra("query",edit_text_home_activity_search.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

                relative_layout_home_activity_search_section.setVisibility(View.GONE);
                edit_text_home_activity_search.setText("");
            }
            return false;
        });
        image_view_activity_home_close_search.setOnClickListener(v->{
            edit_text_home_activity_search.setText("");
        });
        image_view_activity_home_search.setOnClickListener(v->{
            if (edit_text_home_activity_search.getText().length()>0) {

                Intent intent =  new Intent(HomeActivity.this,SearchActivity.class);
                intent.putExtra("query",edit_text_home_activity_search.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                relative_layout_home_activity_search_section.setVisibility(View.GONE);
                edit_text_home_activity_search.setText("");
            }
        });
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerview = navigationView.getHeaderView(0);
        this.text_view_name_nave_header=(TextView) headerview.findViewById(R.id.text_view_name_nave_header);
        this.circle_image_view_profile_nav_header=(CircleImageView) headerview.findViewById(R.id.circle_image_view_profile_nav_header);
        this.image_view_profile_nav_header_bg=(ImageView) headerview.findViewById(R.id.image_view_profile_nav_header_bg);
        // init pager view

        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(100);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new MoviesFragment());
        adapter.addFragment(new SeriesFragment());
        adapter.addFragment(new TvFragment());
        adapter.addFragment(new DownloadsFragment());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        final BubbleNavigationConstraintView bubbleNavigationLinearView = findViewById(R.id.top_navigation_constraint);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                bubbleNavigationLinearView.setCurrentActiveItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                viewPager.setCurrentItem(position, true);
            }
        });

        this.relative_layout_home_activity_search_section =  (RelativeLayout) findViewById(R.id.relative_layout_home_activity_search_section);
        this.edit_text_home_activity_search =  (EditText) findViewById(R.id.edit_text_home_activity_search);
        this.image_view_activity_home_close_search =  (ImageView) findViewById(R.id.image_view_activity_home_close_search);
        this.image_view_activity_actors_back =  (ImageView) findViewById(R.id.image_view_activity_actors_back);
        this.image_view_activity_home_search =  (ImageView) findViewById(R.id.image_view_activity_home_search);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);



        MenuItem item = menu.findItem(R.id.action_search);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            viewPager.setCurrentItem(0);
        }else if(id == R.id.login){
            Intent intent= new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

            FromLogin=true;

        }else if (id == R.id.nav_exit) {
            final PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
                super.onBackPressed();
            } else {
                rateDialog(true);
            }
        }
        else if (id == R.id.my_password) {
            PrefManager prf= new PrefManager(getApplicationContext());
            if (prf.getString("LOGGED").toString().equals("TRUE")){
                Intent intent  =  new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }else{
                Intent intent= new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                FromLogin=true;
            }
        }else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }else if (id==R.id.my_profile){
            PrefManager prf= new PrefManager(getApplicationContext());
            if (prf.getString("LOGGED").toString().equals("TRUE")){
                Intent intent  =  new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("id", Integer.parseInt(prf.getString("ID_USER")));
                intent.putExtra("image",prf.getString("IMAGE_USER").toString());
                intent.putExtra("name",prf.getString("NAME_USER").toString());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

            }else{
                Intent intent= new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                FromLogin=true;
            }
        }else if (id==R.id.logout){
            logout();
        }else if (id ==  R.id.my_list){
            Intent intent= new Intent(HomeActivity.this, MyListActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        }
        else if (id==R.id.nav_share){
			//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MyApi.API_URL)));
            //final String appPackageName=getApplication().getPackageName();
            String shareBody = MyApi.API_URL;
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.app_name));
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
        }else if (id == R.id.nav_rate) {
            rateDialog(false);
        }else if (id == R.id.nav_help){
            Intent intent= new Intent(HomeActivity.this, SupportActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

        } else if (id == R.id.nav_policy  ){
            Intent intent = new Intent(getApplicationContext(), PolicyActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }else if (id == R.id.buy_now){
            showDialog();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public      void logout(){
        PrefManager prf= new PrefManager(getApplicationContext());
        prf.remove("ID_USER");
        prf.remove("SALT_USER");
        prf.remove("TOKEN_USER");
        prf.remove("NAME_USER");
        prf.remove("TYPE_USER");
        prf.remove("USERN_USER");
        prf.remove("IMAGE_USER");
        prf.remove("LOGGED");
        prf.remove("NEW_SUBSCRIBE_ENABLED");
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);
            if (prf.getString("TYPE_USER").toString().equals("google")){
            }else {
            }
        }else{
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.my_profile).setVisible(false);
            nav_Menu.findItem(R.id.my_password).setVisible(false);
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.my_list).setVisible(false);
            nav_Menu.findItem(R.id.login).setVisible(true);
            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            Picasso.with(getApplicationContext()).load(R.drawable.placeholder_profile).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);
        }

        if (prf.getString("APP_LOGIN_REQUIRED").toString().equals("TRUE")) {
            Intent intent= new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            finish();
        }
        Menu nav_Menu = navigationView.getMenu();

        if (checkSUBSCRIBED()){
            nav_Menu.findItem(R.id.buy_now).setVisible(false);
        }else{
            nav_Menu.findItem(R.id.buy_now).setVisible(true);

        }
        image_view_profile_nav_header_bg.setVisibility(View.GONE);
        Toasty.info(getApplicationContext(),getString(R.string.message_logout),Toast.LENGTH_LONG).show();
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }
    private void firebaseSubscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic("CineMax")
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Retrofit retrofit = apiClient.getClient();
                        apiRest service = retrofit.create(apiRest.class);
                        String unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);

                        Call<ApiResponse> call = service.addDevice(unique_id);
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful())
                                    Log.v("HomeActivity","Added : "+response.body().getMessage());
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Log.v("HomeActivity","onFailure : "+ t.getMessage().toString());
                            }
                        });
                    }
                });

    }
    private static final String TAG ="MainActivity ----- : " ;

    private void initGDPR() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        ConsentInformation consentInformation =
                ConsentInformation.getInstance(HomeActivity.this);
//// test
/////
        String[] publisherIds = {getResources().getString(R.string.publisher_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new
                ConsentInfoUpdateListener() {
                    @Override
                    public void onConsentInfoUpdated(ConsentStatus consentStatus) {
// User's consent status successfully updated.
                        Log.d(TAG,"onConsentInfoUpdated");
                        switch (consentStatus){
                            case PERSONALIZED:
                                Log.d(TAG,"PERSONALIZED");
                                ConsentInformation.getInstance(HomeActivity.this)
                                        .setConsentStatus(ConsentStatus.PERSONALIZED);
                                break;
                            case NON_PERSONALIZED:
                                Log.d(TAG,"NON_PERSONALIZED");
                                ConsentInformation.getInstance(HomeActivity.this)
                                        .setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                                break;


                            case UNKNOWN:
                                Log.d(TAG,"UNKNOWN");
                                if
                                (ConsentInformation.getInstance(HomeActivity.this).isRequestLocationInEeaOrUnknown
                                        ()){
                                    URL privacyUrl = null;
                                    try {
// TODO: Replace with your app's privacy policy URL.
                                        privacyUrl = new URL(Actress.actress.replace("/api/","/privacy_policy.html"));

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
// Handle error.

                                    }
                                    form = new ConsentForm.Builder(HomeActivity.this,
                                            privacyUrl)
                                            .withListener(new ConsentFormListener() {
                                                @Override
                                                public void onConsentFormLoaded() {
                                                    Log.d(TAG,"onConsentFormLoaded");
                                                    showform();
                                                }
                                                @Override
                                                public void onConsentFormOpened() {
                                                    Log.d(TAG,"onConsentFormOpened");
                                                }
                                                @Override
                                                public void onConsentFormClosed( ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                                                    Log.d(TAG,"onConsentFormClosed");
                                                }
                                                @Override
                                                public void onConsentFormError(String errorDescription) {
                                                    Log.d(TAG,"onConsentFormError");
                                                    Log.d(TAG,errorDescription);
                                                }
                                            })
                                            .withPersonalizedAdsOption()
                                            .withNonPersonalizedAdsOption()
                                            .build();
                                    form.load();
                                } else {
                                    Log.d(TAG,"PERSONALIZED else");
                                    ConsentInformation.getInstance(HomeActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
                                    AppLovinPrivacySettings.setHasUserConsent(true, getApplicationContext());

                                }
                                break;
                            default:
                                break;
                        }
                    }
                    @Override
                    public void onFailedToUpdateConsentInfo(String errorDescription) {
// User's consent status failed to update.
                        Log.d(TAG,"onFailedToUpdateConsentInfo");
                        Log.d(TAG,errorDescription);
                    }
                });
    }
    private void showform(){
        if (form!=null){
            Log.d(TAG,"show ok");
            form.show();
        }
    }
    public void rateDialog(final boolean close){
        this.rateDialog = new Dialog(this,R.style.Theme_Dialog);

        rateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rateDialog.setCancelable(true);
        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = rateDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        final   PrefManager prf= new PrefManager(getApplicationContext());
        rateDialog.setCancelable(false);
        rateDialog.setContentView(R.layout.dialog_rating_app);
        final AppCompatRatingBar AppCompatRatingBar_dialog_rating_app=(AppCompatRatingBar) rateDialog.findViewById(R.id.AppCompatRatingBar_dialog_rating_app);
        final LinearLayout linear_layout_feedback=(LinearLayout) rateDialog.findViewById(R.id.linear_layout_feedback);
        final LinearLayout linear_layout_rate=(LinearLayout) rateDialog.findViewById(R.id.linear_layout_rate);
        final Button buttun_send_feedback=(Button) rateDialog.findViewById(R.id.buttun_send_feedback);
        final Button button_later=(Button) rateDialog.findViewById(R.id.button_later);
        final Button button_never=(Button) rateDialog.findViewById(R.id.button_never);
        final Button button_cancel=(Button) rateDialog.findViewById(R.id.button_cancel);
        button_never.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf.setString("NOT_RATE_APP", "TRUE");
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        button_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        final EditText edit_text_feed_back=(EditText) rateDialog.findViewById(R.id.edit_text_feed_back);
        buttun_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf.setString("NOT_RATE_APP", "TRUE");
                Retrofit retrofit = apiClient.getClient();
                apiRest service = retrofit.create(apiRest.class);
                Call<ApiResponse> call = service.addSupport("Application rating feedback",AppCompatRatingBar_dialog_rating_app.getRating()+" star(s) Rating".toString(),edit_text_feed_back.getText().toString());
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if(response.isSuccessful()){
                            Toasty.success(getApplicationContext(), getResources().getString(R.string.rating_done), Toast.LENGTH_SHORT).show();
                        }else{
                            Toasty.error(getApplicationContext(), getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                        }
                        rateDialog.dismiss();

                        if (close)
                            finish();

                    }
                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toasty.error(getApplicationContext(), getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                        rateDialog.dismiss();

                        if (close)
                            finish();
                    }
                });
            }
        });
        AppCompatRatingBar_dialog_rating_app.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser){
                    if (rating>3){
                        final String appPackageName = getApplication().getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        prf.setString("NOT_RATE_APP", "TRUE");
                        rateDialog.dismiss();
                    }else{
                        linear_layout_feedback.setVisibility(View.VISIBLE);
                        linear_layout_rate.setVisibility(View.GONE);
                    }
                }else{

                }
            }
        });
        rateDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    rateDialog.dismiss();
                    if (close)
                        finish();
                }
                return true;

            }
        });
        rateDialog.show();

    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_search :
                edit_text_home_activity_search.requestFocus();
                relative_layout_home_activity_search_section.setVisibility(View.VISIBLE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();


        PrefManager prf= new PrefManager(getApplicationContext());
        Menu nav_Menu = navigationView.getMenu();


        if(checkSUBSCRIBED()){
            nav_Menu.findItem(R.id.buy_now).setVisible(false);
        }else{
            nav_Menu.findItem(R.id.buy_now).setVisible(true);
        }
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            nav_Menu.findItem(R.id.my_profile).setVisible(true);
            if (prf.getString("TYPE_USER").toString().equals("email")){
                nav_Menu.findItem(R.id.my_password).setVisible(true);
            }
            nav_Menu.findItem(R.id.logout).setVisible(true);
            nav_Menu.findItem(R.id.my_list).setVisible(true);
            nav_Menu.findItem(R.id.login).setVisible(false);
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);

            final com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    BlurImage.with(getApplicationContext()).load(bitmap).intensity(25).Async(true).into(image_view_profile_nav_header_bg);
                }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) { }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) { }
            };
            Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).into(target);
            image_view_profile_nav_header_bg.setTag(target);
            image_view_profile_nav_header_bg.setVisibility(View.VISIBLE);

        }else{
            nav_Menu.findItem(R.id.my_profile).setVisible(false);
            nav_Menu.findItem(R.id.my_password).setVisible(false);
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.my_list).setVisible(false);
            nav_Menu.findItem(R.id.login).setVisible(true);
            image_view_profile_nav_header_bg.setVisibility(View.GONE);

            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            Picasso.with(getApplicationContext()).load(R.drawable.placeholder_profile).placeholder(R.drawable.placeholder_profile).error(R.drawable.placeholder_profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);
        }
        if (FromLogin){
            FromLogin = false;
        }

    }
    public void goToTV() {
        viewPager.setCurrentItem(3);
    }
    private void getGenreList() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);

        Call<List<Genre>> call = service.getGenreList();
        call.enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {

            }
            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
            }
        });
    }
    public void showDialog(){
        this.dialog = new Dialog(this,
                R.style.Theme_Dialog);




        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        final   PrefManager prf= new PrefManager(getApplicationContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_subscribe);

        CardView card_view_gpay=(CardView) dialog.findViewById(R.id.card_view_gpay);
        CardView card_view_paypal=(CardView) dialog.findViewById(R.id.card_view_paypal);
        CardView card_view_cash=(CardView) dialog.findViewById(R.id.card_view_cash);
        CardView card_view_credit_card=(CardView) dialog.findViewById(R.id.card_view_credit_card);
        LinearLayout payment_methode=(LinearLayout) dialog.findViewById(R.id.payment_methode);
        LinearLayout dialog_content=(LinearLayout) dialog.findViewById(R.id.dialog_content);
        RelativeLayout relative_layout_subscibe_back=(RelativeLayout) dialog.findViewById(R.id.relative_layout_subscibe_back);

        if (prf.getString("APP_STRIPE_ENABLED").toString().equals("FALSE")){
            card_view_credit_card.setVisibility(View.GONE);
        }
        if (prf.getString("APP_PAYPAL_ENABLED").toString().equals("FALSE")){
            card_view_paypal.setVisibility(View.GONE);
        }
        if (prf.getString("APP_CASH_ENABLED").toString().equals("FALSE")){
            card_view_cash.setVisibility(View.GONE);
        }
        if (prf.getString("APP_GPLAY_ENABLED").toString().equals("FALSE")){
            card_view_gpay.setVisibility(View.GONE);
        }
        TextView text_view_go_pro=(TextView) dialog.findViewById(R.id.text_view_go_pro);


        TextView text_view_policy_2=(TextView) dialog.findViewById(R.id.text_view_policy_2);
        TextView text_view_policy=(TextView) dialog.findViewById(R.id.text_view_policy);
        SpannableString content = new SpannableString(getResources().getString(R.string.subscription_policy));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        text_view_policy.setText(content);
        text_view_policy_2.setText(content);


        text_view_policy.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this,RefundActivity.class));
        });
        text_view_policy_2.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this,RefundActivity.class));
        });

        RelativeLayout relative_layout_select_method=(RelativeLayout) dialog.findViewById(R.id.relative_layout_select_method);

        relative_layout_select_method.setOnClickListener(v->{
            if(payment_methode_id.equals("null")) {
                Toasty.error(getApplicationContext(), getResources().getString(R.string.select_payment_method), Toast.LENGTH_LONG).show();
                return;
            }
            switch (payment_methode_id){
                case "gp" :
                    subscribe();
                    dialog.dismiss();
                    break;
                default:
                    PrefManager prf1= new PrefManager(getApplicationContext());
                    if (prf1.getString("LOGGED").toString().equals("TRUE")){
                        Intent intent  =  new Intent(getApplicationContext(), PlansActivity.class);
                        intent.putExtra("method",payment_methode_id);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                        dialog.dismiss();

                    }else{
                        Intent intent= new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                        FromLogin=true;
                    }
                    dialog.dismiss();
                    break;
            }
        });
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment_methode.setVisibility(View.VISIBLE);
                dialog_content.setVisibility(View.GONE);
                relative_layout_subscibe_back.setVisibility(View.VISIBLE);
            }
        });

        relative_layout_subscibe_back.setOnClickListener(v->{
            payment_methode.setVisibility(View.GONE);
            dialog_content.setVisibility(View.VISIBLE);
            relative_layout_subscibe_back.setVisibility(View.GONE);
        });
        card_view_gpay.setOnClickListener(v->{
            payment_methode_id="gp";
            card_view_gpay.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            card_view_paypal.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
            card_view_cash.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
            card_view_credit_card.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
        });
        card_view_paypal.setOnClickListener(v->{
            payment_methode_id="pp";
            card_view_gpay.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
            card_view_paypal.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            card_view_cash.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
            card_view_credit_card.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
        });
        card_view_credit_card.setOnClickListener(v->{
            payment_methode_id="cc";
            card_view_gpay.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
            card_view_paypal.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
            card_view_cash.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
            card_view_credit_card.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
        });
        card_view_cash.setOnClickListener(v->{
            payment_methode_id="cash";
            card_view_gpay.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
            card_view_paypal.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
            card_view_cash.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            card_view_credit_card.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                }
                return true;
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            final PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
                super.onBackPressed();
            } else {
                rateDialog(true);
                return;
            }
        }

    }
    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE") && !prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE")) {
            return false;
        }
        return true;
    }
}
