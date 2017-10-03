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
public class Entities {
    public Entities() {
    }

    public ArrayList<Media> media;

    public static Entities fromJSON(JSONObject jsonObject) throws JSONException {
        Entities entities = new Entities();
        entities.media = new ArrayList<>();

        JSONArray jsonArray = jsonObject.getJSONArray("media");

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Media media = Media.fromJSON(jsonArray.getJSONObject(i));

                entities.media.add(media);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return entities;
    }
}
