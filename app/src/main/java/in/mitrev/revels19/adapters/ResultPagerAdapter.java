package in.mitrev.revels19.adapters;


import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ResultPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList = new ArrayList<>();
    List<String> titleList = new ArrayList<>();

    public ResultPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void add(Fragment fragment, String title) {
        Bundle bundle = new Bundle();
        switch (title) {
            case "Event Results":
                bundle.putInt("Event Results", 1);
                break;
            case "Revels Cup":
                bundle.putInt("Revels Cup", 2);
                break;
        }
        fragment.setArguments(bundle);
        fragmentList.add(fragment);
        titleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
