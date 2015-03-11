package com.codepath.apps.alctwitter;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "06gP6n7WHxdr0noyC2lLy9p1X";
	public static final String REST_CONSUMER_SECRET = "Hppxpa3n9puGsx1uxZE6uEBAUMIlBAhzmaQG6AoJdzPbs02n6k";
	public static final String REST_CALLBACK_URL = "oauth://cpalctwitter";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    //We need to define the methods to grab the json data and populate our timeline screen
    //an endpoint is the method used to grab this data
    //GET statuses/home_timeline.json
    //count=25
    //since_id=1
    public void getHomeTimeline(AsyncHttpResponseHandler handler, long maxId) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        //specify the parameters
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (maxId > 0) {
            params.put("max_id", maxId);
        }
        //execute the request
        getClient().get(apiUrl, params, handler);
    }

    public void getUserInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        //specify the parameters
        //execute the request
        getClient().get(apiUrl, null, handler);
    }

    public void postNewTweet(AsyncHttpResponseHandler handler, String tweetStatus) {
        String apiUrl = getApiUrl("statuses/update.json");
        //specify the parameters
        RequestParams params = new RequestParams();
        params.put("status", tweetStatus);
        //execute the request
        getClient().post(apiUrl, params, handler);
    }
}