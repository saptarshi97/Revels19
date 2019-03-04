package in.mitrev.revels19.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import in.mitrev.revels19.R;
import in.mitrev.revels19.models.categories.CategoriesListModel;
import in.mitrev.revels19.models.categories.CategoryModel;
import in.mitrev.revels19.models.events.EventDetailsModel;
import in.mitrev.revels19.models.events.EventsListModel;
import in.mitrev.revels19.models.events.ScheduleListModel;
import in.mitrev.revels19.models.events.ScheduleModel;
import in.mitrev.revels19.network.APIClient;
import in.mitrev.revels19.utilities.NetworkUtils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private Realm mDatabase;
    private boolean eventsLoaded = false;
    private boolean scheduleLoaded = false;
    private Context context=this;
    private View noConnection;
    private FrameLayout splashLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mDatabase = Realm.getDefaultInstance();

        final ImageView phoenixBody = (ImageView)findViewById(R.id.img_phoenix_body);
        final ImageView phoenixFlame = (ImageView)findViewById(R.id.img_phoenix_flame);
        final ImageView phoenixHalo = (ImageView)findViewById(R.id.img_phoenix_halo);
        final ImageView revelsText = (ImageView)findViewById(R.id.splash_revels_text);
        splashLayout=findViewById(R.id.splash_layout);
        noConnection=findViewById(R.id.splash_no_connection_layout);

        phoenixBody.setAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_in_first));
        phoenixFlame.setAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.slide_in_right));
        phoenixHalo.setAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.slide_in_right));
        revelsText.setAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.slide_in_from_top));

        loadThemGuns();

    }

    private void loadMain(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadThemGuns(){
        if (mDatabase.where(ScheduleModel.class).findAll().size() == 0 && NetworkUtils.isInternetConnected(this)){

            APIClient.APIInterface apiInterface = APIClient.getAPIInterface();
            Call<EventsListModel> eventsCall = apiInterface.getEventsList();
            Call<ScheduleListModel> scheduleCall = apiInterface.getScheduleList();

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
                            Log.d("SplashActivity", response.body().getCategoriesList().size() + "Categories updated in background");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CategoriesListModel> call, @NonNull Throwable t) {
                        Log.d("SplashActivity", "onFailure: Categories not updated");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                eventsCall.enqueue(new Callback<EventsListModel>() {
                    @Override
                    public void onResponse(Call<EventsListModel> call, Response<EventsListModel> response) {
                        if (response.body() != null && mDatabase != null) {
                            mDatabase.beginTransaction();
                            mDatabase.where(EventDetailsModel.class).findAll().deleteAllFromRealm();
                            mDatabase.copyToRealm(response.body().getEvents());
                            mDatabase.commitTransaction();
                        }
                        eventsLoaded = true;
                        if (scheduleLoaded) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadMain();
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onFailure(Call<EventsListModel> call, Throwable t) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadMain();
                            }
                        }, 1000);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                scheduleCall.enqueue(new Callback<ScheduleListModel>() {
                    @Override
                    public void onResponse(Call<ScheduleListModel> call, Response<ScheduleListModel> response) {
                        if (response.body() != null && mDatabase != null) {
                            mDatabase.beginTransaction();
                            mDatabase.where(ScheduleModel.class).findAll().deleteAllFromRealm();
                            mDatabase.copyToRealm(response.body().getData());
                            mDatabase.commitTransaction();
                        }
                        scheduleLoaded = true;
                        if (eventsLoaded) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadMain();
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onFailure(Call<ScheduleListModel> call, Throwable t) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadMain();
                            }
                        }, 1000);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(mDatabase.where(ScheduleModel.class).findAll().size() == 0 && !NetworkUtils.isInternetConnected(this)){
            splashLayout.setVisibility(View.GONE);
            noConnection.setVisibility(View.VISIBLE);
            Button retry=noConnection.findViewById(R.id.retry);
            retry.setOnClickListener(view -> {
                if(NetworkUtils.isInternetConnected(context)){
                    splashLayout.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    loadThemGuns();
                }
            });
        }
        else{
            new Handler().postDelayed(() -> loadMain(), 2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }


}