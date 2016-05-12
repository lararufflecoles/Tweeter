package es.rufflecol.lara.tweeter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.TweetInteractionListener {

    private FavoriteService favoriteService;
    private List<Tweet> tweetList;
    private RecyclerAdapter recyclerAdapter;
    private StatusesService statusesService;
    private TwitterApiClient twitterApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.Toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(recyclerAdapter);

        refreshTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_compose_tweet:
                composeTweet(null, null);
                return true;
            case R.id.action_refresh_timeline:
                refreshTimeline();
                return true;
            case R.id.action_about:
                openAbout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void composeTweet(final Long inReplyToStatusId, String inReplyToScreenName) {
        LayoutInflater inflater = getLayoutInflater();
        View composeTweetLayout = inflater.inflate(R.layout.tweet_compose, null);

        final EditText composeTweetEditText = (EditText) composeTweetLayout.findViewById(R.id.tweet_compose);

        if (inReplyToScreenName != null) {
            composeTweetEditText.setText("@" + inReplyToScreenName + " ");
            composeTweetEditText.setSelection(composeTweetEditText.getText().length());
        }

        AlertDialog.Builder composeTweetAlertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        composeTweetAlertDialogBuilder
                .setView(composeTweetLayout)
                .setCancelable(true)
                .setNegativeButton(R.string.tweet_compose_back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.tweet_compose_tweet, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String composedTweetAsString = composeTweetEditText.getText().toString();
                        addComposedTweetToTimeline(composedTweetAsString, inReplyToStatusId);
                    }
                });
        AlertDialog composeTweetAlertDialog = composeTweetAlertDialogBuilder.create();
        composeTweetAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); // Pops up the keyboard
        composeTweetAlertDialog.show();

        int twitterBlue = ContextCompat.getColor(this, R.color.twitter_blue);
        composeTweetAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(twitterBlue);
        composeTweetAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(twitterBlue);
    }

    @Override
    public void composeTweetReply(Tweet tweet) {
        long statusId = tweet.getId();
        String screenName = tweet.user.screenName;
        composeTweet(statusId, screenName);
    }

    private void addComposedTweetToTimeline(String composedTweetAsString, Long inReplyToStatusId) {
        twitterApiClient = Twitter.getApiClient();
        statusesService = twitterApiClient.getStatusesService();
        statusesService.update(composedTweetAsString, inReplyToStatusId, null, null, null, null, null, null, null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                refreshTimeline();
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, R.string.tweet_posting_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void refreshTimeline() {
        twitterApiClient = Twitter.getApiClient();
        statusesService = twitterApiClient.getStatusesService();
        statusesService.homeTimeline(100, null, null, null, null, null, null, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                tweetList = result.data;
                recyclerAdapter.setTweets(tweetList);
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, R.string.refresh_timeline_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openAbout() {
        Intent openAbout = new Intent(this, AboutActivity.class);
        startActivity(openAbout);
    }

    @Override
    public void onCreateFavoriteTweet(Tweet tweet) {
        twitterApiClient = Twitter.getApiClient();
        favoriteService = twitterApiClient.getFavoriteService();
        favoriteService.create(tweet.getId(), null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                refreshTimeline();
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, R.string.on_create_favorite_tweet_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyFavoriteTweet(Tweet tweet) {
        twitterApiClient = Twitter.getApiClient();
        favoriteService = twitterApiClient.getFavoriteService();
        favoriteService.destroy(tweet.getId(), null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                refreshTimeline();
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, R.string.on_destroy_favorite_tweet_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRetweet(Tweet tweet) {
        twitterApiClient = Twitter.getApiClient();
        statusesService = twitterApiClient.getStatusesService();
        statusesService.retweet(tweet.getId(), null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                refreshTimeline();
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, R.string.on_retweet_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onUnretweet(Tweet tweet) {
        twitterApiClient = Twitter.getApiClient();
        statusesService = twitterApiClient.getStatusesService();
        statusesService.unretweet(tweet.getId(), null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                refreshTimeline();
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, R.string.on_unretweet_failed, Toast.LENGTH_LONG).show();
            }
        });
    }
}