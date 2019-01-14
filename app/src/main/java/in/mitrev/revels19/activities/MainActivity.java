package in.mitrev.revels19.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import in.mitrev.revels19.R;
import in.mitrev.revels19.fragments.CategoriesFragment;
import in.mitrev.revels19.fragments.HomeFragment;
import in.mitrev.revels19.fragments.ResultsFragment;
import in.mitrev.revels19.fragments.RevelsCupFragment;
import in.mitrev.revels19.fragments.ScheduleFragment;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    String TAG = "MainActivity";
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  mDatabase = Realm.getDefaultInstance();
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
}
