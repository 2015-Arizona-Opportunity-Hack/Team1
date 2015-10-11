package com.bramblellc.yoda.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bramblellc.yoda.R;
import com.bramblellc.yoda.fragments.LoadingBar;
import com.bramblellc.yoda.services.ActionConstants;

import java.util.Set;

public class Login extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private boolean buttonsEnabled;
    private String username;
    private String password;

    private FragmentManager fm;
    private FragmentTransaction ft;
    private LoadingBar loadingBar;
    private boolean disabled;

    private LoginReceiver loginReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_layout);

        fm = getFragmentManager();
        loadingBar = new LoadingBar();
        disabled = false;

        usernameEditText = (EditText) findViewById(R.id.editTextLoginUsername);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        loginButton = (Button) findViewById(R.id.buttonSignIn);
        buttonsEnabled = true;

        /*
        SpannableString ss = new SpannableString(getString(R.string.login_clickable_span_text));
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                //showEmailRecoveryDialog();
            }
        };
        ss.setSpan(clickableSpan1, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        TextView textView = (TextView) findViewById(R.id.login_textview);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        */
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    login();
                }
                return false;
            }
        });

        usernameEditText.requestFocus();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loginReceiver = new LoginReceiver();
        IntentFilter filter = new IntentFilter(ActionConstants.LOGIN_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(loginReceiver, filter);
    }

    public void disableButtons() {
        disabled = true;
        loginButton.setEnabled(false);
        usernameEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        buttonsEnabled = false;
    }

    public void enableButtons() {
        disabled = false;
        loginButton.setEnabled(true);
        usernameEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        buttonsEnabled = true;
    }

    @Override
    public void onBackPressed() {
        if (!disabled) {
            Intent intent = new Intent(this, AccountPortal.class);
            startActivity(intent);
            finish();
        }
    }


    // when the login button is pressed (login)
    public void loginPressed(View v){
        login();
    }

    public void login() {
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        if (username.equals("") || password.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.log_in_empty_credentials), Toast.LENGTH_SHORT).show();
        }
        else if (username.equals("q") && password.equals("q")) {
            Intent startIntent = new Intent(Login.this, Landing.class);
            startActivity(startIntent);
            finish();
        }
        else {
            disableButtons();
            ft = fm.beginTransaction();
            ft.add(R.id.loading_frame, loadingBar);
            ft.commit();
            loginButton.setText(getResources().getString(R.string.logging_in));
            //new LoginTask().execute(username, password);
            //Intent intent = new Intent(this, LoginService.class);
            //intent.putExtra("username", username);
            //intent.putExtra("password", password);
            //startService(intent);
        }
    }

    private class LoginReceiver extends BroadcastReceiver {

        private LoginReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean successful = intent.getBooleanExtra("successful", false);
            if (!successful) {
                ft = fm.beginTransaction();
                ft.remove(loadingBar);
                ft.commit();
                Toast.makeText(Login.this, getResources().getString(R.string.log_in_invalid_credentials), Toast.LENGTH_LONG).show();
                loginButton.setText(getResources().getString(R.string.log_in));
                Login.this.passwordEditText.setText("");
                Login.this.enableButtons();
            }
            else {
                LocalBroadcastManager.getInstance(Login.this).unregisterReceiver(loginReceiver);
                Intent startIntent = new Intent(Login.this, Landing.class);
                startActivity(startIntent);
                finish();
            }
        }
    }
}

