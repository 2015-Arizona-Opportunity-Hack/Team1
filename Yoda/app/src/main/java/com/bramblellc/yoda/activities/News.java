package com.bramblellc.yoda.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.bramblellc.yoda.R;
import com.bramblellc.yoda.layouts.CustomActionbar;

public class News extends Activity {


    private CustomActionbar newsCustomActionbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        newsCustomActionbar = (CustomActionbar) findViewById(R.id.news_custom_actionbar);
        int news = getIntent().getIntExtra("news", 0);
        if (news == 1) {
            //newsCustomActionbar.getTextView().setText();
        }
        else if (news == 2) {
            //newsCustomActionbar.getTextView().setText();
        }
        else if (news == 3) {
            //newsCustomActionbar.getTextView().setText();
        }
        else if (news == 4) {
            //newsCustomActionbar.getTextView().setText();
        }
        else {
            //newsCustomActionbar.getTextView().setText();
        }

        //newsCustomActionbar.getTextView().setText();
        // shared prefs title

        newsCustomActionbar.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Landing.class);
        startActivity(intent);
        finish();
    }
}

