package com.bramblellc.yoda.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.bramblellc.yoda.data.SharedPreferencesLayer;
import com.stevex86.napper.http.connection.ConnectionHandler;
import com.stevex86.napper.http.elements.content.JsonBodyContent;
import com.stevex86.napper.http.elements.method.Get;
import com.stevex86.napper.http.elements.method.Post;
import com.stevex86.napper.http.elements.route.Route;
import com.stevex86.napper.request.Request;
import com.stevex86.napper.response.Response;

import org.json.JSONObject;

public class NewsIntentService extends YodaIntentService {


    public NewsIntentService() {
        this("NewsIntentService");
    }

    public NewsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Route route = new Route("http://gideon.stevex86.com/example");
            Request request = new Request(route, new Get());
            JSONObject jsonObject = new JSONObject();

            ConnectionHandler connectionHandler = new ConnectionHandler(request);
            Response response = connectionHandler.getResponse();

            if (response.getResponseCode() == 200) {
                Intent localIntent = new Intent(ActionConstants.NEWS_ACTION);
                localIntent.putExtra("successful", true);
                localIntent.putExtra("content", response.getBodyContent().getOutputString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
            else {
                sendFailBroadcast("connectionFailure", ActionConstants.NEWS_ACTION);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            sendFailBroadcast("connectionFailure", ActionConstants.NEWS_ACTION);
        }
    }


}
