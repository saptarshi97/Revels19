package in.mitrev.revels19.activities;

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
import androidx.fragment.app.FragmentTransaction;
import in.mitrev.revels19.R;
import in.mitrev.revels19.fragments.CategoriesFragment;
import in.mitrev.revels19.fragments.HomeFragment;
import in.mitrev.revels19.fragments.ResultsFragment;
import in.mitrev.revels19.fragments.RevelsCupFragment;
import in.mitrev.revels19.fragments.ScheduleFragment;
import in.mitrev.revels19.models.categories.CategoriesListModel;
import in.mitrev.revels19.models.categories.CategoryModel;
import in.mitrev.revels19.models.events.EventDetailsModel;
import in.mitrev.revels19.models.events.EventsListModel;
import in.mitrev.revels19.models.events.ScheduleListModel;
import in.mitrev.revels19.models.events.ScheduleModel;
import in.mitrev.revels19.models.results.ResultModel;
import in.mitrev.revels19.models.results.ResultsListModel;
import in.mitrev.revels19.network.APIClient;
import in.mitrev.revels19.utilities.NetworkUtils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    String TAG = "MainActivity";
    BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private Realm mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = Realm.getDefaultInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_nav);
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
        setFragment(new HomeFragment());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.action_home:
                return setFragment(new HomeFragment());
            case R.id.action_schedule:
                return setFragment(new ScheduleFragment());
            case R.id.action_categories:
                return setFragment(new CategoriesFragment());
            case R.id.action_revels_cup:
                return setFragment(new RevelsCupFragment());
            case R.id.action_results:
                return setFragment(new ResultsFragment());
        }
        return false;
    }

    /**
     * Set the fragment for this activity
     *
     * @param fragment to set
     */
    public boolean setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        return true;
    }

    public void changeFragment(Fragment fragment){
        if(fragment.getClass() ==  CategoriesFragment.class){
            try{bottomNavigationView.setSelectedItemId(R.id.action_categories);}
            catch (NullPointerException e){e.printStackTrace();}
        }else if(fragment.getClass() == ScheduleFragment.class){
            bottomNavigationView.setSelectedItemId(R.id.action_schedule);
        }else{
            Log.i(TAG, "changeFragment: Unexpected fragment passed!!");
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left).replace(R.id.main_frame_layout, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_workshops:
                startActivity(new Intent(MainActivity.this, WorkshopsActivity.class));
                return true;
            case R.id.menu_proshow_portal:
                //Launch custom chrome tab
                return true;
            case R.id.menu_about_us:
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                return true;
            case R.id.menu_developers:
                startActivity(new Intent(MainActivity.this, DevelopersActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAllFromInternet() {
        loadResultsFromInternet();
        loadEventsFromInternet();
        loadSchedulesFromInternet();
        loadCategoriesFromInternet();
    }

    private void loadEventsFromInternet() {

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
    }

    private void loadResultsFromInternet() {
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
    }
}