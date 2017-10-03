package com.codepath.apps.twitter.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by rdeshpan on 10/1/2017.
 */
@Parcel
public class VideoVariant {

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String bitrate;
    String contentType;
    String url;

    public static VideoVariant fromJSON(JSONObject jsonObject) throws JSONException {
        VideoVariant videoVariant = new VideoVariant();

        videoVariant.bitrate = jsonObject.getString("bitrate");
        videoVariant.contentType = jsonObject.getString("content_type");
        videoVariant.url = jsonObject.getString("url");
        return videoVariant;
    }

}
