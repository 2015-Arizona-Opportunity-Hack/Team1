package com.bramblellc.yoda.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.stevex86.napper.http.connection.ConnectionHandler;
import com.stevex86.napper.http.elements.content.JsonBodyContent;
import com.stevex86.napper.http.elements.method.Post;
import com.stevex86.napper.http.elements.route.Route;
import com.stevex86.napper.request.Request;
import com.stevex86.napper.response.Response;

import org.json.JSONObject;

public class LoginIntentService extends IntentService {

    public LoginIntentService() {
        this("LoginIntentService");
    }

    public LoginIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Route route = new Route("https://v2-dot-kickback-server.appspot.com/api/login");
            Request request = new Request(route, new Post());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", intent.getStringExtra("username"));
            jsonObject.put("password", intent.getStringExtra("password"));
            JsonBodyContent content = new JsonBodyContent(jsonObject.toString());

            request.setBodyContent(content);

            ConnectionHandler connectionHandler = new ConnectionHandler(request);
            Response response = connectionHandler.getResponse();

            if (response.getResponseCode() == 200) {
                jsonObject = new JSONObject(response.getBodyContent().getOutputString());
                String authToken = jsonObject.getString("auth_token");
                Intent localIntent = new Intent(ActionConstants.LOGIN_ACTION);
                localIntent.putExtra("successful", true);
                localIntent.putExtra("auth_token", authToken);
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
            else {
                Intent localIntent = new Intent(ActionConstants.LOGIN_ACTION);
                localIntent.putExtra("successful", false);
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}