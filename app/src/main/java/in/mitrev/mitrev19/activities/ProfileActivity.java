package in.mitrev.mitrev19.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.glxn.qrgen.android.QRCode;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import in.mitrev.mitrev19.models.registration.CreateLeaveTeamResponse;
import in.mitrev.mitrev19.models.registration.ProfileResponse;
import in.mitrev.mitrev19.models.registration.RegisteredEventListModel;
import in.mitrev.mitrev19.models.registration.RegisteredEventModel;
import in.mitrev.mitrev19.R;
import in.mitrev.mitrev19.adapters.RegisteredEventsAdapter;
import in.mitrev.mitrev19.models.events.EventDetailsModel;
import in.mitrev.mitrev19.network.RegistrationClient;
import in.mitrev.mitrev19.utilities.NetworkUtils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements RegisteredEventsAdapter.RegActivityClickListener{
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private static final int ADD_TEAM_MEMBER = 0;
    private TextView name;
    private TextView delID;
    private TextView regno;
    private TextView college;
    private TextView phone;
    private TextView email;
    private LinearLayout profileCard;
    private Button logoutButton;
    private List<RegisteredEventModel> regEventsList;
    private ImageView qrCode;
    private RegisteredEventsAdapter registeredEventsAdapter;
    private RecyclerView eventRegRecyclerView;
    private TextView noEvents;
    private LinearLayout eventRegHeader;
    private ProgressDialog dialog;
    private Realm mDatabase;
//    private EventDetailsModel eventDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = Realm.getDefaultInstance();

        name = findViewById(R.id.name_text_view);
        delID = findViewById(R.id.del_id_text_view);
        phone = findViewById(R.id.phone_text_view);
        email = findViewById(R.id.email_text_view);
        profileCard = findViewById(R.id.profile_layout);
        logoutButton = findViewById(R.id.logout_button);
        qrCode = findViewById(R.id.qr_image_view);
        regno = findViewById(R.id.reg_no_text_view);
        college = findViewById(R.id.college_text_view);
//        eventRegButton = (Button)findViewById(R.id.event_reg_button);
        eventRegRecyclerView = findViewById(R.id.event_reg_recycler_view);
        noEvents = findViewById(R.id.no_reg_events);
        eventRegHeader = findViewById(R.id.event_reg_header);

        loadProfile();
        loadRegisteredEvents();

        logoutButton.setOnClickListener(view -> new AlertDialog.Builder(ProfileActivity.this)
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this).edit();
                    editor.remove("loggedIn");
                    editor.remove("session_cookie");
                    editor.remove("cloudflare_cookie");
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }).setNegativeButton(android.R.string.no, (dialogInterface, i) -> {
                }).show());


    }

    private void loadProfile() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Profile... please wait!");
        dialog.setCancelable(false);

        if (!NetworkUtils.isInternetConnected(this)) {
            noConnectionAlert("Please connect to the internet and try again!");
        } else {
            dialog.show();
            String cookie = RegistrationClient.generateCookie(this);
            Call<ProfileResponse> call = RegistrationClient.getRegistrationInterface(this)
                    .getProfileDetails(cookie);
            Log.i("ProfileActivity", "loadProfile: " + call.request().headers().toString());
            call.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    Log.d("ProfileActivity", "onResponse: ");
                    dialog.dismiss();
                    if (response != null && response.body() != null) {
                        ProfileResponse profileResponse = response.body();
                        Log.d("ProfileActivity", "onResponse: " + profileResponse.getSuccess());
                        if (profileResponse.getSuccess() && profileResponse.getProfileResponseData() != null) {
                            profileCard.setVisibility(View.VISIBLE);
                            name.setText(profileResponse.getProfileResponseData().getName());
                            delID.setText(profileResponse.getProfileResponseData().getId());
                            regno.setText(profileResponse.getProfileResponseData().getRegno());
                            phone.setText(profileResponse.getProfileResponseData().getMobile());
                            email.setText(profileResponse.getProfileResponseData().getEmail());
                            college.setText(profileResponse.getProfileResponseData().getCollege());

                            Bitmap myBitmap = QRCode.from(profileResponse.getProfileResponseData()
                                    .getQr()).withSize(1000, 1000).bitmap();
                            qrCode.setImageBitmap(myBitmap);
                        } else {
                            new AlertDialog.Builder(ProfileActivity.this)
                                    .setMessage("Session expired. Login again to continue!")
                                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this).edit();
                                        editor.remove("loggedIn");
                                        editor.remove("session_cookie");
                                        editor.remove("cloudflare_cookie");
                                        editor.apply();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }).setCancelable(false).show();
                        }
                    } else {
                        noConnectionAlert("Could not connect to server! Please check your internet connect or try again later.");
                        Log.d("Profile", "onFailure: response == null || response.body() == null)");
                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {
                    Log.d("reg", "fail");
                    dialog.dismiss();
                    noConnectionAlert("Could not connect to server! Please check your internet connect or try again later.");
                    Log.d("Profile", "onFailure: " + t.getMessage());
                }
            });
        }
    }

    private void loadRegisteredEvents() {
        if (!NetworkUtils.isInternetConnected(this)) {
            noConnectionAlert("Please connect to the internet and try again!");
        } else {
            try{
                String cookie = RegistrationClient.generateCookie(this);
                Call<RegisteredEventListModel> call = RegistrationClient
                        .getRegistrationInterface(this).getRegisteredEvents(cookie);
                Log.i("ProfileActivity", "loadRegisteredEvents: " + call.request().headers().toString());
                call.enqueue(new Callback<RegisteredEventListModel>() {
                    @Override
                    public void onResponse(Call<RegisteredEventListModel> call, Response<RegisteredEventListModel> response) {
                        Log.d(TAG, "onResponse : " + response.body().getData());
                        if(!response.body().getData().isEmpty()) {
                            regEventsList = response.body().getData();
                            Log.d(TAG, "onResponse: registered events list size"+ regEventsList.size());
                            List<EventDetailsModel> events=mDatabase.copyFromRealm(mDatabase.where(EventDetailsModel.class).findAll());
                            registeredEventsAdapter = new RegisteredEventsAdapter(regEventsList, events,ProfileActivity.this,ProfileActivity.this);
                            eventRegRecyclerView.setAdapter(registeredEventsAdapter);
                            eventRegRecyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                            eventRegRecyclerView.setVisibility(View.VISIBLE);
                            eventRegHeader.setVisibility(View.VISIBLE);
                        }else{
                            noEvents.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisteredEventListModel> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        noEvents.setVisibility(View.VISIBLE);
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void onClick(final Boolean isAdd,RegisteredEventModel regEvent){
        if(isAdd){
            Intent intent = new Intent(ProfileActivity.this, AddTeammateActivity.class);
                intent.putExtra("eventID", regEvent.getEvent());
                startActivityForResult(intent, ADD_TEAM_MEMBER);
        }else{
            leaveTeamAPICall(regEvent.getTeamid());
        }
    }

    private void leaveTeamAPICall(int teamID){
        Call<CreateLeaveTeamResponse> call = RegistrationClient.getRegistrationInterface(ProfileActivity.this).leaveTeam(RegistrationClient.generateCookie(ProfileActivity.this), teamID);
        call.enqueue(new Callback<CreateLeaveTeamResponse>() {
            @Override
            public void onResponse(Call<CreateLeaveTeamResponse> call, Response<CreateLeaveTeamResponse> response) {
                if(response!=null && response.body()!=null){
                    if(response.body().getSuccess())
                        showAlert(response.body().getMsg(),true);
                    else
                        showAlert(response.body().getMsg(),false);
                }
            }

            @Override
            public void onFailure(Call<CreateLeaveTeamResponse> call, Throwable t) {
                    showAlert("Error connecting to server", false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ADD_TEAM_MEMBER && data != null) {
            if (data.getBooleanExtra("success", false)) {

            }
        }
    }

    public void noConnectionAlert(String message) {
        new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    public void showAlert(String message, boolean refresh) {
        new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Alert")
                .setIcon(R.drawable.ic_info)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(refresh)
                            loadRegisteredEvents();
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dialog != null && !dialog.isShowing()) {
            loadProfile();
            loadRegisteredEvents();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
        mDatabase = null;
    }
}
