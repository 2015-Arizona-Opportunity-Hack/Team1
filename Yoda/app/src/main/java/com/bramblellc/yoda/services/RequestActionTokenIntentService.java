package com.bramblellc.yoda.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.stevex86.napper.http.connection.ConnectionHandler;
import com.stevex86.napper.http.elements.content.JsonBodyContent;
import com.stevex86.napper.http.elements.method.Post;
import com.stevex86.napper.http.elements.route.Route;
import com.stevex86.napper.request.Request;
import com.stevex86.napper.response.Response;

import org.json.JSONObject;

public class RequestActionTokenIntentService extends IntentService {

    public RequestActionTokenIntentService() {
        this("RequestActionTokenIntentService");
    }

    public RequestActionTokenIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Class callerClass = (Class) intent.getSerializableExtra("caller");
        Bundle callerExtras = intent.getBundleExtra("callerExtras");
        try {
            Route route = new Route("http://gideon.stevex86.com/request_action_token");
            Request request = new Request(route, new Post());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("auth_token", intent.getStringExtra("auth_token"));
            JsonBodyContent content = new JsonBodyContent(jsonObject.toString());

            request.setBodyContent(content);

            ConnectionHandler connectionHandler = new ConnectionHandler(request);
            Response response = connectionHandler.getResponse();

            if (response.getResponseCode() == 200) {
                jsonObject = new JSONObject(response.getBodyContent().getOutputString());
                String authToken = jsonObject.getString("auth_token");
                String actionToken = jsonObject.getString("action_token");
                Intent localIntent = new Intent(this, callerClass);
                localIntent.putExtras(callerExtras);
                localIntent.putExtra("successful", true);
                localIntent.putExtra("repeated", true);
                startService(localIntent);
            }
            else {
                Intent localIntent = new Intent(this, callerClass);
                localIntent.putExtras(callerExtras);
                localIntent.putExtra("successful", false);
                localIntent.putExtra("repeated", true);
                startService(localIntent);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Intent localIntent = new Intent(this, callerClass);
            localIntent.putExtras(callerExtras);
            localIntent.putExtra("connectionFailure", true);
            localIntent.putExtra("repeated", true);
            startService(localIntent);
        }
    }

}
