package es.rufflecol.lara.tweeter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Tweet;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Tweet> tweets = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);

        Picasso.with(holder.itemView.getContext()).load(tweet.user.profileImageUrl).into(holder.profileImageUrl);
        holder.name.setText(tweet.user.name);
        holder.screenName.setText("@" + tweet.user.screenName);

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
        private ImageView profileImageUrl;
        private TextView name;
        private TextView screenName;
        private TextView createdAt;
        private TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImageUrl = (ImageView) itemView.findViewById(R.id.profile_image_url);
            name = (TextView) itemView.findViewById(R.id.name);
            screenName = (TextView) itemView.findViewById(R.id.screen_name_handle);
            createdAt = (TextView) itemView.findViewById(R.id.created_at);
            text = (TextView) itemView.findViewById(R.id.text);
        }

    }

    public List<Tweet> getTweets() {
        return tweets;
    }
}