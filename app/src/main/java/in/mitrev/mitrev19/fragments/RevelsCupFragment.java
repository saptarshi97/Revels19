package in.mitrev.mitrev19.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import in.mitrev.mitrev19.activities.FavouritesActivity;
import in.mitrev.mitrev19.activities.ProfileActivity;
import in.mitrev.mitrev19.R;
import io.realm.Realm;


public class RevelsCupFragment extends Fragment {

    private Realm realm;
    private View view;

    public RevelsCupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.revels_cup);
        realm = Realm.getDefaultInstance();

        if (getActivity() != null) {
            getActivity().setTitle(R.string.bottom_nav_revels_cup);
            AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar);
            appBarLayout.setExpanded(true, true);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_revels_cup, container, false);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_results, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                return true;
            case R.id.action_profile:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                return true;
            case R.id.action_favourites:
                startActivity(new Intent(getActivity(), FavouritesActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
