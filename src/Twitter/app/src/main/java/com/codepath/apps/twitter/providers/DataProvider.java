package com.codepath.apps.twitter.providers;

import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TweetModel;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;

/**
 * Created by rdeshpan on 9/30/2017.
 */

public class DataProvider {

    public ArrayList<Tweet> readTweets() {
        ArrayList<Tweet> tweets = (ArrayList) SQLite.select().from(Tweet.class).queryList();
        return tweets;
    }

    public void saveDraft(long uuid, String status, String userName, String screenName, String createdAt, Boolean verified, int retweetCount, int favoriteCount) {
        //Tweet tweet = new TweetModel(uuid, status, userName, screenName, createdAt, verified, true, retweetCount, favoriteCount);
        //tweetModel.insert();
    }

    public void saveTweet(long uuid, String status, String userName, String screenName, String createdAt, Boolean verified, Boolean isDraft, int retweetCount, int favoriteCount) {
        //Tweet tweet = new TweetModel(uuid, status, userName, screenName, createdAt, verified, false, retweetCount, favoriteCount);
        //tweetModel.insert();
    }

    public void deleteAll() {
        SQLite.delete().from(TweetModel.class).query();
    }
}
