package com.codepath.apps.twitter.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.adapters.TweetAdapter;
import com.codepath.apps.twitter.adapters.UserAdapter;
import com.codepath.apps.twitter.databinding.ActivityFollowBinding;
import com.codepath.apps.twitter.databinding.ActivitySearchBinding;
import com.codepath.apps.twitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.User;
import com.codepath.apps.twitter.models.UserFollowRequest;
import com.codepath.apps.twitter.providers.DataProvider;
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

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FollowActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FrameLayout flMain;
    private ActivityFollowBinding mBinding;
    private String mType = "";
    private long mCursor = -1;
    private User mUser;
    private TwitterClient mClient;
    private ArrayList<User> mUsers;
    private UserAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView rvUsers;

    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    long mMaxId = 0;

    private static final String FOLLOWING = "Following";
    private static final String FOLLOWER = "Followers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_follow);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Helvetica Neu Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        initialize();
    }

    private void initialize() {
        mType = getIntent().getStringExtra("type");
        mUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        mClient = TwitterApp.getRestClient();
        setupToolbar();

        rvUsers = mBinding.rvUsers;
        mUsers = new ArrayList<>();

        mAdapter = new UserAdapter(getContext(), mClient, mUsers, getSupportFragmentManager());
        mLayoutManager = new LinearLayoutManager(getContext());
        rvUsers.setAdapter(mAdapter);
        rvUsers.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvUsers.addItemDecoration(dividerItemDecoration);

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateList();
            }
        };
        rvUsers.addOnScrollListener(scrollListener);

        swipeContainer = mBinding.swipeContainer;
        swipeContainer.setOnRefreshListener(() -> {
            resetSearch();
            populateList();
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateList();

    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(mType);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void populateList() {
        switch (mType) {
            case FOLLOWER:
                populateFollowerList();
                break;
            case FOLLOWING:
                populateFollowingList();
                break;
            default:
                break;
        }
    }

    public void populateFollowingList() {
        if (NetworkUtils.isNetworkAvailable(this)) {

            mClient.getFriendsList(mUser.getUuid(), mCursor,new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    UserFollowRequest userFollowRequest = new UserFollowRequest();
                    try {
                        userFollowRequest = UserFollowRequest.fromJSON(response);
                        mCursor = userFollowRequest.nextCursor;

                        for(User user: userFollowRequest.getUsers()) {
                            refreshDataAndUI(user);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        } else {
            CommonUtils.showMessage(mBinding.getRoot(), getString(R.string.network_not_available_message));
        }
    }

    public void populateFollowerList() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            mClient.getFollowerList(mUser.getUuid(),mCursor,new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    UserFollowRequest userFollowRequest = new UserFollowRequest();
                    try {
                        userFollowRequest = UserFollowRequest.fromJSON(response);
                        mCursor = userFollowRequest.nextCursor;

                        for(User user: userFollowRequest.getUsers()) {
                            refreshDataAndUI(user);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        } else {
            CommonUtils.showMessage(mBinding.getRoot(), getString(R.string.network_not_available_message));
        }
    }

    private void refreshDataAndUI(User user) {
        mUsers.add(user);
        mAdapter.notifyItemInserted(mUsers.size() - 1);
        swipeContainer.setRefreshing(false);
    }
    private void resetSearch() {
        mUsers.clear();
        mAdapter.notifyDataSetChanged();
        mCursor = -1;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
