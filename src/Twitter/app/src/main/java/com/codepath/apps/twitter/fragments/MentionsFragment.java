package com.codepath.apps.twitter.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.adapters.TweetAdapter;
import com.codepath.apps.twitter.databinding.FragmentMentionsBinding;
import com.codepath.apps.twitter.databinding.FragmentTimelineBinding;
import com.codepath.apps.twitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.providers.DataProvider;
import com.codepath.apps.twitter.utils.CommonUtils;
import com.codepath.apps.twitter.utils.NetworkUtils;
import com.codepath.apps.twitter.utils.TwitterApp;
import com.codepath.apps.twitter.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MentionsFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private static final String TAG = "TwitterClient";
    private static final int ROTATION = 360;

    private TwitterClient client;
    private ArrayList<Tweet> mTweets;
    private TweetAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView rvTweets;
    private FragmentMentionsBinding mBinding;
    FloatingActionButton btnCreatePost;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    long mMaxId = 0;
    private int mPage;

    public MentionsFragment() {
        // Required empty public constructor
    }


    public static MentionsFragment newInstance(int page) {
        MentionsFragment fragment = new MentionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMentionsBinding.inflate(inflater, container, false);
        initialize();
        populateTimeline();
        return mBinding.getRoot();
    }

    private void initialize() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Helvetica Neu Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        client = TwitterApp.getRestClient();
        rvTweets = mBinding.rvTweet;
        mTweets = new ArrayList<>();

        mAdapter = new TweetAdapter(getContext(), client, mTweets, getFragmentManager());
        mLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setAdapter(mAdapter);
        rvTweets.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(dividerItemDecoration);

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline();
            }
        };
        rvTweets.addOnScrollListener(scrollListener);

        btnCreatePost = mBinding.btnCreatePost;

        btnCreatePost.setOnClickListener(v -> {
            btnCreatePost.animate().rotation(ROTATION);
            //showCreateDialog(null);
        });

        swipeContainer = mBinding.swipeContainer;
        swipeContainer.setOnRefreshListener(() -> {
            resetSearch();
            populateTimeline();
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    private void populateTimeline() {
        if (NetworkUtils.isNetworkAvailable(getActivity()))
            client.getMentionsTimeline(mMaxId - 1, new JsonHttpResponseHandler() {
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
                    swipeContainer.setRefreshing(false);
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    swipeContainer.setRefreshing(false);
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    swipeContainer.setRefreshing(false);
                    throwable.printStackTrace();
                }
            });
        else {
            CommonUtils.showMessage(mBinding.getRoot(), "Unable to refresh data. No network connection available.");
            populateDataFromDb();
        }
    }

    private void refreshDataAndUI(Tweet tweet) {
        Log.d(TAG, tweet.body);
        mTweets.add(tweet);
        mAdapter.notifyItemInserted(mTweets.size() - 1);
        swipeContainer.setRefreshing(false);
        setMaxId(tweet.uuid);
        updateDatabase(tweet);
    }

    private void resetSearch() {
        mTweets.clear();
        mAdapter.notifyDataSetChanged();
        mMaxId = 0;
        DataProvider provider = new DataProvider();
        provider.deleteAll();
    }

    private void populateDataFromDb() {
        DataProvider provider = new DataProvider();
        ArrayList<Tweet> tweets = provider.readTweets();

        for (Tweet tweet : tweets) {
            refreshDataAndUI(tweet);
        }
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

    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
