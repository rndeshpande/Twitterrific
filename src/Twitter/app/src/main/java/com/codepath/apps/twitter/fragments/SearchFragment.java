package com.codepath.apps.twitter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codepath.apps.twitter.adapters.TweetAdapter;
import com.codepath.apps.twitter.databinding.FragmentSearchBinding;
import com.codepath.apps.twitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TweetSearchResponse;
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

public class SearchFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ARG_QUERY = "query";
    private static final String TAG = "TwitterClient";
    private static final String DEFAULT_SEARCH = "latest";


    private TwitterClient client;
    private ArrayList<Tweet> mTweets;
    private TweetAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView rvTweets;
    private FragmentSearchBinding mBinding;
    private ProgressBar pbLoading;

    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    long mMaxId = 0;
    private int mPage;
    private String nextResults = "";
    private String mSearchText = "";


    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance(int page) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchFragment newInstance(String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
            mSearchText = getArguments().getString(ARG_QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSearchBinding.inflate(inflater, container, false);
        initialize();
        populateTimeline();
        return mBinding.getRoot();
    }

    private void initialize() {
        client = TwitterApp.getRestClient();
        pbLoading = mBinding.pbLoading;
        pbLoading.setVisibility(ProgressBar.VISIBLE);
        rvTweets = mBinding.rvTweets;
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
                pbLoading.setVisibility(ProgressBar.INVISIBLE);
                populateTimeline();
            }
        };
        rvTweets.addOnScrollListener(scrollListener);

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
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            String query = mSearchText == null || mSearchText.isEmpty() ? DEFAULT_SEARCH : mSearchText;
            client.getTweetsByText(query, nextResults, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        TweetSearchResponse tweetSearchResponse = TweetSearchResponse.fromJSON(response);

                        for (Tweet tweet : tweetSearchResponse.statuses)
                            refreshDataAndUI(tweet);

                        nextResults = tweetSearchResponse.getMetadata().nextResults;
                        pbLoading.setVisibility(ProgressBar.INVISIBLE);

                    } catch (JSONException e) {
                        swipeContainer.setRefreshing(false);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    swipeContainer.setRefreshing(false);
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    swipeContainer.setRefreshing(false);
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    swipeContainer.setRefreshing(false);
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    throwable.printStackTrace();
                }
            });
        } else {
            CommonUtils.showMessage(mBinding.flContainer , "Unable to refresh data. No network connection available.");
            pbLoading.setVisibility(ProgressBar.INVISIBLE);
            populateDataFromDb();
        }
    }

    private void refreshDataAndUI(Tweet tweet) {
        mTweets.add(tweet);
        mAdapter.notifyItemInserted(mTweets.size() - 1);
        swipeContainer.setRefreshing(false);
        setMaxId(tweet.uuid);
        updateDatabase(tweet);
    }

    private void resetSearch() {
        if(mTweets != null) {
            mTweets.clear();
            mAdapter.notifyDataSetChanged();
            DataProvider provider = new DataProvider();
            provider.deleteAll();
        }

        mMaxId = 0;
        nextResults = "";
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

    public void setSearchText(String text) {
        pbLoading.setVisibility(ProgressBar.VISIBLE);
        mSearchText = text;
        resetSearch();
        populateTimeline();
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
