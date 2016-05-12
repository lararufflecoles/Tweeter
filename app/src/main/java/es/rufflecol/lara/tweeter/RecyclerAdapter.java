package es.rufflecol.lara.tweeter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Tweet;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public interface TweetInteractionListener {
        void composeTweetReply(Tweet tweet);
        void onRetweet(Tweet tweet);
        void onUnretweet(Tweet tweet);
        void onCreateFavoriteTweet(Tweet tweet);
        void onDestroyFavoriteTweet(Tweet tweet);
    }

    private List<Tweet> tweets = new ArrayList<>();
    private TweetInteractionListener tweetInteractionListener;

    public RecyclerAdapter(TweetInteractionListener tweetInteractionListener) {
        this.tweetInteractionListener = tweetInteractionListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);

        Picasso.with(holder.itemView.getContext()).load(tweet.user.profileImageUrl).into(holder.userProfileImageUrl);
        holder.userProfileImageUrl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Context context = v.getContext();
                Intent goToUserTimeline = new Intent(context, UserTimeline.class);
                context.startActivity(goToUserTimeline);
            }
        });

        holder.userName.setText(tweet.user.name);

        holder.useScreenName.setText("@" + tweet.user.screenName);

        Resources resources = holder.itemView.getContext().getResources();
        DateTimeFormatter dateTimeParser = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy");
        DateTime dateTime = dateTimeParser.parseDateTime(tweet.createdAt);
        DateTime now = new DateTime();
        Period period = new Period(dateTime, now);
        if (period.getYears() > 0) {
            holder.createdAt.setText(resources.getString(R.string.period_years, period.getYears()));
        } else if (period.getMonths() > 0) {
            holder.createdAt.setText(resources.getString(R.string.period_months, period.getMonths()));
        } else if (period.getWeeks() > 0) {
            holder.createdAt.setText(resources.getString(R.string.period_weeks, period.getWeeks()));
        } else if (period.getDays() > 0) {
            holder.createdAt.setText(resources.getString(R.string.period_days, period.getDays()));
        } else if (period.getHours() > 0) {
            holder.createdAt.setText(resources.getString(R.string.period_hours, period.getHours()));
        } else if (period.getMinutes() > 0) {
            holder.createdAt.setText(resources.getString(R.string.period_minutes, period.getMinutes()));
        } else if (period.getSeconds() > 0) {
            holder.createdAt.setText(resources.getString(R.string.period_seconds, period.getSeconds()));
        } else {
            holder.createdAt.setText(R.string.period_millis);
        }

        holder.text.setText(tweet.text);

        holder.tweetReply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tweetInteractionListener.composeTweetReply(tweet);
            }
        });

        holder.retweet.setOnCheckedChangeListener(null);
        holder.retweet.setChecked(tweet.retweeted);
        holder.retweet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tweetInteractionListener.onRetweet(tweet);
                } else {
                    tweetInteractionListener.onUnretweet(tweet);
                }
            }
        });

        holder.favorited.setOnCheckedChangeListener(null);
        holder.favorited.setChecked(tweet.favorited);
        holder.favorited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tweetInteractionListener.onCreateFavoriteTweet(tweet);
                } else {
                    tweetInteractionListener.onDestroyFavoriteTweet(tweet);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void setTweets(List<Tweet> articles) {
        this.tweets.clear();
        if (articles != null) {
            this.tweets.addAll(articles);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView userProfileImageUrl;
        private TextView userName;
        private TextView useScreenName;
        private TextView createdAt;
        private TextView text;
        private Button tweetReply;
        private ToggleButton retweet;
        private ToggleButton favorited;

        public ViewHolder(View itemView) {
            super(itemView);
            userProfileImageUrl = (ImageView) itemView.findViewById(R.id.profile_image_url);
            userName = (TextView) itemView.findViewById(R.id.name);
            useScreenName = (TextView) itemView.findViewById(R.id.screen_name_handle);
            createdAt = (TextView) itemView.findViewById(R.id.created_at);
            text = (TextView) itemView.findViewById(R.id.text);
            tweetReply = (Button) itemView.findViewById(R.id.tweet_reply);
            retweet = (ToggleButton) itemView.findViewById(R.id.retweet);
            favorited = (ToggleButton) itemView.findViewById(R.id.favorited);
        }
    }

    public List<Tweet> getTweets() {
        return tweets;
    }
}