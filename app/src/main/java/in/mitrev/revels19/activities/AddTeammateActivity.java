package in.mitrev.revels19.activities;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import in.mitrev.revels19.R;
import in.mitrev.revels19.models.registration.CreateLeaveTeamResponse;
import in.mitrev.revels19.network.RegistrationClient;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.zxing.Result;

public class AddTeammateActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 316;
    private ZXingScannerView scannerView;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teammate);
        setTitle(R.string.add_team_member);

        Intent intent=getIntent();
        //Check event id type, its supposed to be int
        eventID=intent.getStringExtra("eventID");

        if (getSupportActionBar() != null) getSupportActionBar().setSubtitle(R.string.scan_qr_code_2);

        scannerView = (ZXingScannerView)findViewById(R.id.event_scanner);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scannerView.setResultHandler(this);
    }

    @Override
    public void handleResult(Result result) {
        if (result.getText().isEmpty()) return;

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Adding member... please wait!");
        dialog.setCancelable(false);
        dialog.show();
        String cookie = RegistrationClient.generateCookie(this);
        Call<CreateLeaveTeamResponse> call=RegistrationClient.getRegistrationInterface(this).addMember(cookie,result.getText(),eventID);
        call.enqueue(new Callback<CreateLeaveTeamResponse>() {
            @Override
            public void onResponse(Call<CreateLeaveTeamResponse> call, Response<CreateLeaveTeamResponse> response) {
                dialog.dismiss();
                if(response!=null && response.body()!=null){
                    showAlert(response.body().getMsg());
                }else {
                    noConnectionAlert();
                }
            }

            @Override
            public void onFailure(Call<CreateLeaveTeamResponse> call, Throwable t) {
                dialog.dismiss();
                noConnectionAlert();
            }
        });
    }


    public void noConnectionAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setMessage("Could not connect to server! Please check your internet connect or try again later.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }
    public void showAlert(String message) {
        new AlertDialog.Builder(AddTeammateActivity.this)
                .setTitle("Information")
                .setIcon(R.drawable.ic_info)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_reg, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
            case R.id.event_reg_flash:
                scannerView.toggleFlash();
                break;
            case R.id.event_reg_reload:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
                scannerView.stopCamera();
                scannerView.startCamera();
                scannerView.setResultHandler(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
