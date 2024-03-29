package in.mitrev.mitrev19.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import in.mitrev.mitrev19.R;
import in.mitrev.mitrev19.fragments.CategoriesFragment;
import in.mitrev.mitrev19.fragments.HomeFragment;
import in.mitrev.mitrev19.fragments.ResultsFragment;
import in.mitrev.mitrev19.fragments.ScheduleFragment;
import in.mitrev.mitrev19.models.categories.CategoriesListModel;
import in.mitrev.mitrev19.models.categories.CategoryModel;
import in.mitrev.mitrev19.models.events.EventDetailsModel;
import in.mitrev.mitrev19.models.events.EventsListModel;
import in.mitrev.mitrev19.models.events.ScheduleListModel;
import in.mitrev.mitrev19.models.events.ScheduleModel;
import in.mitrev.mitrev19.models.results.ResultModel;
import in.mitrev.mitrev19.models.results.ResultsListModel;
import in.mitrev.mitrev19.network.APIClient;
import in.mitrev.mitrev19.utilities.NetworkUtils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private Realm mDatabase;
    public int fragmentIndex;
    public static final String TAG_HOME = "HomeFragment";
    public static final String TAG_SCHEDULE = "ScheduleFragment";
    public static final String TAG_CATEGORIES = "CategoriesFragment";
    public static final String TAG_RESULTS = "ResultsFragment";


    @Override
    public void onBackPressed() {
        switch (fragmentIndex) {
            case 1:
            case 2:
            case 3:
                setFragment(TAG_HOME);
                setBottomNavSelectedItem(R.id.action_home);
                break;
            default:
                finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = Realm.getDefaultInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.main_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        View activeLabel = bottomNavigationView.findViewById(com.google.android.material.R.id.largeLabel);
        if (activeLabel != null && activeLabel instanceof TextView)
            activeLabel.setPadding(0, 0, 0, 0);

        boolean isConnected = NetworkUtils.isInternetConnected(this);
        if (isConnected) {
            loadAllFromInternet();
            Log.i(TAG, "onCreate: Connected and background updated");
        }
        Log.d(TAG, "onCreate: " + "home!");
        setFragment(TAG_HOME);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.action_home:
                return setFragment(TAG_HOME);
            case R.id.action_schedule:
                return setFragment(TAG_SCHEDULE);
            case R.id.action_categories:
                return setFragment(TAG_CATEGORIES);
            case R.id.action_results:
                return setFragment(TAG_RESULTS);
        }
        return false;
    }



    public void setTitle() {
        switch (fragmentIndex) {
            case 0:
                setTitle(R.string.app_name);
                break;
            case 1:
                setTitle(R.string.bottom_nav_schedule);
                break;
            case 2:
                setTitle(R.string.bottom_nav_categories);
                break;
            default:
                setTitle(R.string.bottom_nav_results);
        }
    }

    public void setFragmentIndex(String tag) {
        switch (tag) {
            case TAG_HOME:
                fragmentIndex = 0;
                break;
            case TAG_SCHEDULE:
                fragmentIndex = 1;
                break;
            case TAG_CATEGORIES:
                fragmentIndex = 2;
                break;
            default:
                fragmentIndex = 3;
        }
    }

    public int getBottomNavId(String tag) {
        switch (tag) {
            case TAG_HOME:
                return R.id.action_home;
            case TAG_SCHEDULE:
                return R.id.action_schedule;
            case TAG_CATEGORIES:
                return R.id.action_categories;
            default:
                return R.id.action_results;
        }
    }

    public boolean setFragment(String tag) {
        setFragmentIndex(tag);
        setTitle();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        Log.e(TAG, "setFragment");
        if (fragment != null) {
            Log.e(TAG, "fragment != null");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            Log.e(TAG, "fragment == null");
            switch (tag) {
                case TAG_HOME:
                    Log.e(TAG, "tag == home");
                    fragment = new HomeFragment();
                    break;
                case TAG_SCHEDULE:
                    Log.e(TAG, "tag == schedule");
                    fragment = new ScheduleFragment();
                    break;
                case TAG_CATEGORIES:
                    Log.e(TAG, "tag == categories");
                    fragment = new CategoriesFragment();
                    break;
                case TAG_RESULTS:
                    Log.e(TAG, "tag == results");
                    fragment = new ResultsFragment();
                    break;
                default:
                    Log.d(TAG, "default");
                    fragment = new HomeFragment();
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .addToBackStack(tag)
                    .commit();
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about_us:
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                return true;
            case R.id.menu_developers:
                startActivity(new Intent(MainActivity.this, DevelopersActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setBottomNavSelectedItem(int id) {
        bottomNavigationView.setSelectedItemId(id);
    }

    private void loadAllFromInternet() {
        loadResultsFromInternet();
        loadEventsFromInternet();
        loadSchedulesFromInternet();
        loadCategoriesFromInternet();
    }

    private void loadEventsFromInternet() {
        try {
            Call<EventsListModel> eventsCall = APIClient.getAPIInterface().getEventsList();
            eventsCall.enqueue(new Callback<EventsListModel>() {
                @Override
                public void onResponse(Call<EventsListModel> call, Response<EventsListModel> response) {
                    if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                        Log.d(TAG, "onResponse: Loading events....");
                        mDatabase.beginTransaction();
                        mDatabase.where(EventDetailsModel.class).findAll().deleteAllFromRealm();
                        mDatabase.copyToRealm(response.body().getEvents());
                        mDatabase.commitTransaction();
                        Log.d(TAG, "Events updated in background");
                    }
                }

                @Override
                public void onFailure(Call<EventsListModel> call, Throwable t) {
                    Log.d(TAG, "onFailure: Events not updated ");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadCategoriesFromInternet() {
        try {
            Call<CategoriesListModel> categoriesCall = APIClient.getAPIInterface().getCategoriesList();
            categoriesCall.enqueue(new Callback<CategoriesListModel>() {
                @Override
                public void onResponse(@NonNull Call<CategoriesListModel> call, @NonNull Response<CategoriesListModel> response) {
                    if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                        mDatabase.beginTransaction();
                        mDatabase.where(CategoryModel.class).findAll().deleteAllFromRealm();
                        mDatabase.copyToRealmOrUpdate(response.body().getCategoriesList());
                        mDatabase.commitTransaction();
                        Log.d(TAG, response.body().getCategoriesList().size() + "Categories updated in background");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CategoriesListModel> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: Categories not updated");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSchedulesFromInternet() {
        try {
            Call<ScheduleListModel> schedulesCall = APIClient.getAPIInterface().getScheduleList();
            schedulesCall.enqueue(new Callback<ScheduleListModel>() {
                @Override
                public void onResponse(Call<ScheduleListModel> call, Response<ScheduleListModel> response) {
                    if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                        mDatabase.beginTransaction();
                        mDatabase.where(ScheduleModel.class).findAll().deleteAllFromRealm();
                        mDatabase.copyToRealm(response.body().getData());
                        mDatabase.commitTransaction();
                        Log.d(TAG, "Schedule updated in background");
                    }
                }

                @Override
                public void onFailure(Call<ScheduleListModel> call, Throwable t) {
                    Log.d(TAG, "onFailure: Schedules not updated ");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadResultsFromInternet() {
        try {
            Call<ResultsListModel> resultsCall = APIClient.getAPIInterface().getResultsList();
            resultsCall.enqueue(new Callback<ResultsListModel>() {
                List<ResultModel> results = new ArrayList<ResultModel>();

                @Override
                public void onResponse(@NonNull Call<ResultsListModel> call,
                                       @NonNull Response<ResultsListModel> response) {
                    if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                        results = response.body().getData();
                        mDatabase.beginTransaction();
                        mDatabase.where(ResultModel.class).findAll().deleteAllFromRealm();
                        mDatabase.copyToRealm(results);
                        mDatabase.commitTransaction();
                        Log.d(TAG, "Results updated in the background" + results.size());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResultsListModel> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: Results not updated");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}