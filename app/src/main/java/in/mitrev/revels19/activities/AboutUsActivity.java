package in.mitrev.revels19.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import in.mitrev.revels19.R;

public class AboutUsActivity extends AppCompatActivity {

    private static final String LOG_TAG = AboutUsActivity.class.getSimpleName();
    String URL_SNAPCHAT = "https://www.snapchat.com/add/revelsmit";
    String URL_TWITTER = "http://www.twitter.com/revelsmit";
    String URL_FACEBOOK = "http://www.facebook.com/mitrevels";
    String URL_INSTAGRAM = "http://www.instagram.com/revelsmit";
    ImageView facebookLogo, twitterLogo, instagramLogo, snapchatLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.about_us);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        facebookLogo = findViewById(R.id.logo_facebook);
        twitterLogo = findViewById(R.id.logo_twitter);
        instagramLogo = findViewById(R.id.logo_instagram);
        snapchatLogo = findViewById(R.id.logo_snapchat);

        facebookLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchURL(URL_FACEBOOK);
            }
        });

        twitterLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchURL(URL_TWITTER);
            }
        });

        instagramLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchURL(URL_INSTAGRAM);
            }
        });

        snapchatLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchURL(URL_SNAPCHAT);
            }
        });
    }

    private void launchURL(String url) {
        try {
            Intent socialIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(socialIntent);
        } catch (ActivityNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage() + "\nPerhaps user does not have a browser installed");
        }
    }
}
