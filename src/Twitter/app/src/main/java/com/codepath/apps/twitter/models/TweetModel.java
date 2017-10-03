package com.codepath.apps.twitter.models;

import com.codepath.apps.twitter.database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by rdeshpan on 9/30/2017.
 */

@Table(database = AppDatabase.class)
public class TweetModel extends BaseModel {
    public TweetModel(int id, long uuid, String status, String userName, String screenName, String createdAt, Boolean verified, Boolean isDraft, int retweetCount, int favoriteCount) {
        this.id = id;
        this.uuid = uuid;
        this.status = status;
        this.userName = userName;
        this.screenName = screenName;
        this.createdAt = createdAt;
        this.verified = verified;
        this.isDraft = isDraft;
        this.retweetCount = retweetCount;
        this.favoriteCount = favoriteCount;
    }

    public TweetModel(long uuid, String status, String userName, String screenName, String createdAt, Boolean verified, Boolean isDraft, int retweetCount, int favoriteCount) {
        this.id = id;
        this.uuid = uuid;
        this.status = status;
        this.userName = userName;
        this.screenName = screenName;
        this.createdAt = createdAt;
        this.verified = verified;
        this.isDraft = isDraft;
        this.retweetCount = retweetCount;
        this.favoriteCount = favoriteCount;
    }

    public TweetModel(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    @Column
    @PrimaryKey(autoincrement = true)
    int id;

    @Unique(onUniqueConflict = ConflictAction.REPLACE)
    @Column
    long uuid;

    @Column
    String status;

    @Column
    String userName;

    @Column
    String screenName;

    @Column
    String createdAt;

    @Column
    Boolean verified;

    @Column
    int retweetCount;

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

    @Column
    int favoriteCount;

    public Boolean getDraft() {
        return isDraft;
    }

    public void setDraft(Boolean draft) {
        isDraft = draft;
    }

    @Column
    Boolean isDraft;
}
