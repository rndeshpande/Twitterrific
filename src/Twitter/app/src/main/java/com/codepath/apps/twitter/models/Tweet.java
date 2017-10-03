package com.codepath.apps.twitter.models;

import com.codepath.apps.twitter.database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by rdeshpan on 9/25/2017.
 */
@Parcel
@Table(database = AppDatabase.class)
public class Tweet extends BaseModel {
    @Column
    public String body;

    @Column
    @PrimaryKey
    public long uuid;

    @ForeignKey
    public User user;

    @Column
    public String createdAt;

    @Column
    public int retweetCount;

    @Column
    public int favoriteCount;

    public Boolean getRetweeted() {
        return retweeted;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    @Column
    public Boolean retweeted;

    @Column
    public Boolean favorited;

    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    public Entities entities;

    public  Tweet() {
    }

    public Tweet(String body, long uuid, String createdAt, User user, int retweetCount, int favoriteCount, Boolean retweeted, Boolean favorited) {
        this.body = body;
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.user = user;
        this.retweetCount = retweetCount;
        this.favoriteCount = favoriteCount;
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

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.uuid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        return tweet;
    }
}
