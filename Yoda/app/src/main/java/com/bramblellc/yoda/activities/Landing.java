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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bramblellc.yoda.R;
import com.bramblellc.yoda.services.ActionConstants;
import com.bramblellc.yoda.services.NewsIntentService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Landing extends Activity {


    private IntentFilter intentFilter;

    private LandingBroadcastReceiver receiver;

    private RelativeLayout news1;
    private RelativeLayout news2;
    private RelativeLayout news3;
    private RelativeLayout news4;
    private RelativeLayout news5;

    private TextView title1;
    private TextView title2;
    private TextView title3;
    private TextView title4;
    private TextView title5;

    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private TextView text5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            Intent intent = new Intent(this, AccountPortal.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
            startActivity(intent);
            finish();
            return;
        }

        intentFilter = new IntentFilter(ActionConstants.NEWS_ACTION);
        receiver = new LandingBroadcastReceiver();

        news1 = (RelativeLayout) findViewById(R.id.news1);
        news2 = (RelativeLayout) findViewById(R.id.news2);
        news3 = (RelativeLayout) findViewById(R.id.news3);
        news4 = (RelativeLayout) findViewById(R.id.news4);
        news5 = (RelativeLayout) findViewById(R.id.news5);

        title1 = (TextView) findViewById(R.id.title1);
        title2 = (TextView) findViewById(R.id.title2);
        title3 = (TextView) findViewById(R.id.title3);
        title4 = (TextView) findViewById(R.id.title4);
        title5 = (TextView) findViewById(R.id.title5);

        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);
        text4 = (TextView) findViewById(R.id.text4);
        text5 = (TextView) findViewById(R.id.text5);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
        Intent localIntent = new Intent(this, NewsIntentService.class);
        startService(localIntent);
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
                    String len = Locale.getDefault().getLanguage();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        System.out.println(i);
                        JSONObject item = jsonArray.getJSONObject(i);
                        JSONArray posts = item.getJSONArray("posts");
                        JSONObject post1 = (JSONObject) posts.get(0);
                        JSONObject post2 = (JSONObject) posts.get(1);
                        boolean postone = false;
                        if (len.equals(post1.getString("lang"))) {
                            postone = true;
                        }
                        if (i == 0) {
                            if (postone) {
                                title1.setText(post1.getString("title"));
                                text1.setText(post1.getString("body"));
                            }
                            else {
                                title1.setText(post2.getString("title"));
                                text1.setText(post2.getString("body"));
                            }
                            news1.setVisibility(View.VISIBLE);
                        }
                        else if (i == 1) {
                            if (postone) {
                                title2.setText(post1.getString("title"));
                                text2.setText(post1.getString("body"));
                            }
                            else {
                                title2.setText(post2.getString("title"));
                                text2.setText(post2.getString("body"));
                            }
                            news2.setVisibility(View.VISIBLE);
                        }
                        else if (i == 2) {
                            if (postone) {
                                title3.setText(post1.getString("title"));
                                text3.setText(post1.getString("body"));
                            }
                            else {
                                title3.setText(post2.getString("title"));
                                text3.setText(post2.getString("body"));
                            }
                            news3.setVisibility(View.VISIBLE);
                        }
                        else if (i == 3) {
                            if (postone) {
                                title4.setText(post1.getString("title"));
                                text4.setText(post1.getString("body"));
                            }
                            else {
                                title4.setText(post2.getString("title"));
                                text4.setText(post2.getString("body"));
                            }
                            news4.setVisibility(View.VISIBLE);
                        }
                        else {
                            if (postone) {
                                title5.setText(post1.getString("title"));
                                text5.setText(post1.getString("body"));
                            }
                            else {
                                title5.setText(post2.getString("title"));
                                text5.setText(post2.getString("body"));
                            }
                            news5.setVisibility(View.VISIBLE);
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            LocalBroadcastManager.getInstance(Landing.this).unregisterReceiver(receiver);
        }

    }

    public void btn1(View view) {
        Intent intent = new Intent(this, News.class);
        intent.putExtra("title",title1.getText().toString());
        intent.putExtra("text",text1.getText().toString());
        startActivity(intent);
        finish();
    }
    public void btn2(View view) {
        Intent intent = new Intent(this, News.class);
        intent.putExtra("title",title2.getText().toString());
        intent.putExtra("text",text2.getText().toString());
        startActivity(intent);
        finish();
    }
    public void btn3(View view) {
        Intent intent = new Intent(this, News.class);
        intent.putExtra("title",title3.getText().toString());
        intent.putExtra("text",text3.getText().toString());
        startActivity(intent);
        finish();
    }
    public void btn4(View view) {
        Intent intent = new Intent(this, News.class);
        intent.putExtra("title",title4.getText().toString());
        intent.putExtra("text",text4.getText().toString());
        startActivity(intent);
        finish();
    }
    public void btn5(View view) {
        Intent intent = new Intent(this, News.class);
        intent.putExtra("title",title5.getText().toString());
        intent.putExtra("text",text5.getText().toString());
        startActivity(intent);
        finish();
    }

}
