package es.rufflecol.lara.tweeter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Tweet> tweets = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);

        Picasso.with(holder.itemView.getContext())
                .load(tweet.user.profileImageUrl)
                .into(holder.profileImageUrl);

        holder.name.setText(tweet.user.name);
        holder.screenName.setText("@" + tweet.user.screenName);
        holder.createdAt.setText(tweet.createdAt);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageUrl;
        TextView name;
        TextView screenName;
        TextView createdAt;
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImageUrl = (ImageView) itemView.findViewById(R.id.profile_image_url);
            name = (TextView) itemView.findViewById(R.id.name);
            screenName = (TextView) itemView.findViewById(R.id.screen_name_handle);
            createdAt = (TextView) itemView.findViewById(R.id.created_at);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }
}