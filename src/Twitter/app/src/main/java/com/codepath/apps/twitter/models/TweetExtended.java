package com.codepath.apps.twitter.models;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by rdeshpan on 9/29/2017.
 */
@Parcel
public class TweetExtended {
    public String body;
    public long uuid;
    public User user;
    public String createdAt;
    public int retweetCount;
    public int favoriteCount;
    public Entities entities;
    public EntitiesExtended entitiesExtended;

    public Boolean getRetweeted() {
        return retweeted;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }

    public Boolean retweeted;
    public Boolean favorited;

    public EntitiesExtended getEntitiesExtended() {
        return entitiesExtended;
    }

    public void setEntitiesExtended(EntitiesExtended entitiesExtended) {
        this.entitiesExtended = entitiesExtended;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }


    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }


    public TweetExtended() {

    }

    public TweetExtended(String body, long uuid, String createdAt, User user) {
        this.body = body;
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.user = user;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static TweetExtended fromJSON(JSONObject jsonObject) throws JSONException {

        TweetExtended tweetExtended = new TweetExtended();
        tweetExtended.body = jsonObject.getString("full_text");
        tweetExtended.uuid = jsonObject.getLong("id");
        tweetExtended.createdAt = jsonObject.getString("created_at");
        tweetExtended.retweetCount = jsonObject.getInt("retweet_count");
        tweetExtended.favoriteCount = jsonObject.getInt("favorite_count");
        tweetExtended.retweeted = jsonObject.getBoolean("retweeted");
        tweetExtended.favorited = jsonObject.getBoolean("favorited");
        tweetExtended.user = User.fromJSON(jsonObject.getJSONObject("user"));
        try{
            tweetExtended.entities = Entities.fromJSON(jsonObject.getJSONObject("entities"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        try{
            tweetExtended.entitiesExtended = EntitiesExtended.fromJSON(jsonObject.getJSONObject("extended_entities"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return tweetExtended;
    }
}
