package com.codepath.apps.twitter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rdeshpan on 10/8/2017.
 */

public class TweetSearchResponse {
    public ArrayList<Tweet> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Tweet> statuses) {
        this.statuses = statuses;
    }

    public SearchMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SearchMetadata metadata) {
        this.metadata = metadata;
    }

    public ArrayList<Tweet> statuses;

    public SearchMetadata metadata;

    public static TweetSearchResponse fromJSON(JSONObject jsonObject) throws JSONException {
        TweetSearchResponse tweetSearchResponse = new TweetSearchResponse();
        tweetSearchResponse.metadata = SearchMetadata.fromJSON(jsonObject.getJSONObject("search_metadata"));

        tweetSearchResponse.statuses = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("statuses");

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(jsonArray.getJSONObject(i));

                tweetSearchResponse.statuses.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tweetSearchResponse;
    }
}
