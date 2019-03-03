package in.mitrev.revels19.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import in.mitrev.revels19.R;
import in.mitrev.revels19.activities.FavouritesActivity;
import in.mitrev.revels19.activities.LoginActivity;
import in.mitrev.revels19.activities.ProfileActivity;
import in.mitrev.revels19.adapters.ResultsAdapter;
import in.mitrev.revels19.models.results.EventResultModel;
import in.mitrev.revels19.models.results.ResultModel;
import in.mitrev.revels19.models.results.ResultsListModel;
import in.mitrev.revels19.network.APIClient;
import in.mitrev.revels19.utilities.NetworkUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultsFragment extends Fragment {
    String TAG = "ResultsFragment";
    Realm mDatabase;
    private List<EventResultModel> resultsList = new ArrayList<>();
    private ResultsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout rootLayout;
    private View view;
    private LinearLayout noResultsLayout;

    private FrameLayout resultsAvailable;

    public ResultsFragment() {
    }

    public static ResultsFragment newInstance() {
        ResultsFragment fragment = new ResultsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.results);
        mDatabase = Realm.getDefaultInstance();

        if (getActivity() != null) {
            getActivity().setTitle(R.string.bottom_nav_results);
            AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar);
            appBarLayout.setExpanded(true, true);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_results, container, false);
        rootLayout = view.findViewById(R.id.results_root_layout);
        resultsAvailable = view.findViewById(R.id.results_available);
        noResultsLayout = view.findViewById(R.id.no_results_layout);
        RecyclerView resultsRecyclerView = view.findViewById(R.id.results_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.results_swipe_refresh_layout);
        adapter = new ResultsAdapter(resultsList, getContext(), getActivity());
        resultsRecyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        resultsRecyclerView.setLayoutManager(gridLayoutManager);
        swipeRefreshLayout.setOnRefreshListener(() -> refreshData(view));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Hello!");
        displayData();
    }

    private void refreshData(View view) {
        boolean isConnectedTemp = NetworkUtils.isInternetConnected(getContext());
        if (isConnectedTemp) {
            updateData();
        } else {
            if (mDatabase == null) {
                resultsAvailable.setVisibility(View.GONE);
                noResultsLayout.setVisibility(View.VISIBLE);
            }
            Snackbar.make(view, "Check connection!", Snackbar.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void displayData() {
        if (mDatabase != null) {
            Log.d(TAG, "displayData: Results Fragment  ");
            resultsAvailable.setVisibility(View.VISIBLE);
            noResultsLayout.setVisibility(View.GONE);
            Log.d(TAG, "displayData: Results Fragment  resultsAvailable");
        } else {
            resultsAvailable.setVisibility(View.GONE);
            noResultsLayout.setVisibility(View.VISIBLE);
            return;
        }

        RealmResults<ResultModel> results = mDatabase.where(ResultModel.class)
                .findAll().sort("eventName", Sort.ASCENDING, "position", Sort.ASCENDING);
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
            resultsAvailable.setVisibility(View.VISIBLE);
            noResultsLayout.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        } else {
            resultsAvailable.setVisibility(View.GONE);
            noResultsLayout.setVisibility(View.VISIBLE);
        }
    }

    public void updateData() {
        Call<ResultsListModel> callResultsList = APIClient.getAPIInterface().getResultsList();
        callResultsList.enqueue(new Callback<ResultsListModel>() {
            List<ResultModel> results = new ArrayList<>();

            @Override
            public void onResponse(@NonNull Call<ResultsListModel> call,
                                   @NonNull Response<ResultsListModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        results = response.body().getData();
                        mDatabase.beginTransaction();
                        mDatabase.where(ResultModel.class).findAll().deleteAllFromRealm();
                        mDatabase.copyToRealm(results);
                        mDatabase.commitTransaction();
                    } catch (Exception e) {
                        Log.d(TAG, "onResponse: " + e.toString());
                    }
                    noResultsLayout.setVisibility(View.GONE);
                    resultsAvailable.setVisibility(View.VISIBLE);
                    displayData();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultsListModel> call, @NonNull Throwable t) {
                if (mDatabase == null) {
                    resultsAvailable.setVisibility(View.GONE);
                    noResultsLayout.setVisibility(View.VISIBLE);
                }
                try {
                    Snackbar.make(rootLayout, "Error fetching results", Snackbar.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_results, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
                swipeRefreshLayout.setRefreshing(true);
                refreshData(view);
                return true;
            }

            case R.id.action_profile: {
                Log.d(TAG, "onOptionsItemSelected: hello from profile");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (sp.getBoolean("loggedIn", false))
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
    public void onStop() {
        super.onStop();
        swipeRefreshLayout.setRefreshing(false);
        noResultsLayout.setVisibility(View.GONE);
        resultsAvailable.setVisibility(View.GONE);
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