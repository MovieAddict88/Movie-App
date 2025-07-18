package my.cinemax.app.free.ui.fragments;


import android.os.Bundle;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.ui.Adapters.PosterAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment {


    private View view;
    private RelativeLayout relative_layout_movies_fragement_filtres_button;
    private CardView card_view_movies_fragement_filtres_layout;
    private ImageView image_view_movies_fragement_close_filtres;
    private AppCompatSpinner spinner_fragement_movies_orders_list;
    private List<Genre> genreList =  new ArrayList<>();
    private AppCompatSpinner spinner_fragement_movies_genre_list;
    private RelativeLayout relative_layout_frament_movies_genres;
    private RecyclerView recycler_view_movies_fragment;
    private LinearLayout linear_layout_page_error_movies_fragment;
    private LinearLayout linear_layout_load_movies_fragment;
    private SwipeRefreshLayout swipe_refresh_layout_movies_fragment;
    private RelativeLayout relative_layout_load_more_movies_fragment;
    private ImageView image_view_empty_list;


    private GridLayoutManager gridLayoutManager;
    private PosterAdapter adapter;
    private List<Poster> movieList =  new ArrayList<>();

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private Integer page = 0;
    private Integer position = 0;
    private Integer item = 0 ;
    private Button button_try_again;
    private int genreSelected = 0;
    private String orderSelected = "created";

    private boolean firstLoadGenre = true;
    private boolean firstLoadOrder = true;
    private boolean loaded = false;

    private Integer lines_beetween_ads = 2 ;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;
    private int type_ads = 0;
    private PrefManager prefManager;


    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            if (!loaded) {
                loaded=true;
                page = 0;
                loading = true;
                getGenreList();
                loadMovies();
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_movies, container, false);
        movieList.add(new Poster().setTypeView(2));
        prefManager= new PrefManager(getApplicationContext());

        initView();
        initActon();

        return view;
    }

    private void initActon() {
        swipe_refresh_layout_movies_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                item = 0;
                page = 0;
                loading = true;
                movieList.clear();
                movieList.add(new Poster().setTypeView(2));
                adapter.notifyDataSetChanged();
                loadMovies();

            }
        });
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = 0;
                page = 0;
                loading = true;
                movieList.clear();
                movieList.add(new Poster().setTypeView(2));
                adapter.notifyDataSetChanged();
                loadMovies();

            }
        });
        recycler_view_movies_fragment.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {

                    visibleItemCount    = gridLayoutManager.getChildCount();
                    totalItemCount      = gridLayoutManager.getItemCount();
                    pastVisiblesItems   = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            loadMovies();
                        }
                    }
                }else{

                }
            }
        });
    }
    private void initView() {
        AdManager adManager = new AdManager(getActivity());
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!adManager.getAdmobNativeId().equals("false")){
            native_ads_enabled=true;
            if (tabletSize) {
                lines_beetween_ads=6*Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            }else{
                lines_beetween_ads=3*Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            }
        }
        // prod

        this.button_try_again = (Button) view.findViewById(R.id.button_try_again);
        this.image_view_empty_list = (ImageView) view.findViewById(R.id.image_view_empty_list);
        this.relative_layout_load_more_movies_fragment = (RelativeLayout) view.findViewById(R.id.relative_layout_load_more_movies_fragment);
        this.swipe_refresh_layout_movies_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_movies_fragment);
        this.linear_layout_load_movies_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_movies_fragment);
        this.linear_layout_page_error_movies_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_page_error_movies_fragment);
        this.recycler_view_movies_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_movies_fragment);
        this.relative_layout_movies_fragement_filtres_button = (RelativeLayout) view.findViewById(R.id.relative_layout_movies_fragement_filtres_button);
        this.card_view_movies_fragement_filtres_layout = (CardView) view.findViewById(R.id.card_view_movies_fragement_filtres_layout);
        this.image_view_movies_fragement_close_filtres = (ImageView) view.findViewById(R.id.image_view_movies_fragement_close_filtres);
        this.spinner_fragement_movies_orders_list = (AppCompatSpinner) view.findViewById(R.id.spinner_fragement_movies_orders_list);
        this.spinner_fragement_movies_genre_list = (AppCompatSpinner) view.findViewById(R.id.spinner_fragement_movies_genre_list);
        this.relative_layout_frament_movies_genres = (RelativeLayout) view.findViewById(R.id.relative_layout_frament_movies_genres);


        adapter = new PosterAdapter(movieList,getActivity());
        if (native_ads_enabled){
            Log.v("MYADS","ENABLED");
            if (tabletSize) {
                this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),6,RecyclerView.VERTICAL,false);
                Log.v("MYADS","tabletSize");
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ((position ) % (lines_beetween_ads + 1 ) == 0 || position == 0) ? 6 : 1;
                    }
                });
            } else {
                this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),3,RecyclerView.VERTICAL,false);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ((position ) % (lines_beetween_ads + 1 ) == 0 || position == 0) ? 3 : 1;
                    }
                });
            }
        }else {
            if (tabletSize) {
                this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),6,RecyclerView.VERTICAL,false);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ( position == 0) ? 6 : 1;
                    }
                });
            } else {
                this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),3,RecyclerView.VERTICAL,false);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ( position == 0) ? 3 : 1;
                    }
                });
            }
        }

        recycler_view_movies_fragment.setHasFixedSize(true);
        recycler_view_movies_fragment.setAdapter(adapter);
        recycler_view_movies_fragment.setLayoutManager(gridLayoutManager);
        // test


        final String[] countryCodes = getResources().getStringArray(R.array.orders_list);

        ArrayAdapter<String> ordersAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_layout,R.id.textView,countryCodes);
        ordersAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner_fragement_movies_orders_list.setAdapter(ordersAdapter);
    }
    private void loadMovies() {
        if (page == 0) {
            linear_layout_load_movies_fragment.setVisibility(View.VISIBLE);
        } else {
            relative_layout_load_more_movies_fragment.setVisibility(View.VISIBLE);
        }
        swipe_refresh_layout_movies_fragment.setRefreshing(false);

        TmdbService tmdbService = TmdbClient.getService();
        Call<TmdbMovieResponse> call = tmdbService.getPopularMovies(Global.TMDB_API_KEY);
        call.enqueue(new Callback<TmdbMovieResponse>() {
            @Override
            public void onResponse(Call<TmdbMovieResponse> call, Response<TmdbMovieResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMovies().size() > 0) {
                        movieList.addAll(response.body().getMovies());
                        adapter.notifyDataSetChanged();
                        page++;
                        loading = true;
                    } else {
                        if (page == 0) {
                            image_view_empty_list.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    linear_layout_page_error_movies_fragment.setVisibility(View.VISIBLE);
                }
                relative_layout_load_more_movies_fragment.setVisibility(View.GONE);
                swipe_refresh_layout_movies_fragment.setRefreshing(false);
                linear_layout_load_movies_fragment.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<TmdbMovieResponse> call, Throwable t) {
                linear_layout_page_error_movies_fragment.setVisibility(View.VISIBLE);
                relative_layout_load_more_movies_fragment.setVisibility(View.GONE);
                swipe_refresh_layout_movies_fragment.setRefreshing(false);
                linear_layout_load_movies_fragment.setVisibility(View.GONE);
            }
        });
    }
}
