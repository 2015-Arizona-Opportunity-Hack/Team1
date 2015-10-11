package com.bramblellc.yoda.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public abstract class YodaIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public YodaIntentService(String name) {
        super(name);
    }


    public void sendFailBroadcast(String failureReason, String actionConstant) {
        Intent localIntent = new Intent(actionConstant);
        localIntent.putExtra(failureReason, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
