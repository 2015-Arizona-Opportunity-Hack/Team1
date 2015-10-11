package com.bramblellc.yoda.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bramblellc.yoda.R;
import com.bramblellc.yoda.layouts.CustomActionbar;
import com.bramblellc.yoda.layouts.FullWidthButton;

public class Settings extends Activity {

    private CustomActionbar settingsCustomActionbar;
    private FullWidthButton languageFullWidthButton;
    private FullWidthButton phoneFullWidthButton;
    private FullWidthButton msgFullWidthButton;
    private FullWidthButton logoutFullWidthButton;

    private Integer[] selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        settingsCustomActionbar = (CustomActionbar) findViewById(R.id.settings_custom_actionbar);
        languageFullWidthButton = (FullWidthButton) findViewById(R.id.change_language_full_width_button);
        phoneFullWidthButton = (FullWidthButton) findViewById(R.id.change_phone_number_full_width_button);
        msgFullWidthButton = (FullWidthButton) findViewById(R.id.message_prefs_full_width_button);
        logoutFullWidthButton = (FullWidthButton) findViewById(R.id.logout_full_width_button);
        //testFullWidthButton = (FullWidthButton) findViewById(R.id.test_full_width_button);
        selected = new Integer[0];
        //selected = new Integer[2];
        //selected[0] = 0;
        //selected[1] = 1;

        settingsCustomActionbar.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        languageFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage();
            }
        });

        phoneFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhoneNumber();
            }
        });

        msgFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMessagePrefs();
            }
        });

        logoutFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutPressed();
            }
        });
    }

    public void changeLanguage() {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.change_language))
                .content(getResources().getString(R.string.change_languages_text))
                .positiveText(getResources().getString(R.string.english))
                .negativeText(getResources().getString(R.string.spanish))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        //change language to english
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        //change language to spanish
                    }
                })
                .show();
    }

    public void changePhoneNumber() {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.change_phone))
                .content(getResources().getString(R.string.change_phone_text))
                .positiveText(getResources().getString(R.string.confirm))
                .negativeText(getResources().getString(R.string.cancel))
                .inputType(InputType.TYPE_CLASS_PHONE)
                .input("phone number", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // change phone number of user on server
                    }
                })
                .show();
    }

    public void changeMessagePrefs() {
        new MaterialDialog.Builder(this)
                .title(R.string.msg_prefs_title)
                .items(R.array.messeges_list)
                .itemsCallbackMultiChoice(selected, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        return true;
                    }
                })
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .show();
    }

    public void logoutPressed() {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.log_out))
                .content(getResources().getString(R.string.log_out_confirmation))
                .positiveText(getResources().getString(R.string.yes))
                .negativeText(getResources().getString(R.string.no))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        logout();
                    }
                })
                .show();
    }

    public void logout() {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Landing.class);
        startActivity(intent);
        finish();
    }
}
