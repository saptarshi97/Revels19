package in.mitrev.revels19.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
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
import in.mitrev.revels19.adapters.HomeResultsAdapter;
import in.mitrev.revels19.models.categories.CategoryModel;
import in.mitrev.revels19.models.events.ScheduleModel;
import in.mitrev.revels19.models.favourites.FavouritesModel;
import in.mitrev.revels19.models.instagram.InstagramFeed;
import in.mitrev.revels19.models.results.EventResultModel;
import in.mitrev.revels19.models.results.ResultModel;
import in.mitrev.revels19.models.results.ResultsListModel;
import in.mitrev.revels19.network.APIClient;
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
    private List<EventResultModel> resultsList = new ArrayList<>();
    private HomeCategoriesAdapter categoriesAdapter;
    private HomeEventsAdapter eventsAdapter;
    private RecyclerView homeRV;
    private RecyclerView resultsRV;
    private RecyclerView categoriesRV;
    private RecyclerView eventsRV;
    private View blogButton, newsletterButton;
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
    private HomeResultsAdapter resultsAdapter;
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
        getActivity().setTitle(R.string.revels19);
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
        ((MainActivity) getActivity()).fragmentIndex = 0;
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
//        progressBar = view.findViewById(R.id.insta_progress);
//        instaTextView = view.findViewById(R.id.insta_text_view);

        blogButton = view.findViewById(R.id.home_blog);
        newsletterButton = view.findViewById(R.id.home_newsletter);
        blogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCCT("https://themitpost.com/revels19-liveblog/", getContext());
            }
        });

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

        if (imageSlider == null)
            imageSlider = view.findViewById(R.id.home_image_slider);
//        getImageURLSfromFirebase();
//        sliderInit();

        resultsAdapter = new HomeResultsAdapter(resultsList, getActivity());
        resultsRV.setAdapter(resultsAdapter);
        resultsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        updateResultsList();
        resultsMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MORE Clicked - Take user to Results Fragment
                ((MainActivity) getActivity()).setBottomNavSelectedItem(R.id.action_results);
                ((MainActivity) getActivity()).setFragment(ResultsFragment.newInstance());
            }
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
            ((MainActivity) getActivity()).setBottomNavSelectedItem(R.id.action_categories);
            ((MainActivity) getActivity()).setFragment(CategoriesFragment.newInstance());
        });
        if (categoriesList.size() == 0) {
            view.findViewById(R.id.home_categories_none_text_view).setVisibility(View.VISIBLE);
        }

        //Display Events of current day
        Calendar cal = Calendar.getInstance();
        Calendar day1 = new GregorianCalendar(2019, 1, 26);
        Calendar day2 = new GregorianCalendar(2019, 1, 27);
        Calendar day3 = new GregorianCalendar(2019, 1, 28);
        Calendar day4 = new GregorianCalendar(2019, 2, 1);
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
            List<ScheduleModel> tempEventsList = mDatabase
                    .copyFromRealm(mDatabase.where(ScheduleModel.class).findAll());

            for (int i = 0; i < tempEventsList.size(); i++) {
                ScheduleModel scheduleModel = tempEventsList.get(i);
                if (scheduleModel.getDay().equals("2")) {
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
        eventsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        eventsAdapter.notifyDataSetChanged();
        eventsMore.setOnClickListener(v -> {
            //MORE Clicked - Take user to Events Fragment
            ((MainActivity) getActivity()).setBottomNavSelectedItem(R.id.action_schedule);
            ((MainActivity) getActivity()).setFragment(ScheduleFragment.newInstance());

        });
        if (eventsList.size() == 0) {
            view.findViewById(R.id.home_events_none_text_view).setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setOnRefreshListener(() -> {
            boolean isConnectedTemp = NetworkUtils.isInternetConnected(getContext());
            if (isConnectedTemp) {
//                displayInstaFeed();
                fetchResults();
                new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 5000);
            } else {
                Snackbar.make(view, "Check connection!", Snackbar.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }

        });
        return view;
    }

    private void sliderInit() {   //Updating the SliderLayout with images
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

    public void displayInstaFeed() {
        if (initialLoad) progressBar.setVisibility(View.VISIBLE);
        homeRV.setVisibility(View.GONE);
        instaTextView.setVisibility(View.GONE);
        Call<InstagramFeed> call = InstaFeedAPIClient.getInterface().getInstagramFeed();
        processes++;
        call.enqueue(new Callback<InstagramFeed>() {
            @Override
            public void onResponse(@NonNull Call<InstagramFeed> call,
                                   @NonNull Response<InstagramFeed> response) {
                if (initialLoad) progressBar.setVisibility(View.GONE);
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
            public void onFailure(@NonNull Call<InstagramFeed> call, @NonNull Throwable t) {
                if (initialLoad) progressBar.setVisibility(View.GONE);
                instaTextView.setVisibility(View.VISIBLE);
                Log.i(TAG, "onFailure: Error Fetching insta feed ");
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
        instaTextView = view.findViewById(R.id.instagram_textview);
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

    private void launchCCT(String url, Context context){

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

