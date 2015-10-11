package com.bramblellc.yoda.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bramblellc.yoda.R;
import com.bramblellc.yoda.layouts.CustomActionbar;
import com.bramblellc.yoda.layouts.FullWidthButton;
import com.bramblellc.yoda.services.ActionConstants;
import com.bramblellc.yoda.services.ChangePropertyIntentService;
import com.bramblellc.yoda.services.LoginIntentService;

public class Settings extends Activity {

    private CustomActionbar settingsCustomActionbar;
    private FullWidthButton languageFullWidthButton;
    private FullWidthButton phoneFullWidthButton;
    private FullWidthButton msgFullWidthButton;
    private FullWidthButton helpFullWidthButton;
    private FullWidthButton logoutFullWidthButton;

    private IntentFilter filter;
    private BroadcastReceiver receiver;

    private int bitField;

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
        helpFullWidthButton = (FullWidthButton) findViewById(R.id.help_full_width_button);
        logoutFullWidthButton = (FullWidthButton) findViewById(R.id.logout_full_width_button);
        //testFullWidthButton = (FullWidthButton) findViewById(R.id.test_full_width_button);
        selected = new Integer[0];
        //selected = new Integer[2];
        //selected[0] = 0;
        //selected[1] = 1;

        bitField = 0;

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

        helpFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHelp();
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
                        Intent localIntent = new Intent(Settings.this, ChangePropertyIntentService.class);
                        localIntent.putExtra("property", "language_pref");
                        localIntent.putExtra("value", "en");

                        filter = new IntentFilter(ActionConstants.CHANGE_PROPERTY);
                        receiver = new SettingsBroadcastReceiver();
                        LocalBroadcastManager.getInstance(Settings.this).registerReceiver(receiver, filter);

                        startService(localIntent);

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        Intent localIntent = new Intent(Settings.this, ChangePropertyIntentService.class);
                        localIntent.putExtra("property", "language_pref");
                        localIntent.putExtra("value", "es");

                        filter = new IntentFilter(ActionConstants.CHANGE_PROPERTY);
                        receiver = new SettingsBroadcastReceiver();
                        LocalBroadcastManager.getInstance(Settings.this).registerReceiver(receiver, filter);

                        startService(localIntent);
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
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Intent localIntent = new Intent(Settings.this, ChangePropertyIntentService.class);
                        localIntent.putExtra("property", "message_prefs");

                        localIntent.putExtra("value", bitField + "");

                        filter = new IntentFilter(ActionConstants.CHANGE_PROPERTY);
                        receiver = new SettingsBroadcastReceiver();
                        LocalBroadcastManager.getInstance(Settings.this).registerReceiver(receiver, filter);

                        startService(localIntent);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }

                })
                .itemsCallbackMultiChoice(selected, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        if (which.length == 0) {
                            bitField = 0;
                        } else if (which.length == 1) {
                            bitField = which[0] + 1;
                        } else {
                            bitField = which[0] + 1 | which[1] + 1;
                        }

                        return true;
                    }
                })
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .show();
    }

    public void getHelp() {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.contact_ican_it))
                .content(getResources().getString(R.string.help_text))
                .positiveText(getResources().getString(R.string.yes))
                .negativeText(getResources().getString(R.string.no))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:4808214207"));
                        startActivity(callIntent);
                    }
                })
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

    private class SettingsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean successful = intent.getBooleanExtra("successful", false);
            if (!successful) {
                Log.d("Yoda", "" + intent.getBooleanExtra("authenticationFailure", false));
            }
            else {
                Log.d("Yoda", "Ayy lmao :)");
            }
        }


    }

}
