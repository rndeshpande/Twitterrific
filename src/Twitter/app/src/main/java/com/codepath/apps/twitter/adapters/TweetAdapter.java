package com.codepath.apps.twitter.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.activities.DetailsActivity;
import com.codepath.apps.twitter.activities.TimelineActivity;
import com.codepath.apps.twitter.databinding.ListTweetBinding;
import com.codepath.apps.twitter.fragments.CreateDialogFragment;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TweetRequest;
import com.codepath.apps.twitter.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.sdk.android.core.Twitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by rdeshpan on 9/25/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>  {
    private ArrayList<Tweet> mTweets;
    private Context mContext;
    private TwitterClient mClient;
    private FragmentManager mFragmentManager;

    public TweetAdapter(Context context, TwitterClient client, ArrayList<Tweet> tweets, FragmentManager fragmentManager) {
        mContext = context;
        mTweets = tweets;
        mClient = client;
        mFragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        final ViewHolder viewHolder;
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;

        itemView = inflater.inflate(R.layout.list_tweet, parent, false);
        viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Tweet tweet = mTweets.get(position);
        viewHolder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ListTweetBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = ListTweetBinding.bind(itemView);
            itemView.setOnClickListener(this);
            binding.tvComment.setOnClickListener(v-> {
                onCommentClick(binding.tvComment);
            });
            binding.tvRetweet.setOnClickListener(v-> {
                retweet(binding.tvRetweet);
            });
            binding.tvFavorite.setOnClickListener(v-> {
                favorite(binding.tvFavorite);
            });
        }

        public void bind(Tweet tweet) {
            binding.setTweet(tweet);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = mTweets.get(position);
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra("tweet_details", Parcels.wrap(Tweet.class, tweet));
                mContext.startActivity(intent);
            }
        }

        public void onCommentClick(View view) {
            TweetRequest tweetRequest = new TweetRequest("", binding.getTweet().getUuid(), binding.getTweet().getUser().getScreenName());
            CreateDialogFragment dialogFragment = CreateDialogFragment.newInstance(Parcels.wrap(tweetRequest));
            dialogFragment.show(mFragmentManager, "fragment_create_dialog");
        }

        public void retweet(View view) {
            Tweet tweet = mTweets.get(getAdapterPosition());
            postRetweet(tweet.getUuid(), tweet.retweeted, getAdapterPosition());
        }

        public void favorite(View view) {
            Tweet tweet = mTweets.get(getAdapterPosition());
            postFavorite(tweet.getUuid(), tweet.favorited, getAdapterPosition());
        }

        private void postRetweet(long tweetId, Boolean retweeted, int position) {
            Log.d("TWITTERCLIENT", "Retweeted before: " +  String.valueOf(retweeted));
            mClient.postReTweet(tweetId, !retweeted, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    Tweet tweet = new Tweet();
                    try {
                        tweet = Tweet.fromJSON(response);
                        Log.d("TWITTERCLIENT", "Retweeted: " +  tweet.retweeted);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mTweets.set(position, tweet);
                    notifyItemChanged(position);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    throwable.printStackTrace();
                }
            });
        }

        private void postFavorite(long tweetId, Boolean favorited, int position) {
            mClient.postFavorite(tweetId, !favorited, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("TWITTERCLIENT", "favorite success");
                    Tweet tweet = new Tweet();
                    try {
                        tweet = Tweet.fromJSON(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mTweets.set(position, tweet);
                    notifyItemChanged(position);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    throwable.printStackTrace();
                }
            });
        }
    }
}
