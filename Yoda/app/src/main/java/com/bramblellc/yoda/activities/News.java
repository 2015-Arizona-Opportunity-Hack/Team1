package com.bramblellc.yoda.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bramblellc.yoda.R;
import com.bramblellc.yoda.layouts.CustomActionbar;

public class News extends Activity {


    private CustomActionbar newsCustomActionbar;
    private TextView textView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        newsCustomActionbar = (CustomActionbar) findViewById(R.id.news_custom_actionbar);
        textView = (TextView) findViewById(R.id.text);
        String title = getIntent().getStringExtra("title");
        String text = getIntent().getStringExtra("text");
        textView.setText(text);
        newsCustomActionbar.getTextView().setText(title);

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

