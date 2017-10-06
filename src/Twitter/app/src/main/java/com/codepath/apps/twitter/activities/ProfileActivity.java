package com.codepath.apps.twitter.activities;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.adapters.TweetAdapter;
import com.codepath.apps.twitter.databinding.ActivityProfileBinding;
import com.codepath.apps.twitter.fragments.UserTimelineFragment;
import com.codepath.apps.twitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TweetExtended;
import com.codepath.apps.twitter.models.User;
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

public class ProfileActivity extends AppCompatActivity {

    private TwitterClient client;
    private ActivityProfileBinding mBinding;
    private User mUser;

    private static final String TAG = "TwitterClient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialize();
        populateView();
        setupUserTimeline();
    }

    private void initialize() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Helvetica Neu Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        client = TwitterApp.getRestClient();
    }

    private void populateView() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            client.getLoggedInUserInformation(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    User user = new User();
                    try {
                        Log.d(TAG, response.toString());
                        user = User.fromJSON(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, user.toString());
                    mUser = user;
                    mBinding.setUser(mUser);
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
            CommonUtils.showMessage(mBinding.container, getString(R.string.network_not_available_message));
        }
    }

    private void setupUserTimeline() {
        Fragment fragment = null;
        Class fragmentClass;

        try {
            fragment = UserTimelineFragment.newInstance(mUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flTimeline, fragment).commit();
    }
}
