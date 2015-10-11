package com.bramblellc.yoda.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.bramblellc.yoda.R;
import com.bramblellc.yoda.layouts.CustomActionbar;

public class Alerts extends Activity {

    private CustomActionbar alertsCustomActionbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alerts_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        alertsCustomActionbar = (CustomActionbar) findViewById(R.id.alerts_custom_actionbar);


        alertsCustomActionbar.getBackButton().setOnClickListener(new View.OnClickListener() {
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
