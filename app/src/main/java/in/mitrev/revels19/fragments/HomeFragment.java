package in.mitrev.revels19.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import in.mitrev.revels19.R;
import in.mitrev.revels19.activities.FavouritesActivity;
import in.mitrev.revels19.activities.LoginActivity;
import in.mitrev.revels19.activities.MainActivity;
import in.mitrev.revels19.activities.ProfileActivity;
import in.mitrev.revels19.adapters.HomeAdapter;
import in.mitrev.revels19.adapters.HomeCategoriesAdapter;
import in.mitrev.revels19.adapters.HomeEventsAdapter;
import in.mitrev.revels19.models.categories.CategoryModel;
import in.mitrev.revels19.models.events.ScheduleModel;
import in.mitrev.revels19.models.favourites.FavouritesModel;
import in.mitrev.revels19.models.instagram.InstagramFeed;
import in.mitrev.revels19.network.InstaFeedAPIClient;
import in.mitrev.revels19.utilities.NetworkUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private InstagramFeed feed;
    SwipeRefreshLayout swipeRefreshLayout;
    private HomeAdapter instaAdapter;
    View v;
    private HomeCategoriesAdapter categoriesAdapter;
    private HomeEventsAdapter eventsAdapter;
    private RecyclerView homeRV;
    private RecyclerView resultsRV;
    private RecyclerView categoriesRV;
    private RecyclerView eventsRV;
    private TextView resultsMore;
    private TextView categoriesMore;
    private TextView eventsMore;
    private TextView resultsNone;
    private FrameLayout homeResultsItem;
    private ProgressBar progressBar;
    private BottomNavigationView navigation;
    private AppBarLayout appBarLayout;
    private TextView instaTextView;
    private boolean initialLoad = true;
    private boolean firstLoad = true;
    private int processes = 0;
    private SliderLayout imageSlider;
    String TAG = "HomeFragment";
    private Realm mDatabase;

    // Declare after Results fragment in made
    //private List<EventResultModel> resultsList = new ArrayList<>();

    private List<CategoryModel> categoriesList = new ArrayList<>();
    private List<ScheduleModel> eventsList = new ArrayList<>();
  // private FirebaseRemoteConfig firebaseRemoteConfig;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //use string from resource later on instead of hardcoding
        getActivity().setTitle("Revels19");
        setHasOptionsMenu(true);
        mDatabase = Realm.getDefaultInstance();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().findViewById(R.id.toolbar)
                        .setElevation((4 * getResources().getDisplayMetrics().density + 0.5f));
                getActivity().findViewById(R.id.app_bar)
                        .setElevation((4 * getResources().getDisplayMetrics().density + 0.5f));
                appBarLayout = getActivity().findViewById(R.id.app_bar);
                appBarLayout.setExpanded(true, true);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = initViews(inflater, container);
        v = view;
        progressBar = view.findViewById(R.id.insta_progress);
        instaTextView = view.findViewById(R.id.insta_text_view);

       // Checking User's Network Status
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (imageSlider == null)
            imageSlider = view.findViewById(R.id.home_image_slider);
