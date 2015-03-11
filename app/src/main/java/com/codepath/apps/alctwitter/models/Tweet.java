package com.codepath.apps.alctwitter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *The objective of this model is to parse the json data and store it
 * encapsulate state logic or response logic
 **/
public class Tweet {
    //list out the attributes that we need to get
    private String body;
    private long uid; //unique id for the tweet
    private User user;
    private String createdAt;
    private String screenName;

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getScreenName() {return screenName;}

    //Desserliaze the json and build tweet objects
    //Tweet.fromJSON["{}", ... "{}"] => Tweets
    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();
        //list the things we want to extract from the tweet
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user= User.fromJSON(jsonObject.getJSONObject("user"));
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        //tweet.user
        return tweet;
    }
    //Serialize our variables now to create the new tweets objects
    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray){
        ArrayList<Tweet> aTweet = new ArrayList<>();
        //iterate the json array and create tweets
        for (int i = 0 ; i < jsonArray.length() ; i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    aTweet.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        //return the aTweet
        return aTweet;
    }

}
