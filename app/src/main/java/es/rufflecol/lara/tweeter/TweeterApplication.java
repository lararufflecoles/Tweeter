package es.rufflecol.lara.tweeter;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;

public class TweeterApplication extends Application {

    private static final String TWITTER_KEY = "2InB5cnoHD5JDXY5aisltp27R";
    private static final String TWITTER_SECRET = "Azs5lXnXsvn5NZSV59jqCY2NYQbFVf4d2CiEHzxRyjMRly69yl";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new TweetComposer(), new Crashlytics());
    }
}