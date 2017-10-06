package com.codepath.apps.twitter.utils;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.twitter.R;
import com.codepath.apps.twitter.models.TweetRequest;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "de7h2S1vm6Sx7fPYu8pCDECRp";       // Change this
	public static final String REST_CONSUMER_SECRET = "EX4wEXulHg5p8esmuHK1JrHkmuyr7MPiNtnTKHGOZHiE0WJAkK"; // Change this

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	private static final int REQUEST_ITEM_COUNT = 25;
    private static final String TAG = "TwitterClient";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("format", "json");
		params.put("count", REQUEST_ITEM_COUNT);
		if(maxId > 0)
			params.put("max_id", maxId);
		client.get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("format", "json");
		params.put("count", REQUEST_ITEM_COUNT);
		if(maxId > 0)
			params.put("max_id", maxId);
		client.get(apiUrl, params, handler);
	}

    public void getUserTimeline(long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("format", "json");
        params.put("count", REQUEST_ITEM_COUNT);
        if(maxId > 0)
            params.put("max_id", maxId);
        client.get(apiUrl, params, handler);
    }

	public void postTweet(TweetRequest tweetRequest, AsyncHttpResponseHandler handler) {
		String  apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", tweetRequest.getStatus());
        if(tweetRequest.getInReplyToStatusId() > 0)
            params.put("in_reply_to_status_id", tweetRequest.getInReplyToStatusId());
        client.post(apiUrl, params, handler);
	}

    public void postReTweet(long tweetId, Boolean retweet, AsyncHttpResponseHandler handler) {
        String  apiUrl = retweet ? getApiUrl("statuses/retweet.json") : getApiUrl("statuses/unretweet.json");
        RequestParams params = new RequestParams();
        params.put("id", tweetId);
        client.post(apiUrl, params, handler);
    }

    public void postFavorite(long tweetId, Boolean favorite, AsyncHttpResponseHandler handler) {
        String  apiUrl = favorite ? getApiUrl("favorites/create.json") : getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", tweetId);
        client.post(apiUrl, params, handler);
    }

	public void getTweetById(long tweetId, AsyncHttpResponseHandler handler) {
		String  apiUrl = getApiUrl("statuses/show.json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
        params.put("tweet_mode", "extended");
		client.get(apiUrl, params, handler);
	}

	public void getProfileByScreenName(String userName, AsyncHttpResponseHandler handler) {
		String  apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", userName);
		client.get(apiUrl, params, handler);
	}

    public void getLoggedInUserInformation(AsyncHttpResponseHandler handler) {
        String  apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        client.get(apiUrl, params, handler);
    }
}
