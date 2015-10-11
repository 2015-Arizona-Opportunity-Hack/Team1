package com.bramblellc.yoda.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesLayer {

    private static final String preferencesFile = "Gideon";

    private static SharedPreferencesLayer instance;

    private Context mContext;

    private SharedPreferences sharedPreferences;


    private SharedPreferencesLayer(Context ctx) {
        mContext = ctx;
        sharedPreferences = ctx.getSharedPreferences(preferencesFile, Context.MODE_PRIVATE);
    }

    public void init(Context ctx) {
        instance = new SharedPreferencesLayer(ctx);
    }

    public SharedPreferencesLayer getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SharedPreferencesLayer not initialized");
        }
        else {
            return instance;
        }
    }

    public String getUsername() {
        return sharedPreferences.getString("username", "");
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString("phoneNumber", "");
    }

    public void setPhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString("authToken", "");
    }

    public void setAuthToken(String authToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("authToken", authToken);
        editor.apply();
    }

    public String getActionToken() {
        return sharedPreferences.getString("actionToken", "");
    }

    public void setActionToken(String actionToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("actionToken", actionToken);
        editor.apply();
    }

}
