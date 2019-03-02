package in.mitrev.revels19.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;

import in.mitrev.revels19.R;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextInputEditText nameSignUp, phoneSignUp, registrationSignUp, emailSignUp, referralSignUp;
    Spinner collegeSelector;
    String collegeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }
    public void requestSignUp(View v){


    }
    private void initViews(){
        nameSignUp=findViewById(R.id.name_sign_up);
        phoneSignUp=findViewById(R.id.phone_sign_up);
        registrationSignUp=findViewById(R.id.registration_sign_up);
        emailSignUp=findViewById(R.id.email_sign_up);
        referralSignUp=findViewById(R.id.referral_sign_up);
        collegeSelector=findViewById(R.id.college_sign_up);

        //Spinner code here
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.college_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collegeSelector.setAdapter(adapter);
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        collegeSelected=parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        collegeSelected="Manipal Institute of Technology";
    }
}
