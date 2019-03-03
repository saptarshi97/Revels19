package in.mitrev.revels19.fragments;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.revels19.R;
import in.mitrev.revels19.activities.FavouritesActivity;
import in.mitrev.revels19.activities.LoginActivity;
import in.mitrev.revels19.activities.ProfileActivity;
import in.mitrev.revels19.adapters.CategoriesAdapter;
import in.mitrev.revels19.application.Revels19;
import in.mitrev.revels19.models.categories.CategoryModel;
import io.realm.Realm;
import io.realm.RealmResults;

public class CategoriesFragment extends Fragment {

    private static final int LOAD_CATEGORIES = 0;
    private static final int UPDATE_CATEGORIES = 1;
    private List<CategoryModel> categoriesList = new ArrayList<>();
    AppBarLayout appBarLayout;
    private Realm mDatabase;
    private CategoriesAdapter adapter;
    private ProgressDialog dialog;
    private MenuItem searchItem;
    private RecyclerView categoriesRecyclerView;
    private String TAG = "CategoriesFragment";
    private LinearLayout noDataLayout;

    public CategoriesFragment() {
    }

    public static CategoriesFragment newInstance() {
        CategoriesFragment fragment = new CategoriesFragment();
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.categories);
        mDatabase = Realm.getDefaultInstance();
        Log.d(TAG, "onCreate: mDatabase" + mDatabase);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        noDataLayout = view.findViewById(R.id.no_category_data_layout);
        adapter = new CategoriesAdapter(categoriesList, getActivity());
        categoriesRecyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        categoriesRecyclerView.setLayoutManager(gridLayoutManager);
        //categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoriesRecyclerView.getContext(),DividerItemDecoration.VERTICAL);
        //categoriesRecyclerView.addItemDecoration(dividerItemDecoration);

        if (mDatabase.where(CategoryModel.class).findAll().size() != 0) {
            displayData();
        } else {
            Log.i(TAG, "onCreateView: No categories in realm");
        }
        return view;
    }

    private void displayData() {
        if (mDatabase != null) {
            categoriesList.clear();

            List<CategoryModel> categoryResults = mDatabase.copyFromRealm(mDatabase
                    .where(CategoryModel.class)
                    .notEqualTo("categoryType", "SUPPORTING")
                    .findAll().sort("categoryName"));
            if (!categoryResults.isEmpty()) {
                Log.d(TAG, "displayData: categorysize : " + categoryResults.size());
                categoriesList.clear();
                categoriesList.addAll(categoryResults);
                Collections.sort(categoriesList, (a, b) -> a.getCategoryName().compareTo(b.getCategoryName()));
                adapter.notifyDataSetChanged();
                if (categoriesRecyclerView.getVisibility() == View.GONE) {
                    categoriesRecyclerView.setVisibility(View.VISIBLE);
                    noDataLayout.setVisibility(View.GONE);
                }
            } else {
                if (categoriesRecyclerView.getVisibility() == View.VISIBLE) {
                    categoriesRecyclerView.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (categoriesRecyclerView.getVisibility() == View.VISIBLE) {
                categoriesRecyclerView.setVisibility(View.GONE);
                noDataLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void displaySearchData(String text) {
        /*if(text.equals("Memelord")){
            Intent intent = new Intent(getContext(),EasterEggActivity.class);
            startActivity(intent);
        }*/
        text = text.toLowerCase();
        if (mDatabase != null) {
            RealmResults<CategoryModel> categoryResults = mDatabase.where(CategoryModel.class)
                    .notEqualTo("categoryType", "SUPPORTING")
                    .findAll()
                    .sort("categoryName");
            List<CategoryModel> temp = mDatabase.copyFromRealm(categoryResults);
            categoriesList.clear();
            for (int i = 0; i < temp.size(); i++) {
                if (temp.get(i).getCategoryName().toLowerCase().contains(text)) {
                    categoriesList.add(temp.get(i));
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        if (dialog != null && dialog.isShowing())
            dialog.hide();
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_categories, menu);
        searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                displaySearchData(text);
                Revels19.searchOpen = 1;
                return false;
            }
            @Override
            public boolean onQueryTextChange(String text) {
                displaySearchData(text);
                Revels19.searchOpen = 1;
                return false;
            }
        });
        searchView.setQueryHint("Search Categories");
        searchView.setOnCloseListener(() -> {
            displayData();
            searchView.clearFocus();
            Revels19.searchOpen = 1;
            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile: {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (sp.getBoolean("loggedIn", false)) startActivity(new Intent(getActivity(),
                        ProfileActivity.class));
                else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                return true;
            }
            case R.id.menu_favourites: {
                startActivity(new Intent(getActivity(), FavouritesActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        setHasOptionsMenu(false);
        setMenuVisibility(false);
    }
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


}