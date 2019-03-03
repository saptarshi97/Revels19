package in.mitrev.revels19.activities;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import in.mitrev.revels19.R;
import in.mitrev.revels19.models.registration.ProfileResponse;
import in.mitrev.revels19.models.registration.RegisteredEventListModel;
import in.mitrev.revels19.network.RegistrationClient;
import in.mitrev.revels19.utilities.NetworkUtils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private TextView name;
    private TextView delID;
    private TextView regno;
    private TextView college;
    private TextView phone;
    private TextView email;
    private LinearLayout profileCard;
    private Button logoutButton;
    //    private Button eventRegButton;
    private ImageView qrCode;
    //    private RecyclerView eventRegRecyclerView;
//    private TextView noEvents;
//    private LinearLayout eventRegHeader;
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
//        eventRegRecyclerView = (RecyclerView)findViewById(R.id.event_reg_recycler_view);
//        noEvents = (TextView)findViewById(R.id.no_reg_events);
//        eventRegHeader = (LinearLayout)findViewById(R.id.event_reg_header);

        loadProfile();
        loadRegisteredEvents();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this).edit();
                                editor.remove("loggedIn");
                                editor.remove("session_cookie");
                                editor.remove("cloudflare_cookie");
                                editor.apply();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
            }
        });


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
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this).edit();
                                            editor.remove("loggedIn");
                                            editor.remove("session_cookie");
                                            editor.remove("cloudflare_cookie");
                                            editor.apply();
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
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
            String cookie = RegistrationClient.generateCookie(this);
            Call<RegisteredEventListModel> call = RegistrationClient
                    .getRegistrationInterface(this).getRegisteredEvents(cookie);
            Log.i("ProfileActivity", "loadRegisteredEvents: " + call.request().headers().toString());
            call.enqueue(new Callback<RegisteredEventListModel>() {
                @Override
                public void onResponse(Call<RegisteredEventListModel> call, Response<RegisteredEventListModel> response) {
                    Log.d(TAG, "onResponse : " + response.body().getData());
                }

                @Override
                public void onFailure(Call<RegisteredEventListModel> call, Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                }
            });
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
        if (dialog != null && !dialog.isShowing())
            loadProfile();
        loadRegisteredEvents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDatabase.close();
        mDatabase = null;
    }
}
