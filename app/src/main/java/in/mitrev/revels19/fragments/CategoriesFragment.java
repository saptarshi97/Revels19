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
import android.widget.SearchView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    AppBarLayout appBarLayout;
    private List<CategoryModel> categoriesList = new ArrayList<>();
    private Realm database;
    private RecyclerView categoriesRecyclerView;
    private LinearLayout noDataLayout;
    private CategoriesAdapter categoriesAdapter;
    private ProgressDialog dialog;
    private String TAG = CategoriesFragment.class.getSimpleName();

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        database = Realm.getDefaultInstance();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().setTitle(R.string.bottom_nav_categories);
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        noDataLayout = view.findViewById(R.id.no_category_data_layout);
        categoriesAdapter = new CategoriesAdapter(categoriesList, getActivity());
        categoriesRecyclerView.setAdapter(categoriesAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        categoriesRecyclerView.setLayoutManager(gridLayoutManager);

        if (database.where(CategoryModel.class).findAll().size() != 0) {
            displayData();
        } else {
            Log.i(TAG, "onCreateView: No categories in realm");
        }
        return view;
    }

    private void displayData() {
        if (database != null) {
            categoriesList.clear();
            List<CategoryModel> categoryResults = database.copyFromRealm(database
                    .where(CategoryModel.class)
                    .notEqualTo("type", "SUPPORTING")
                    .findAll()
                    .sort("categoryName")
            );
            if (!categoryResults.isEmpty()) {
                categoriesList.clear();
                categoriesList.addAll(categoryResults);
                categoriesAdapter.notifyDataSetChanged();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_categories, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = null;
        if (getActivity() != null) {
            searchManager = (SearchManager) getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);
        }
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                displaySearchData(query);
                Revels19.searchOpen = 1;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                displaySearchData(newText);
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

    private void displaySearchData(String text) {
        text = text.toLowerCase();
        if (database != null) {
            List<CategoryModel> categoryResults = database.copyFromRealm(database
                    .where(CategoryModel.class)
                    .notEqualTo("type", "SUPPORTING")
                    .findAll()
                    .sort("categoryName")
            );
            categoriesList.clear();
            for (int i = 0; i < categoryResults.size(); i++) {
                if (categoryResults.get(i).getCategoryName().toLowerCase().contains(text)) {
                    categoriesList.add(categoryResults.get(i));
                }
            }
            categoriesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                if (sharedPreferences.getBoolean("loggedIn", false)) {
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                return true;

            case R.id.action_favourites:
                startActivity(new Intent(getActivity(), FavouritesActivity.class));
                return true;
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
        categoriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (database != null) {
            database.close();
            database = null;
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.hide();
        }
    }
}
