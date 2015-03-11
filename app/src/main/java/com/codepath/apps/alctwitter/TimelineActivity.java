package com.codepath.apps.alctwitter;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.apps.alctwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private String screenName;
    private String profilePictureUrl;
    private String userName;
    private ImageView ivUserPicture;
    private String tweetMessage;
    private EditText etCompose;
    private String tweetStatus;

    // ID of the earliest tweet that was retrieved so far.
    // 0 means not set yet.
    private long oldestTweetId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        //create the array list
        tweets = new ArrayList<>();
        //create the adapter from data source
        aTweets = new TweetsArrayAdapter(this,tweets);
        //connect adapter to listview
        lvTweets.setAdapter(aTweets);
        lvTweets.setOnScrollListener(new EndlessScroller() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                populateTimeline(false);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
        //Get the client
        client = TwitterApp.getRestClient();
        populateTimeline(true);
        loadUserInfo();

        //make tweet button send message
        final Button button = (Button) findViewById(R.id.btTweet);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                composeTweet();
                findViewById(R.id.compose_screen).setVisibility(View.GONE);
                lvTweets.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("TweetFeed");
            }
        });
    }


    //send a request to get the json
    //fill the listview by creating the tweets objects from the json
    // if clearAll is true, all existing tweets will be cleared
    // if clearAll is false, older tweets will be loaded and appended to the existing ones
    private void populateTimeline(final boolean clearAll) {
        Log.d("DEBUG", "populating timeline, clearAll=" + clearAll);
        if (clearAll) {
            oldestTweetId = 0;
        }
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            //success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", "populate timeline success.");
                Log.d("DEBUG",json.toString());
                //json here
                //desserialize json
                //create models
                //load the model data into listview (adapter \0/!)
                //ArrayList<Tweet> tweets = Tweet.fromJSONArray();
                ArrayList<Tweet> results = Tweet.fromJSONArray(json);
                Log.d("DEBUG", "Got " + results.size() + " results, adding to adapter.");
                oldestTweetId = results.get(results.size() - 1).getUid();
                if (clearAll) {
                    aTweets.clear();
                }
                aTweets.addAll(results);
                aTweets.notifyDataSetChanged();
            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "populate time line failed and we are sad");
                Log.d("DEBUG", "Error:" + (errorResponse == null ? "NULL" : errorResponse.toString()));
            }
        }, oldestTweetId);
    }

    private void loadUserInfo() {
        ivUserPicture = (ImageView) findViewById(R.id.ivUserPicture);
        client.getUserInfo(new JsonHttpResponseHandler() {
            //success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", json.toString());
                try {
                    screenName = json.getString("screen_name");
                    userName = json.getString("name");
                    profilePictureUrl = json.getString("profile_image_url");
                    ((TextView) findViewById(R.id.tvUserName)).setText(userName);
                    ((TextView) findViewById(R.id.tvScreenName)).setText("@" + screenName);
                    Picasso.with(TimelineActivity.this).load(profilePictureUrl).into(ivUserPicture);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "Error:" + (errorResponse == null ? "NULL" : errorResponse.toString()));
            }
        });
    }

    private void composeTweet() {
        etCompose = (EditText) findViewById(R.id.etCompose);
        tweetStatus = etCompose.getText().toString();
        client.postNewTweet(new JsonHttpResponseHandler() {
            //success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG","Tweet Posted Successfully");
                populateTimeline(true);
            }

            //failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "Error:" + (errorResponse == null ? "NULL" : errorResponse.toString()));
            }
        }, tweetStatus);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.compose_tweet) {
            // Show the compose screen
            findViewById(R.id.compose_screen).setVisibility(View.VISIBLE);
            lvTweets.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Compose Tweet");
        }

        if (id==R.id.refresh_timeline){
            populateTimeline(true);
        }

        return super.onOptionsItemSelected(item);
    }
}