//        getImageURLSfromFirebase();
//        sliderInit();

        //Display Categories
        RealmResults<CategoryModel> categoriesRealmList = mDatabase.where(CategoryModel.class).sort("categoryName").findAll();
        categoriesList = mDatabase.copyFromRealm(categoriesRealmList);
        if (categoriesList.size() > 10) {
            categoriesList.subList(10, categoriesList.size()).clear();
        }
        categoriesAdapter = new HomeCategoriesAdapter(categoriesList, getActivity());
        categoriesRV.setAdapter(categoriesAdapter);
        categoriesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesAdapter.notifyDataSetChanged();
        try{((MainActivity) getActivity()).changeFragment(ScheduleFragment.newInstance());}
        catch (NullPointerException e){
            categoriesMore.setVisibility(View.INVISIBLE);
        }
        categoriesMore.setOnClickListener(v -> {
            //MORE Clicked - Take user to Categories Fragment
            Log.i(TAG, "onClick: Categories More");
            try {
                ((MainActivity) getActivity()).changeFragment(CategoriesFragment.newInstance());
            } catch (NullPointerException e) {
                Log.i(TAG, "Required Fragment does not have data yet.");
            }
//                Fragment fragment = new CategoriesFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
        });
        if (categoriesList.size() == 0) {
            view.findViewById(R.id.home_categories_none_text_view).setVisibility(View.VISIBLE);
        }

        //Display Events of current day
        Calendar cal = Calendar.getInstance();
        Calendar day1 = new GregorianCalendar(2019, 2, 7);
        Calendar day2 = new GregorianCalendar(2019, 2, 8);
        Calendar day3 = new GregorianCalendar(2019, 2, 9);
        Calendar day4 = new GregorianCalendar(2019, 2, 10);
        Calendar curDay = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        int dayOfEvent;

        /*if(curDay.getTimeInMillis() < day1.getTimeInMillis()){
            dayOfEvent =0;
        }else */
        if (curDay.getTimeInMillis() < day2.getTimeInMillis()) {
            dayOfEvent = 1;
        } else if (curDay.getTimeInMillis() < day3.getTimeInMillis()) {
            dayOfEvent = 2;
        } else if (curDay.getTimeInMillis() < day4.getTimeInMillis()) {
            dayOfEvent = 3;
        } else {
            dayOfEvent = 4;
        }

        String sortCriteria[] = {"day", "startTime", "eventName"};
        Sort sortOrder[] = {Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};

        //PreRevels events
        if (dayOfEvent == 0) {
            try {
                List<ScheduleModel> eventsRealmResults = mDatabase
                        .copyFromRealm((mDatabase.where(ScheduleModel.class).findAll()));
                for (int i = 0; i < eventsRealmResults.size(); i++) {
                    Log.d(TAG, "dayFilter Value: " + eventsRealmResults.get(i).getIsRevels());
                    if (eventsRealmResults.get(i).getIsRevels().contains("0")) {
                        eventsList.add(eventsRealmResults.get(i));
                        if (isFavourite(eventsRealmResults.get(i))) {
                            eventsList.remove(eventsRealmResults.get(i));
                            eventsList.add(0, eventsRealmResults.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Main Revels Events
        else {
            eventsList.clear();
            eventsList = mDatabase.copyFromRealm(mDatabase.where(ScheduleModel.class).equalTo("isRevels", "1").equalTo("day", dayOfEvent + "").sort(sortCriteria, sortOrder).findAll());
            for (int i = 0; i < eventsList.size(); i++) {
                ScheduleModel event = eventsList.get(i);
                if (isFavourite(event)) {
                    //Move to top if the event is a Favourite
                    eventsList.remove(event);
                    eventsList.add(0, event);
                }
            }
        }
        Log.i(TAG, "onCreateView: eventsList size" + eventsList.size());
        if (eventsList.size() > 10) {
            eventsList.subList(10, eventsList.size()).clear();
        }
        eventsAdapter = new HomeEventsAdapter(eventsList, null, getActivity());
        eventsRV.setAdapter(eventsAdapter);
        eventsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        eventsAdapter.notifyDataSetChanged();
        try{((MainActivity) getActivity()).changeFragment(ScheduleFragment.newInstance());}
        catch (NullPointerException e){
            eventsMore.setVisibility(View.INVISIBLE);
        }
        eventsMore.setOnClickListener(v -> {
            //MORE Clicked - Take user to Events Fragment
            Log.i(TAG, "onClick: Events More");
            try {
                ((MainActivity) getActivity()).changeFragment(ScheduleFragment.newInstance());
            } catch (NullPointerException e) {
//                    Fragment fragment = new ScheduleFragment();
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.fragment_container, fragment);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
                Log.i(TAG, "Required Fragment does not have data yet.");
                //  setFragment(new ScheduleFragment());
            }
        });
        if (eventsList.size() == 0) {
            view.findViewById(R.id.home_events_none_text_view).setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setOnRefreshListener(() -> {
            boolean isConnectedTemp = NetworkUtils.isInternetConnected(getContext());
            if (isConnectedTemp) {
                displayInstaFeed();
                fetchResults();
                new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 5000);
            } else {
                Snackbar.make(view, "Check connection!", Snackbar.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }

        });
        return view;
    }

    private void sliderInit(){   //Updating the SliderLayout with images
        //Animation type
        imageSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        //Setting the Transition time and Interpolator for the Animation
        imageSlider.setSliderTransformDuration(200, new AccelerateDecelerateInterpolator());
        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        imageSlider.setCustomAnimation(new DescriptionAnimation());
        //Setting the time after which it moves to the next image
        imageSlider.setDuration(400);
        imageSlider.setVisibility(View.GONE);
    }

    public void displayInstaFeed(){
        if (initialLoad) progressBar.setVisibility(View.VISIBLE);
        homeRV.setVisibility(View.GONE);
        instaTextView.setVisibility(View.GONE);
        Call<InstagramFeed> call = InstaFeedAPIClient.getInterface().getInstagramFeed();
        processes ++;
        call.enqueue(new Callback<InstagramFeed>() {
            @Override
            public void onResponse(@NonNull Call<InstagramFeed> call,
                                   @NonNull Response<InstagramFeed> response) {
                if (initialLoad) progressBar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    feed = response.body();
                    instaAdapter =  new HomeAdapter(feed);
                    homeRV.setAdapter(instaAdapter);
                    homeRV.setLayoutManager(new LinearLayoutManager(getContext()));
                    ViewCompat.setNestedScrollingEnabled(homeRV, false);
                }
                homeRV.setVisibility(View.VISIBLE);
                initialLoad = false;

            }

            @Override
            public void onFailure(@NonNull Call<InstagramFeed> call, @NonNull Throwable t) {
                if (initialLoad) progressBar.setVisibility(View.GONE);
                instaTextView.setVisibility(View.VISIBLE);
                Log.i(TAG, "onFailure: Error Fetching insta feed ");
                initialLoad = false;
            }
        });
    }

    public void updateResultsList(){
        //After results fragment in made
    }

    public void fetchResults(){
        //After Results fragment in made
    }

    public boolean isFavourite(ScheduleModel event){
        RealmResults<FavouritesModel> favouritesRealmList = mDatabase.where(FavouritesModel.class).equalTo("id",event.getEventId()).contains("day", event.getDay()).equalTo("round", event.getRound()).findAll();
        return (favouritesRealmList.size()!=0);
    }

    public View initViews(LayoutInflater inflater, ViewGroup container){

        appBarLayout = container.findViewById(R.id.app_bar);
        navigation = container.findViewById(R.id.main_bottom_nav);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homeRV = view.findViewById(R.id.home_recycler_view);
        resultsRV = view.findViewById(R.id.home_results_recycler_view);
        categoriesRV = view.findViewById(R.id.home_categories_recycler_view);
        eventsRV = view.findViewById(R.id.home_events_recycler_view);
        resultsMore = view.findViewById(R.id.home_results_more_text_view);
        categoriesMore = view.findViewById(R.id.home_categories_more_text_view);
        eventsMore = view.findViewById(R.id.home_events_more_text_view);
        resultsNone = view.findViewById(R.id.home_results_none_text_view);
        homeResultsItem = view.findViewById(R.id.home_results_frame);
        instaTextView = view.findViewById(R.id.instagram_textview);
        swipeRefreshLayout = view.findViewById(R.id.home_swipe_refresh_layout);
//
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_profile:{
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (sp.getBoolean("loggedIn", false)) startActivity(new Intent(getActivity(), ProfileActivity.class));
                else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                return true;
            }
            case R.id.action_favourites: {
                startActivity(new Intent(getActivity(), FavouritesActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

    }
}

