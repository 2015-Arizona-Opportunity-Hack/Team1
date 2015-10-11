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

public class SignUpIntentService extends YodaIntentService {

    public SignUpIntentService(String name) {
        super(name);
    }

    public SignUpIntentService() {
        this("SignUpService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            //route URL
            Route route = new Route("http://gideon.stevex86.com/register");
            //makes request to the route ^^^
            Request request = new Request(route, new Post());

            //Builds JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", intent.getStringExtra("email"));
            jsonObject.put("password", intent.getStringExtra("password"));
            jsonObject.put("first_name", intent.getStringExtra("firstName"));
            jsonObject.put("last_name", intent.getStringExtra("lastName"));
            jsonObject.put("phone_number", intent.getStringExtra("phoneNumber"));
            jsonObject.put("language_pref", intent.getStringExtra("language"));
            //converts JSON object to JSON string
            JsonBodyContent content = new JsonBodyContent(jsonObject.toString());

            // adds the JSON string to the request
            request.setBodyContent(content);

            // builds connection handler for request
            ConnectionHandler connectionHandler = new ConnectionHandler(request);
            // retrieves response from connection to route formed by connection handler
            Response response = connectionHandler.getResponse();

            if (response.getResponseCode() == 200) {
                jsonObject = new JSONObject(response.getBodyContent().getOutputString());
                String authToken = jsonObject.getString("auth_token");
                SharedPreferencesLayer.getInstance().setAuthToken(authToken);
                Intent localIntent = new Intent(ActionConstants.REGISTER_ACTION);
                localIntent.putExtra("successful", true);
                localIntent.putExtra("authToken", authToken);
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
            else {
                String outputString = response.getBodyContent().getOutputString();
                Intent localIntent = new Intent(ActionConstants.REGISTER_ACTION);
                localIntent.putExtra("successful", false);
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            sendFailBroadcast("connectionFailure", ActionConstants.CHANGE_PROPERTY);
        }
    }

}
