package com.codepath.apps.twitter.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by rdeshpan on 10/1/2017.
 */

@Parcel
public class Media {
    public Media() {

    }
    public Media(long id, String mediaUrl, String displayUrl, String type) {
        this.id = id;
        this.mediaUrl = mediaUrl;
        this.displayUrl = displayUrl;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    long id;
    String mediaUrl;
    String displayUrl;
    String type;
    public VideoInfo videoInfo;

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }


    public static Media fromJSON(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.id = jsonObject.getLong("id");
        media.mediaUrl = jsonObject.getString("media_url");
        media.displayUrl = jsonObject.getString("display_url");
        media.type = jsonObject.getString("type");
        try {
            media.videoInfo = VideoInfo.fromJSON(jsonObject.getJSONObject("video_info"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return media;
    }

}
