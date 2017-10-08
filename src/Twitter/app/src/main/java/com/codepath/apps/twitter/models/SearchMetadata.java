package com.codepath.apps.twitter.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rdeshpan on 10/8/2017.
 */

public class SearchMetadata {

    public long maxId;

    public String nextResults;

    public static SearchMetadata fromJSON(JSONObject jsonObject) throws JSONException {
        SearchMetadata searchMetadata = new SearchMetadata();
        searchMetadata.maxId = jsonObject.getLong("max_id");
        searchMetadata.nextResults = jsonObject.getString("next_results");
        return searchMetadata;
    }
}
