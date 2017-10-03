package com.codepath.apps.twitter.handlers;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.twitter.activities.DetailsActivity;
import com.codepath.apps.twitter.models.Tweet;

import org.parceler.Parcels;

/**
 * Created by rdeshpan on 9/29/2017.
 */

public class TweetHandler {

    public void showDetails(View view, Tweet tweet) {
        Context context = view.getContext();
        Toast.makeText(context, "CLICKED", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("tweet_details", Parcels.wrap(tweet));
        context.startActivity(intent);
    }
}
