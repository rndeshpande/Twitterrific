package com.codepath.apps.twitter.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.adapters.TweetAdapter;
import com.codepath.apps.twitter.databinding.FragmentUserTimelineBinding;
import com.codepath.apps.twitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.User;
import com.codepath.apps.twitter.utils.CommonUtils;
import com.codepath.apps.twitter.utils.NetworkUtils;
import com.codepath.apps.twitter.utils.TwitterApp;
import com.codepath.apps.twitter.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by rdeshpan on 10/5/2017.
 */

public class UserTimelineFragment extends Fragment {

    private TwitterClient mClient;
    private ArrayList<Tweet> mTweets;
    private TweetAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView rvTweets;
    private FragmentUserTimelineBinding mBinding;
    EndlessRecyclerViewScrollListener scrollListener;
    long mMaxId = 0;
    private User mUser;

    private static final String TAG = "TwitterClient";


    public UserTimelineFragment() {
    }

    public static UserTimelineFragment newInstance(User user) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = Parcels.unwrap(getArguments().getParcelable("user"));
        }
        mClient = TwitterApp.getRestClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_timeline, container, false);
        initialize();
        populateTimeline();
        return mBinding.getRoot();
    }

    private void initialize() {
        rvTweets = mBinding.rvTweets;
        mTweets = new ArrayList<>();
        mAdapter = new TweetAdapter(getContext(), mClient, mTweets, getFragmentManager());
        mLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setAdapter(mAdapter);
        rvTweets.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(dividerItemDecoration);
    }

    private void populateTimeline() {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            mClient.getUserTimeline(mMaxId - 1,mUser.getUuid(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {

                    for (int i = 0; i < responseArray.length(); i++) {
                        Tweet tweet = null;
                        try {
                            tweet = Tweet.fromJSON(responseArray.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        refreshDataAndUI(tweet);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    //swipeContainer.setRefreshing(false);
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    //swipeContainer.setRefreshing(false);
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    //swipeContainer.setRefreshing(false);
                    throwable.printStackTrace();
                }
            });
        } else {
            // TODO : Enable this code or refactor
            //CommonUtils.showMessage(mBinding.clMain, "Unable to refresh data. No network connection available.");
            //populateDataFromDb();
        }
    }

    private void refreshDataAndUI(Tweet tweet) {
        mTweets.add(tweet);
        mAdapter.notifyItemInserted(mTweets.size() - 1);
        //swipeContainer.setRefreshing(false);
        setMaxId(tweet.uuid);
        updateDatabase(tweet);
    }

    private void updateDatabase(Tweet tweet) {
        tweet.user.save();
        tweet.save();
    }

    private void setMaxId(long maxId) {
        if (mMaxId == 0)
            mMaxId = maxId;
        else
            mMaxId = maxId < mMaxId ? maxId : mMaxId;
    }
}
