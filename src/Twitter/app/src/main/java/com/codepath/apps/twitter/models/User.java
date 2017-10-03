package com.codepath.apps.twitter.models;

import com.codepath.apps.twitter.database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
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
public class User extends BaseModel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Column
    public String name;

    @Column
    @PrimaryKey
    public long uuid;

    @Column
    public String screenName;

    @Column
    public String profileImageUrl;

    @Column
    public boolean verified;

    public static User fromJSON(JSONObject jsonObject) throws JSONException{
        User user = new User();
        user.name = jsonObject.getString("name");
        user.uuid = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");
        user.verified = jsonObject.getBoolean("verified");
        return user;
    }
}
