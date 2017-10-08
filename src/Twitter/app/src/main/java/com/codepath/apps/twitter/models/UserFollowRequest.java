package com.codepath.apps.twitter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by rdeshpan on 10/8/2017.
 */

public class UserFollowRequest {

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<User> users;

    public int nextCursor;

    public int previousCursor;

    public static UserFollowRequest fromJSON(JSONObject jsonObject) throws JSONException {
        UserFollowRequest userFollowRequest = new UserFollowRequest();
        userFollowRequest.nextCursor = jsonObject.getInt("next_cursor");
        userFollowRequest.previousCursor = jsonObject.getInt("previous_cursor");

        userFollowRequest.users = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("users");

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                User user = User.fromJSON(jsonArray.getJSONObject(i));

                userFollowRequest.users.add(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return userFollowRequest;
    }
}
