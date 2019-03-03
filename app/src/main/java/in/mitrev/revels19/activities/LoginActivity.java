package in.mitrev.revels19.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import in.mitrev.revels19.R;
import in.mitrev.revels19.models.registration.LoginResponse;
import in.mitrev.revels19.network.RegistrationClient;
import in.mitrev.revels19.utilities.NetworkUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText emailEditText, passwordEditText;
    LinearLayout contentLayout;
    String email, password;
    View loadingSpiner;
    String CAPTCHA_KEY = "6Lfho3IUAAAAAEu6JHZojPoo55KE885x5LJVIIfN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loadingSpiner = findViewById(R.id.loading_spinner);
        contentLayout=findViewById(R.id.login_content);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (sp.getBoolean("loggedIn", false)) {
            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(intent);
        } else {
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    public void loginInit(View v) {
        if (emailEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()) {
            showAlert("Email and password fields cannot be empty");
        } else {
            email = emailEditText.getText().toString();
            password = passwordEditText.getText().toString();
            if (!NetworkUtils.isInternetConnected(this)) {
                showAlert("Please connect to the internet and try again!");
                return;
            }
            SafetyNet.getClient(this).verifyWithRecaptcha(CAPTCHA_KEY)
                    .addOnSuccessListener( this,
                            new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                                @Override
                                public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                    // Indicates communication with reCAPTCHA service was successful.
                                    String userResponseToken=response.getTokenResult();
                                    Log.d("Login Activity", "onSuccess: uRT"+userResponseToken);
                                    if (!userResponseToken.isEmpty()) {
                                        // Validate the user response token using the
                                        // reCAPTCHA siteverify API.
                                        performLogin(userResponseToken);
                                    }
                                }
                            })
                    .addOnFailureListener( this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof ApiException) {
                                ApiException apiException = (ApiException) e;
                                int statusCode = apiException.getStatusCode();
                                Log.d("reCaptcha", "GCError: StatusCode " + statusCode);
                            } else {
                                // A unknown type of error occurred.
                                Log.d("reCaptcha", "GCError: " + e.getMessage());
                            }
                        }
                    });
        }
    }

    private void performLogin(String urt){
        Log.d("LoginActivity", "performLogin: Here");
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), "email=" + email + "&password=" + password + "&type=android" + "&g-recaptcha-response=" + urt);
        Call<LoginResponse> call = RegistrationClient.getRegistrationInterface(LoginActivity.this).attemptLogin(body);
        loadingSpiner.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loadingSpiner.setVisibility(View.GONE);
                String message = "";
                int error = 0;
                Log.d("LoginActivity", "onResponse: " + response.body());
                if (response != null && response.body() != null) {
                    if (response.body().getStatus()) {
                        message = "Login successful!";
                        login();
                    } else {
                        message = "Login unsuccessful";
                    }
                    showAlert(message);
                } else {
                    showAlert("Could not connect to server! Please check your internet connect or try again later.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showAlert("Could not connect to server! Please check your internet connect or try again later.");
            }
        });
    }

    public void goToSignup(View v){
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }

    public void guestContinue(View v){
        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void login() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
        editor.putBoolean("loggedIn", true);
        editor.apply();
        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    public void showAlert(String message) {
        loadingSpiner.setVisibility(View.GONE);
        new AlertDialog.Builder(LoginActivity.this).setTitle("Alert").setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setCancelable(true).show();
    }
}
