package com.bramblellc.yoda.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.bramblellc.yoda.data.SharedPreferencesLayer;
import com.stevex86.napper.http.connection.ConnectionHandler;
import com.stevex86.napper.http.elements.content.JsonBodyContent;
import com.stevex86.napper.http.elements.method.Post;
import com.stevex86.napper.http.elements.route.Route;
import com.stevex86.napper.request.Request;
import com.stevex86.napper.response.Response;

import org.json.JSONObject;

public class ChangePropertyIntentService extends YodaIntentService {

    public ChangePropertyIntentService() {
        this("ChangePropertyIntentService");
    }

    public ChangePropertyIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Route route = new Route("http://gideon.stevex86.com/usr_prop");
            Request request = new Request(route, new Post());
            String actionToken = SharedPreferencesLayer.getInstance().getActionToken();
            boolean repeated = intent.getBooleanExtra("repeated", false);
            if (repeated && intent.getBooleanExtra("connectionFailure", false)) {
                sendFailBroadcast("connectionFailure", ActionConstants.CHANGE_PROPERTY);
            }
            else if (repeated && !intent.getBooleanExtra("successful", false)) {
                sendFailBroadcast("authenticationFailure", ActionConstants.CHANGE_PROPERTY);
            }
            else {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("action_token", actionToken);

                JsonBodyContent content = new JsonBodyContent(jsonObject.toString());

                request.setBodyContent(content);

                ConnectionHandler connectionHandler = new ConnectionHandler(request);
                Response response = connectionHandler.getResponse();

                if (response.getResponseCode() == 200) {
                    jsonObject = new JSONObject(response.getBodyContent().getOutputString());
                    String newActionToken = jsonObject.getString("action_token");
                    SharedPreferencesLayer.getInstance().setActionToken(newActionToken);

                    Intent localIntent = new Intent(ActionConstants.CHANGE_PROPERTY);
                    localIntent.putExtra("successful", true);
                    SharedPreferencesLayer.getInstance().setAuthToken(jsonObject.getString("auth_token"));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
                else if (response.getResponseCode() == 401) {
                    if (!repeated) {
                        Intent localIntent = new Intent(this, RequestActionTokenIntentService.class);
                        localIntent.putExtra("caller", ChangePropertyIntentService.class);
                        localIntent.putExtra("callerExtras", intent.getExtras());
                        startService(localIntent);
                    }
                    else {
                        sendFailBroadcast("authenticationFailure", ActionConstants.CHANGE_PROPERTY);
                    }
                }
                else {
                    sendFailBroadcast("connectionFailure", ActionConstants.CHANGE_PROPERTY);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            sendFailBroadcast("connectionFailure", ActionConstants.CHANGE_PROPERTY);
        }
    }


}
