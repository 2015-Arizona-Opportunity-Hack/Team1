package com.bramblellc.yoda.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bramblellc.yoda.R;
import com.bramblellc.yoda.services.ActionConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Landing extends Activity {


    private IntentFilter intentFilter;

    private LandingBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        intentFilter = new IntentFilter(ActionConstants.NEWS_ACTION);
        receiver = new LandingBroadcastReceiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    public void settingsPressed(View v){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        finish();
    }

    public void alertsPressed(View v) {
        Intent intent = new Intent(this, Alerts.class);
        startActivity(intent);
        finish();
    }


    private class LandingBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean successful = intent.getBooleanExtra("successful", false);
            if (!successful) {
                Toast.makeText(Landing.this, ":(", Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    String content = intent.getStringExtra("content");
                    JSONObject jsonObject = new JSONObject(content);
                    JSONArray jsonArray = jsonObject.getJSONArray("news");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        JSONArray posts = item.getJSONArray("posts");
                        JSONObject post1 = posts.get(0);
                        JSONObject post2 = posts.get(1);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            LocalBroadcastManager.getInstance(Landing.this).unregisterReceiver(receiver);
        }

    }

}
