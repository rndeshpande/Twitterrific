package com.codepath.apps.twitter.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.adapters.HomeFragmentPagerAdapter;
import com.codepath.apps.twitter.fragments.CreateDialogFragment;
import com.codepath.apps.twitter.fragments.SearchFragment;
import com.codepath.apps.twitter.fragments.TimelineFragment;
import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TweetRequest;
import com.codepath.apps.twitter.utils.CommonUtils;
import com.codepath.apps.twitter.utils.TwitterApp;
import com.codepath.apps.twitter.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.sdk.android.core.models.Search;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

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
    private EditText etSearch;
    private TabLayout tabLayout;
    ViewPager mViewPager;
    HomeFragmentPagerAdapter mAdapter;
    CoordinatorLayout clMain;

    private static final String TAG = "TwitterClient";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkSendIntent();
        setToolbar();
        setupTabs();
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
        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                updateSearchTimeline(v.getText().toString());
                return true;
            }
            return false;
        });
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
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

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setTitle(mViewPager.getAdapter().getPageTitle(tabLayout.getSelectedTabPosition()));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTitle(mViewPager.getAdapter().getPageTitle(tab.getPosition()));
                if(tab.getPosition() == 2) {
                    etSearch.setVisibility(View.VISIBLE);
                    etSearch.requestFocus();
                }
                else
                    etSearch.setVisibility(View.GONE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabStyle();
    }

    private void setupTabStyle() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_blue);
        tabLayout.getTabAt(0).setText("");

        tabLayout.getTabAt(1).setIcon(R.drawable.ic_comments1600);
        tabLayout.getTabAt(1).setText("");

        tabLayout.getTabAt(2).setIcon(R.drawable.ic_search_white);
        tabLayout.getTabAt(2).setText("");
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
        postTweet(tweetRequest);
        clMain = (CoordinatorLayout) findViewById(R.id.clMain);
        CommonUtils.showMessage(clMain , getString(R.string.tweet_posted));
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

    private void updateSearchTimeline(String text) {
        SearchFragment fragment = (SearchFragment) mAdapter.getRegisteredFragment(2);
        fragment.setSearchText(text);
    }
}
