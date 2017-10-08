package com.codepath.apps.twitter.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.databinding.ActivitySearchBinding;
import com.codepath.apps.twitter.fragments.SearchFragment;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FrameLayout flMain;
    private ActivitySearchBinding mBinding;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initialize();
    }

    private void initialize() {
        setupToolbar();
        String query = getIntent().getStringExtra("query");
        populateSearchView(query);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateSearchView (String query) {
        if(query == null || query.isEmpty())
            return;

        SearchFragment searchFragment = SearchFragment.newInstance(query);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flMain, searchFragment).commit();
        setTitle(query);
    }

    public void onBack() {
        finish();
    }
}
