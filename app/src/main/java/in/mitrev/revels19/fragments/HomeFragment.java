package in.mitrev.revels19.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import in.mitrev.revels19.R;
import in.mitrev.revels19.activities.FavouritesActivity;
import in.mitrev.revels19.activities.ProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getActivity() != null) {
            getActivity().setTitle(R.string.app_name);
            AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar);
            appBarLayout.setExpanded(true, true);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                return true;
            case R.id.menu_favourites:
                startActivity(new Intent(getActivity(), FavouritesActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
