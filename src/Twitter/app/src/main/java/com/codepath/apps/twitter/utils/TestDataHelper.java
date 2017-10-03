package com.codepath.apps.twitter.utils;

import com.codepath.apps.twitter.models.Tweet;
import com.codepath.apps.twitter.models.TweetExtended;
import com.codepath.apps.twitter.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rdeshpan on 9/29/2017.
 */

public class TestDataHelper {

    public static Tweet getTweet () {
        Tweet tweet = new Tweet();

        tweet.setBody("With data from @googlenewslab, tap below to see our insights on the top search trends from this week. https://t.codV854mSofU");
        tweet.setCreatedAt("Fri Sep 29 23:24:44 +0000 2017");
        tweet.setUuid(913907427224936449L);
        tweet.setFavoriteCount(171);
        tweet.setRetweetCount(30);

        User user = new User();
        user.setName("Google");
        user.setProfileImageUrl("http://pbs.twimg.com/profile_images/839721704163155970/LI_TRk1z_normal.jpg");
        user.setScreenName("Google");
        user.setVerified(true);
        user.setUuid(20536157);

        tweet.user = user;

        return  tweet;
    }


    public static TweetExtended getTweetExtended () {
        TweetExtended tweet = new TweetExtended();
        tweet.setBody("With data from @googlenewslab, tap below to see our insights on the top search trends from this week. https://t.codV854mSofU");
        tweet.setCreatedAt("Fri Sep 29 23:24:44 +0000 2017");
        tweet.setUuid(913907427224936449L);
        User user = new User();
        user.setName("Google");
        user.setProfileImageUrl("http://pbs.twimg.com/profile_images/839721704163155970/LI_TRk1z_normal.jpg");
        user.setScreenName("Google");
        user.setVerified(true);
        user.setUuid(20536157);
        tweet.user = user;
        return  tweet;
    }

    public static ArrayList<Tweet> getTweets() {
        ArrayList<Tweet> tweets = new ArrayList<>();
        tweets.add(getTweet());
        return tweets;
    }
}
