package in.mitrev.revels19.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import in.mitrev.revels19.R;
import in.mitrev.revels19.fragments.CategoriesFragment;
import in.mitrev.revels19.fragments.HomeFragment;
import in.mitrev.revels19.fragments.ResultsFragment;
import in.mitrev.revels19.fragments.RevelsCupFragment;
import in.mitrev.revels19.fragments.ScheduleFragment;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

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
    private boolean setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
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
            case R.id.menu_workshops:
                //open workshops activity
                return true;
            case R.id.menu_proshow_portal:
                //Launch custom chrome tab
                return true;
            case R.id.menu_about_us:
                //Lauch about us activity
                return true;
            case R.id.menu_developers:
                //Open developers activity
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
