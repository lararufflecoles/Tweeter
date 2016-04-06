package es.rufflecol.lara.tweeter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private static final String TWITTER_KEY = "2InB5cnoHD5JDXY5aisltp27R";
    private static final String TWITTER_SECRET = "Azs5lXnXsvn5NZSV59jqCY2NYQbFVf4d2CiEHzxRyjMRly69yl";

    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.Toolbar);
        setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        if (loginButton != null) { // Android Studio suggested the null check be added
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success (Result < TwitterSession > result) {
                    TwitterSession session = result.data;
                    String message = "@" + session.getUserName() + " logged in (#" + session.getUserId() + ")";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }

                @Override
                public void failure (TwitterException exception){
                    Log.d("TwitterKit", "Login with Twitter failure", exception);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}