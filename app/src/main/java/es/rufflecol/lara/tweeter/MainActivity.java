package es.rufflecol.lara.tweeter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private RecyclerAdapter adapter;
    private List<Tweet> tweetList;
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

        adapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(adapter);

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
            case R.id.action_refresh_timeline:
                refreshTimeline();
                return true;
            case R.id.action_about:
                openAbout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshTimeline() {
        twitterApiClient = Twitter.getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();
        statusesService.homeTimeline(100, null, null, null, null, null, null, new Callback<List<Tweet>>() {

            @Override
            public void success(Result<List<Tweet>> result) {
                tweetList = result.data;
                adapter.setTweets(tweetList);
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, R.string.refresh_timeline_failed, Toast.LENGTH_LONG).show();
            }
        });
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

    private void openAbout() {
        Intent openAbout = new Intent(this, AboutActivity.class);
        startActivity(openAbout);
    }
}