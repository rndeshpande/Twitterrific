package com.codepath.apps.twitter.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by rdeshpan on 10/1/2017.
 */
@Parcel
public class VideoInfo {
    public ArrayList<VideoVariant> getVariants() {
        return variants;
    }

    public void setVariants(ArrayList<VideoVariant> variants) {
        this.variants = variants;
    }

    ArrayList<VideoVariant> variants;

    public static VideoInfo fromJSON(JSONObject jsonObject) throws JSONException {
        VideoInfo videoInfo = new VideoInfo();

        videoInfo.variants = new ArrayList<>();

        JSONArray jsonArray = jsonObject.getJSONArray("variants");

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                VideoVariant variant = VideoVariant.fromJSON(jsonArray.getJSONObject(i));

                videoInfo.variants.add(variant);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return videoInfo;
    }

}
