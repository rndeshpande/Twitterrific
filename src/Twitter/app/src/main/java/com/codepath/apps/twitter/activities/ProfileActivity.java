package com.codepath.apps.twitter.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.databinding.ActivityProfileBinding;
import com.codepath.apps.twitter.fragments.UserTimelineFragment;
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

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity {

    private TwitterClient client;
    private ActivityProfileBinding mBinding;
    private User mUser;
    private ProgressBar pbLoading;

    private static final String TAG = "TwitterClient";
    private static final String FOLLOWING = "Following";
    private static final String FOLLOWER = "Followers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialize();
        String username = getIntent().getStringExtra("username");

        if (username != null && !username.isEmpty())
            populateViewForUser(username);
        else
            populateViewLoggedInUser();
    }

    private void initialize() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        pbLoading = mBinding.pbLoading;
        pbLoading.setVisibility(ProgressBar.VISIBLE);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Helvetica Neu Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        client = TwitterApp.getRestClient();
    }

    private void populateViewLoggedInUser() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            client.getLoggedInUserInformation(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    User user = new User();
                    try {
                        user = User.fromJSON(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mUser = user;
                    mBinding.setUser(mUser);
                    setupUserTimeline(mUser);
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    throwable.printStackTrace();
                }
            });
        } else {
            CommonUtils.showMessage(mBinding.container, getString(R.string.network_not_available_message));
            pbLoading.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    private void populateViewForUser(String username) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            client.getProfileByScreenName(username, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    User user = new User();
                    try {
                        user = User.fromJSON(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mUser = user;
                    mBinding.setUser(mUser);
                    setupUserTimeline(user);
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    throwable.printStackTrace();
                }
            });
        } else {
            CommonUtils.showMessage(mBinding.container, getString(R.string.network_not_available_message));
            pbLoading.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    private void setupUserTimeline(User user) {
        Fragment fragment = null;
        Class fragmentClass;

        try {
            fragment = UserTimelineFragment.newInstance(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flTimeline, fragment).commit();
    }

    public void onFollowCountClick(View view) {
        String type = "";
        switch (view.getId()) {
            case R.id.tvFollowerCount:
                type = FOLLOWER;
                break;
            case R.id.tvFollowingCount:
                type = FOLLOWING;
                break;
            default:
                break;
        }
        Intent intent = new Intent(this, FollowActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("user", Parcels.wrap(mUser));
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
