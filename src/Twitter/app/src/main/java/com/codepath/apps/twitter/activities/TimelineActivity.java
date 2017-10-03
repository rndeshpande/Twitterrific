package com.codepath.apps.twitter.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.utils.TwitterApp;
import com.codepath.apps.twitter.utils.TwitterClient;
import com.codepath.apps.twitter.adapters.TweetAdapter;
import com.codepath.apps.twitter.databinding.ActivityTimelineBinding;
import com.codepath.apps.twitter.fragments.CreateDialogFragment;
import com.codepath.apps.twitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TweetRequest;
import com.codepath.apps.twitter.providers.DataProvider;
import com.codepath.apps.twitter.utils.CommonUtils;
import com.codepath.apps.twitter.utils.NetworkUtils;
import com.codepath.apps.twitter.utils.TestDataHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TimelineActivity extends AppCompatActivity implements CreateDialogFragment.OnFragmentInteractionListener {

    private TwitterClient client;
    private ArrayList<Tweet> mTweets;
    private TweetAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView rvTweets;
    private ActivityTimelineBinding mBinding;
    FloatingActionButton btnCreatePost;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    long mMaxId = 0;
    Button btnLogout;

    private static final String TAG = "TwitterClient";
    private static final int ROTATION = 360;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkSendIntent();
        initialize();
        populateTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;
        }
        return true;
    }

    private void checkSendIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                // Make sure to check whether returned data will be null.
                String titleOfPage = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);
                Uri imageUriOfPage = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                TweetRequest tweetRequest = new TweetRequest();
                String status = titleOfPage + " " + urlOfPage;
                tweetRequest.setStatus(status);

                showCreateDialog(tweetRequest);
            }
        }
    }

    private void initialize() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        setToolbar();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Helvetica Neu Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        client = TwitterApp.getRestClient();
        rvTweets = mBinding.rvTweet;
        mTweets = new ArrayList<>();

        mAdapter = new TweetAdapter(this, client, mTweets, getSupportFragmentManager());
        mLayoutManager = new LinearLayoutManager(this);
        rvTweets.setAdapter(mAdapter);
        rvTweets.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
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
            showCreateDialog(null);
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

    private void logout() {
        client.clearAccessToken();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void setToolbar() {
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void showCreateDialog(TweetRequest tweetRequest) {
        CreateDialogFragment dialogFragment;

        if (tweetRequest != null)
            dialogFragment = CreateDialogFragment.newInstance(Parcels.wrap(tweetRequest));
        else
            dialogFragment = CreateDialogFragment.newInstance();

        dialogFragment.show(TimelineActivity.this.getSupportFragmentManager(), "fragment_create_dialog");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void populateTimeline() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            client.getHomeTimeline(mMaxId - 1, new JsonHttpResponseHandler() {
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
        } else {
            CommonUtils.showMessage(mBinding.clMain, "Unable to refresh data. No network connection available.");
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

    // TODO : move this to a separate class
    private void postTweet(TweetRequest tweetRequest) {
        client.postTweet(tweetRequest, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = new Tweet();
                try {
                    tweet = Tweet.fromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mTweets.add(0, tweet);
                mAdapter.notifyItemInserted(0);
                rvTweets.smoothScrollToPosition(0);
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



    @Override
    public void onFragmentInteraction(TweetRequest tweetRequest) {
        postTweet(tweetRequest);
        CommonUtils.showMessage(mBinding.clMain, getString(R.string.tweet_posted));
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
}
