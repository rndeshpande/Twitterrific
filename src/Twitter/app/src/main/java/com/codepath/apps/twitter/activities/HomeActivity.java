package com.codepath.apps.twitter.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.adapters.HomeFragmentPagerAdapter;
import com.codepath.apps.twitter.fragments.CreateDialogFragment;
import com.codepath.apps.twitter.fragments.TimelineFragment;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TweetRequest;
import com.codepath.apps.twitter.utils.CommonUtils;
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


public class HomeActivity extends AppCompatActivity  implements CreateDialogFragment.OnFragmentInteractionListener {

    private TwitterClient client;
    FloatingActionButton btnCreatePost;
    private static final int ROTATION = 360;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    ViewPager mViewPager;
    HomeFragmentPagerAdapter mAdapter;

    private static final String TAG = "TwitterClient";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupTabs();
        setToolbar();
        setupStyle();
        setupDrawer();
        initialize();
    }

    private void initialize() {
        btnCreatePost = (FloatingActionButton) findViewById(R.id.btnCreatePost);

        btnCreatePost.setOnClickListener(v -> {
            btnCreatePost.animate().rotation(ROTATION);
            showCreateDialog(null);
        });

        client = TwitterApp.getRestClient();
    }

    private void setupDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        mDrawer.addDrawerListener(drawerToggle);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_profile :
                Intent intent  = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                Fragment fragment = null;
                Class fragmentClass = TimelineFragment.class;

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.viewpager, fragment).commit();

                // Highlight the selected item has been done by NavigationView
                menuItem.setChecked(true);
                // Set action bar title
                setTitle(menuItem.getTitle());
                // Close the navigation drawer
                mDrawer.closeDrawers();
                break;
        }
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupStyle() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Helvetica Neu Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        client.clearAccessToken();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void showCreateDialog(TweetRequest tweetRequest) {
        CreateDialogFragment dialogFragment;

        if (tweetRequest != null)
            dialogFragment = CreateDialogFragment.newInstance(Parcels.wrap(tweetRequest));
        else
            dialogFragment = CreateDialogFragment.newInstance();

        dialogFragment.show(HomeActivity.this.getSupportFragmentManager(), "fragment_create_dialog");
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupTabs() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), HomeActivity.this);
        mViewPager.setAdapter(mAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setTitle(mViewPager.getAdapter().getPageTitle(tabLayout.getSelectedTabPosition()));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTitle(mViewPager.getAdapter().getPageTitle(tabLayout.getSelectedTabPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onFragmentInteraction(TweetRequest tweetRequest) {
        //Toast.makeText(this, tweetRequest.getStatus(), Toast.LENGTH_SHORT).show();
        postTweet(tweetRequest);
        //CommonUtils.showMessage(mBinding.clMain, getString(R.string.tweet_posted));
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
                updateHomeTimeline(tweet);
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

    private void updateHomeTimeline(Tweet tweet) {
        TimelineFragment fragment = (TimelineFragment) mAdapter.getRegisteredFragment(0);
        fragment.addTweetToTop(tweet);
    }
}
