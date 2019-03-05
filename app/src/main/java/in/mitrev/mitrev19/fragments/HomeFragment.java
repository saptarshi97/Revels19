package in.mitrev.mitrev19.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.synnapps.carouselview.CarouselView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import in.mitrev.mitrev19.activities.FavouritesActivity;
import in.mitrev.mitrev19.activities.LoginActivity;
import in.mitrev.mitrev19.activities.MainActivity;
import in.mitrev.mitrev19.activities.ProfileActivity;
import in.mitrev.mitrev19.network.RevelsLiveAPIClient;
import in.mitrev.mitrev19.R;
import in.mitrev.mitrev19.adapters.HomeAdapter;
import in.mitrev.mitrev19.adapters.HomeCategoriesAdapter;
import in.mitrev.mitrev19.adapters.HomeEventsAdapter;
import in.mitrev.mitrev19.adapters.HomeResultsAdapter;
import in.mitrev.mitrev19.models.categories.CategoryModel;
import in.mitrev.mitrev19.models.events.ScheduleModel;
import in.mitrev.mitrev19.models.favourites.FavouritesModel;
import in.mitrev.mitrev19.models.results.EventResultModel;
import in.mitrev.mitrev19.models.results.ResultModel;
import in.mitrev.mitrev19.models.results.ResultsListModel;
import in.mitrev.mitrev19.models.revels_live.RevelsLiveListModel;
import in.mitrev.mitrev19.network.APIClient;
import in.mitrev.mitrev19.utilities.NetworkUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RevelsLiveListModel feed;
    SwipeRefreshLayout swipeRefreshLayout;
    private HomeAdapter instaAdapter;
    View v;
    private HomeCategoriesAdapter categoriesAdapter;
    private HomeEventsAdapter eventsAdapter;
    private RecyclerView homeRV;
    private RecyclerView resultsRV;
    private RecyclerView categoriesRV;
    private RecyclerView eventsRV;
    private View newsletterButton;
    private TextView resultsMore;
    private TextView categoriesMore;
    private TextView eventsMore;
    private TextView resultsNone;
    private FrameLayout homeResultsItem;
    private ProgressBar progressBar;
    private BottomNavigationView navigation;
    private AppBarLayout appBarLayout;
    private TextView revelsLiveTextView;
    private boolean initialLoad = true;
    private int processes = 0;
    private HomeResultsAdapter resultsAdapter;
    private Toolbar toolBar;
    String TAG = "HomeFragment";
    int[] images = {
            R.drawable.slider1,
            R.drawable.slider2,
            R.drawable.slider3,
            R.drawable.slider4,
            R.drawable.slider5,
            R.drawable.slider6,
            R.drawable.slider7
    };

    private CarouselView carouselView;
    private Realm mDatabase;


    private List<EventResultModel> resultsList = new ArrayList<>();
    private List<CategoryModel> categoriesList = new ArrayList<>();
    private List<ScheduleModel> eventsList = new ArrayList<>();

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
        getActivity().setTitle(R.string.app_name);
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
        Log.d(TAG, "onCreateView: HEllo!");
        // Inflate the layout for this fragment
        final View view = initViews(inflater, container);
        v = view;
        progressBar = view.findViewById(R.id.revels_live_progress);
        revelsLiveTextView = view.findViewById(R.id.revels_live_error_text_view);
        toolBar = view.findViewById(R.id.toolbar);

        newsletterButton = view.findViewById(R.id.home_newsletter);

        carouselView = view.findViewById(R.id.carouselView);
        carouselView.setPageCount(images.length);

        carouselView.setImageListener((position, imageView) -> imageView.setImageResource(images[position]));

        newsletterButton.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int today = c.get(Calendar.DATE);
            final int startDate = 6;
            int dayOfFest = today - startDate;
            String URL;
            int current_hour = c.get(Calendar.HOUR_OF_DAY);

            if (dayOfFest == 0 || (dayOfFest == 1 && current_hour < 8))
                URL = "https://themitpost.com/revels19-newsletter-day-0/";

            else if ((dayOfFest == 1 && current_hour >= 8) || (dayOfFest == 2 && current_hour < 8))
                URL = "https://themitpost.com/revels19-newsletter-day-1/";

            else if ((dayOfFest == 2 && current_hour >= 8) || (dayOfFest == 3 && current_hour < 8))
                URL = "https://themitpost.com/revels19-newsletter-day-2/";

            else if ((dayOfFest == 3 && current_hour >= 8))
                URL = "https://themitpost.com/revels19-newsletter-day-3/";

            else
                URL = "https://themitpost.com/";

            launchCCT(URL, getContext());
        });

        // Checking User's Network Status
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        resultsAdapter = new HomeResultsAdapter(resultsList, getActivity());
        resultsRV.setAdapter(resultsAdapter);
        resultsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        updateResultsList();
        resultsMore.setOnClickListener(v -> {
            //MORE Clicked - Take user to Results Fragment
            ((MainActivity) getActivity()).setFragment(MainActivity.TAG_RESULTS);
        });

        //Display Categories
        RealmResults<CategoryModel> categoriesRealmList = mDatabase.where(CategoryModel.class)
                .sort("categoryName")
                .notEqualTo("categoryType", "SUPPORTING")
                .findAll();
        categoriesList = mDatabase.copyFromRealm(categoriesRealmList);
        if (categoriesList.size() > 10) {
            categoriesList.subList(10, categoriesList.size()).clear();
        }
        categoriesAdapter = new HomeCategoriesAdapter(categoriesList, getActivity());
        categoriesRV.setAdapter(categoriesAdapter);
        categoriesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesAdapter.notifyDataSetChanged();
        categoriesMore.setOnClickListener(v -> {
            //MORE Clicked - Take user to Categories Fragment
            ((MainActivity) getActivity()).setFragment(MainActivity.TAG_CATEGORIES);
        });
        if (categoriesList.size() == 0) {
            view.findViewById(R.id.home_categories_none_text_view).setVisibility(View.VISIBLE);
        }

        //Display Events of current day
        Calendar cal = Calendar.getInstance();
        Calendar day1 = new GregorianCalendar(2019, 2, 6);
        Calendar day2 = new GregorianCalendar(2019, 2, 7);
        Calendar day3 = new GregorianCalendar(2019, 2, 8);
        Calendar day4 = new GregorianCalendar(2019, 2, 9);
        Calendar curDay = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        int dayOfEvent;

        if (curDay.getTimeInMillis() < day2.getTimeInMillis()) {
            dayOfEvent = 1;
        } else if (curDay.getTimeInMillis() < day3.getTimeInMillis()) {
            dayOfEvent = 2;
        } else if (curDay.getTimeInMillis() < day4.getTimeInMillis()) {
            dayOfEvent = 3;
        } else {
            dayOfEvent = 4;
        }

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
            List<ScheduleModel> tempEventsList = mDatabase
                    .copyFromRealm(mDatabase.where(ScheduleModel.class).findAll());

            Log.d(TAG, "onCreateView: Day of Event " + dayOfEvent);
            for (int i = 0; i < tempEventsList.size(); i++) {
                ScheduleModel scheduleModel = tempEventsList.get(i);
                if (scheduleModel.getDay().equals(dayOfEvent + "")) {
                    eventsList.add(scheduleModel);
                }
            }

            Collections.sort(eventsList, (a, b) -> a.getEventName().compareTo(b.getEventName()));

            for (int i = 0; i < eventsList.size(); i++) {
                ScheduleModel event = eventsList.get(i);
                if (isFavourite(event)) {
                    //Move to top if the event is a Favourite
                    eventsList.remove(event);
                    eventsList.add(0, event);
                }
            }
        }
        if (eventsList.size() > 10) {
            eventsList.subList(10, eventsList.size()).clear();
        }

        eventsAdapter = new HomeEventsAdapter(eventsList, null, getActivity());
        eventsRV.setAdapter(eventsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        eventsRV.setLayoutManager(linearLayoutManager);
        eventsAdapter.notifyDataSetChanged();
        eventsMore.setOnClickListener(v -> {
            //MORE Clicked - Take user to Events Fragment
            ((MainActivity) getActivity()).setFragment(MainActivity.TAG_SCHEDULE);

        });
        if (eventsList.size() == 0) {
            view.findViewById(R.id.home_events_none_text_view).setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setOnRefreshListener(() -> {
            boolean isConnectedTemp = NetworkUtils.isInternetConnected(getContext());
            if (isConnectedTemp) {
                displayRevelsLiveFeed();
                fetchResults();
                new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 5000);
            } else {
                Snackbar.make(view, "Check connection!", Snackbar.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }

        });
        displayRevelsLiveFeed();
        fetchResults();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!sp.getBoolean("subsequentRuns", false)) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            editor.putBoolean("subsequentRuns", true);
            editor.apply();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                            TapTarget.forView(linearLayoutManager.findViewByPosition(0), "This is a Home Event", "Long press on this, or on the Events in schedule or Categories to register to it.")
                                    // All options below are optional
                                    .outerCircleColor(R.color.colorPrimary)      // Specify a color for the outer circle
                                    .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                    .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                    .titleTextSize(20)                  // Specify the size (in sp) of the title text
                                    .titleTextColor(R.color.white)      // Specify the color of the title text
                                    .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                    .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                    .textColor(R.color.white)            // Specify a color for both the title and description text
                                    .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                    .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                    .drawShadow(true)                   // Whether to draw a drop shadow or not
                                    .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                    .tintTarget(true)                   // Whether to tint the target view's color
                                    .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                    .targetRadius(60),                  // Specify the target radius (in dp)
                            new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                                //(android.widget.Toolbar) ((MainActivity)getActivity()).findViewById(R.id.toolbar))
                                @Override
                                public void onTargetClick(TapTargetView view) {
                                    super.onTargetClick(view);      // This call is optional
                                    TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                                            TapTarget.forToolbarMenuItem((Toolbar) getActivity().findViewById(R.id.toolbar) ,
                                                    R.id.action_profile, "This takes you to your Profile",
                                                    "You can add events you've registered for, add team members or leave a team")
                                    // All options below are optional
                                                    .outerCircleColor(R.color.colorPrimary)      // Specify a color for the outer circle
                                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                            .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                            .titleTextSize(20)                  // Specify the size (in sp) of the title text
                                            .titleTextColor(R.color.white)      // Specify the color of the title text
                                            .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                            .descriptionTextColor(R.color.white)  // Specify the color of the description text
                                            .textColor(R.color.white)            // Specify a color for both the title and description text
                                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                            .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                            .tintTarget(true)                   // Whether to tint the target view's color
                                            .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                            .targetRadius(35),                  // Specify the target radius (in dp)
                                            new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                                                @Override
                                                public void onTargetClick(TapTargetView view) {
                                                    super.onTargetClick(view);      // This call is optional
                                                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                                    if (sp.getBoolean("loggedIn", false))
                                                        startActivity(new Intent(getActivity(), ProfileActivity.class));
                                                    else {
                                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            });
                }
            }, 1250);
        }

        return view;
    }

    public void displayRevelsLiveFeed() {
        progressBar.setVisibility(View.VISIBLE);
        homeRV.setVisibility(View.GONE);
        revelsLiveTextView.setVisibility(View.GONE);
        Call<RevelsLiveListModel> call = RevelsLiveAPIClient.getInterface().getRevelsLiveFeed();
        processes++;
        call.enqueue(new Callback<RevelsLiveListModel>() {
            @Override
            public void onResponse(@NonNull Call<RevelsLiveListModel> call,
                                   @NonNull Response<RevelsLiveListModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    feed = response.body();
                    instaAdapter = new HomeAdapter(feed);
                    homeRV.setAdapter(instaAdapter);
                    homeRV.setLayoutManager(new LinearLayoutManager(getContext()));
                    ViewCompat.setNestedScrollingEnabled(homeRV, false);
                }
                homeRV.setVisibility(View.VISIBLE);
                initialLoad = false;

            }

            @Override
            public void onFailure(@NonNull Call<RevelsLiveListModel> call, @NonNull Throwable t) {
                if (initialLoad) progressBar.setVisibility(View.GONE);
                revelsLiveTextView.setVisibility(View.VISIBLE);
                Log.i(TAG, "onFailure: Error code " + t.getMessage());
                initialLoad = false;
            }
        });
    }

    public void updateResultsList() {
        RealmResults<ResultModel> results = mDatabase.where(ResultModel.class)
                .findAll()
                .sort("eventName", Sort.ASCENDING, "position", Sort.ASCENDING);
        List<ResultModel> resultsArrayList;
        resultsArrayList = mDatabase.copyFromRealm(results);
        if (!resultsArrayList.isEmpty()) {
            resultsList.clear();
            List<String> eventNamesList = new ArrayList<>();
            for (ResultModel result : resultsArrayList) {
                String eventName = result.getEventName() + " " + result.getRound();
                if (eventNamesList.contains(eventName)) {
                    resultsList.get(eventNamesList.indexOf(eventName)).eventResultsList.add(result);
                } else {
                    EventResultModel eventResult = new EventResultModel();
                    eventResult.eventName = result.getEventName();
                    eventResult.eventRound = result.getRound();
                    eventResult.eventCategory = result.getCatName();
                    eventResult.eventResultsList.add(result);
                    resultsList.add(eventResult);
                    eventNamesList.add(eventName);
                }
            }
        }
        Log.i(TAG, "displayResults: resultsList size:" + resultsList.size());
        //Moving favourite results to top
        for (int i = 0; i < resultsList.size(); i++) {
            EventResultModel result = resultsList.get(i);
            if (isFavourite(result)) {
                resultsList.remove(result);
                resultsList.add(0, result);
            }
        }
        //Picking first 10 results to display
        if (resultsList.size() > 10) {
            resultsList.subList(10, resultsList.size()).clear();
        }
        resultsAdapter.notifyDataSetChanged();

        if (resultsList.size() == 0) {
            homeResultsItem.setVisibility(View.GONE);
        } else {
            homeResultsItem.setVisibility(View.VISIBLE);
        }
    }

    public void fetchResults() {
        processes++;
        Call<ResultsListModel> callResultsList = APIClient.getAPIInterface().getResultsList();
        callResultsList.enqueue(new Callback<ResultsListModel>() {
            List<ResultModel> results = new ArrayList<>();

            @Override
            public void onResponse(Call<ResultsListModel> call, Response<ResultsListModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    results = response.body().getData();
                    mDatabase.beginTransaction();
                    mDatabase.where(ResultModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(results);
                    mDatabase.commitTransaction();
                    //homeResultsItem.setVisibility(View.VISIBLE);
                    updateResultsList();
                    resultsNone.setVisibility(View.GONE);
                    resultsNone.setText("");
                }
            }

            @Override
            public void onFailure(Call<ResultsListModel> call, Throwable t) {
                if (homeResultsItem.getVisibility() == View.VISIBLE)
                    //homeResultsItem.setVisibility(View.GONE);
                    processes--;
            }
        });
    }

    public boolean isFavourite(ScheduleModel event) {
        RealmResults<FavouritesModel> favouritesRealmList = mDatabase.where(FavouritesModel.class).equalTo("id", event.getEventId()).contains("day", event.getDay()).equalTo("round", event.getRound()).findAll();
        return (favouritesRealmList.size() != 0);
    }

    public boolean isFavourite(EventResultModel result) {
        RealmResults<FavouritesModel> favouritesRealmList = mDatabase.where(FavouritesModel.class).equalTo("eventName", result.eventName).findAll();
        return (favouritesRealmList.size() != 0);
    }

    public View initViews(LayoutInflater inflater, ViewGroup container) {

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
        revelsLiveTextView = view.findViewById(R.id.revels_live_textview);
        swipeRefreshLayout = view.findViewById(R.id.home_swipe_refresh_layout);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile: {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (sp.getBoolean("loggedIn", false))
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                else {
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

    private void launchCCT(String url, Context context) {

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.mitpost));
        // set toolbar color and/or setting custom actions before invoking build()
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(context, Uri.parse(url));
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

