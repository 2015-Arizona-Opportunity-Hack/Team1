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
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bramblellc.yoda.R;
import com.bramblellc.yoda.fragments.LoadingBar;
import com.bramblellc.yoda.services.ActionConstants;
import com.bramblellc.yoda.services.SignUpIntentService;

public class SignUp extends Activity {

    private EditText desiredPasswordEditText;
    private EditText phoneNumberEditText;
    private EditText emailEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private CheckBox english;
    private CheckBox spanish;
    private Button continueButton;

    private FragmentManager fm;
    private FragmentTransaction ft;
    private LoadingBar loadingBar;
    private boolean disabled;

    private String password;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    private boolean language;

    private SignUpReceiver signUpReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);

        fm = getFragmentManager();
        loadingBar = new LoadingBar();
        disabled = false;

        desiredPasswordEditText = (EditText) findViewById(R.id.editTextDesiredPassword);
        phoneNumberEditText = (EditText) findViewById(R.id.editTextPhoneNumber);
        emailEditText = (EditText) findViewById(R.id.editTextemail);
        firstNameEditText = (EditText) findViewById(R.id.editTextFirstName);
        lastNameEditText = (EditText) findViewById(R.id.editTextLastName);
        english = (CheckBox) findViewById(R.id.english_checkbox);
        english.setChecked(true);
        spanish = (CheckBox) findViewById(R.id.spanish_checkbox);
        spanish.setChecked(false);
        language = true;
        continueButton = (Button) findViewById(R.id.buttonSignUp);

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {

            private boolean isFormatting;
            private boolean deletingHyphen;
            private int hyphenStart;
            private boolean deletingBackward;

            @Override
            public void afterTextChanged(Editable text) {
                if (isFormatting)
                    return;

                isFormatting = true;

                // If deleting hyphen, also delete character before or after it
                if (deletingHyphen && hyphenStart > 0) {
                    if (deletingBackward) {
                        if (hyphenStart - 1 < text.length()) {
                            text.delete(hyphenStart - 1, hyphenStart);
                        }
                    } else if (hyphenStart < text.length()) {
                        text.delete(hyphenStart, hyphenStart + 1);
                    }
                }
                if (text.length() == 3 || text.length() == 7) {
                    text.append('-');
                }

                isFormatting = false;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (isFormatting)
                    return;

                // Make sure user is deleting one char, without a selection
                final int selStart = Selection.getSelectionStart(s);
                final int selEnd = Selection.getSelectionEnd(s);
                if (s.length() > 1 // Can delete another character
                        && count == 1 // Deleting only one character
                        && after == 0 // Deleting
                        && s.charAt(start) == '-' // a hyphen
                        && selStart == selEnd) { // no selection
                    deletingHyphen = true;
                    hyphenStart = start;
                    // Check if the user is deleting forward or backward
                    if (selStart == start + 1) {
                        deletingBackward = true;
                    } else {
                        deletingBackward = false;
                    }
                } else {
                    deletingHyphen = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        lastNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    signUp();
                }
                return false;
            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        signUpReceiver = new SignUpReceiver();
        IntentFilter filter = new IntentFilter(ActionConstants.REGISTER_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(signUpReceiver, filter);
    }

    public void disableButtons() {
        disabled = true;
        continueButton.setEnabled(false);
        desiredPasswordEditText.setEnabled(false);
        phoneNumberEditText.setEnabled(false);
    }

    public void enableButtons() {
        disabled = false;
        continueButton.setEnabled(true);
        desiredPasswordEditText.setEnabled(true);
        phoneNumberEditText.setEnabled(true);
    }

    // when the continue button is pressed (sign up)
    public void continueSignUpPressed(View v) {
        signUp();
    }

    public void signUp() {
        password = desiredPasswordEditText.getText().toString();
        phoneNumber = phoneNumberEditText.getText().toString().replace("-", "");
        email = emailEditText.getText().toString();
        firstName = firstNameEditText.getText().toString();
        lastName = lastNameEditText.getText().toString();
        // native checks on inputs gathered
        if(email.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.sign_up_credentials_empty_email_error), Toast.LENGTH_SHORT).show();
        }
        else if (password.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.sign_up_credentials_empty_password_error), Toast.LENGTH_SHORT).show();
        }
        else if(phoneNumber.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.sign_up_credentials_empty_phone_number_error), Toast.LENGTH_SHORT).show();
        }
        else if(firstName.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.sign_up_credentials_empty_first_name_error), Toast.LENGTH_SHORT).show();
        }
        else if(lastName.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.sign_up_credentials_empty_last_name_error), Toast.LENGTH_SHORT).show();
        }
        else if(password.length() < 6 || password.length() > 20){
            Toast.makeText(this,  getResources().getString(R.string.sign_up_credentials_invalid_password_length_error), Toast.LENGTH_SHORT).show();
        }
        else {
            disableButtons();
            ft = fm.beginTransaction();
            ft.add(R.id.loading_frame, loadingBar);
            ft.commit();
            continueButton.setText(getResources().getString(R.string.signing_up));
            Intent intent = new Intent(this, SignUpIntentService.class);
            intent.putExtra("username", email);
            intent.putExtra("password", password);
            intent.putExtra("phoneNumber", phoneNumber);
            intent.putExtra("email", email);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            if (language)
                intent.putExtra("language", "en");
            else
                intent.putExtra("language", "es");
            startService(intent);
        }
    }

    public void englishPressed(View view) {
        if (!language) {
            language = true;
            spanish.setEnabled(true);
            spanish.setChecked(false);
            english.setChecked(true);
            english.setEnabled(false);
        }
    }

    public void spanishPressed(View view) {
        if (language) {
            language = false;
            english.setEnabled(true);
            spanish.setChecked(true);
            english.setChecked(false);
            spanish.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (!disabled) {
            Intent intent = new Intent(this, AccountPortal.class);
            startActivity(intent);
            finish();
        }
    }

    public String getDesiredPasswordString() {
        return this.desiredPasswordEditText.getText().toString();
    }

    public String getPhoneNumberString() {
        return this.phoneNumberEditText.getText().toString();
    }

    public void setDesiredPasswordString(String desiredPassword) {
        this.desiredPasswordEditText.setText(desiredPassword);
    }

    public void setPhoneNumberString(String phoneNumber) {
        this.phoneNumberEditText.setText(phoneNumber);
    }

    private class SignUpReceiver extends BroadcastReceiver {

        private SignUpReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean successful = intent.getBooleanExtra("successful", false);
            if (!successful) {
                ft = fm.beginTransaction();
                ft.remove(loadingBar);
                ft.commit();
                Toast.makeText(SignUp.this, intent.getStringExtra("message"), Toast.LENGTH_LONG).show();
                SignUp.this.enableButtons();
            }
            else {
                LocalBroadcastManager.getInstance(SignUp.this).unregisterReceiver(signUpReceiver);
                SignUp.this.enableButtons();
                Intent startIntent = new Intent(SignUp.this, Landing.class);
                startIntent.putExtra("setup", true);
                startActivity(startIntent);
                finish();
            }
        }
    }
}
