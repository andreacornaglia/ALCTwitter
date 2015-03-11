package com.codepath.apps.alctwitter.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    //list the attributes
    private String name;
    private long uid;
    private String screenName;
    private String profileImageUrl;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
//desserialize the json => user object

    public static User fromJSON(JSONObject json){
        User u = new User();
        //get properties from user json object
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    };
}
