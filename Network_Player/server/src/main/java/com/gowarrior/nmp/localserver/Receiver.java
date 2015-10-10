package com.gowarrior.nmp.localserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
    private final static String TAG="Receiver";
    private final  static String ACTION = "android.intent.action.BOOT_COMPLETED";
    public Receiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "action="+intent.getAction());
        if (intent.getAction().equals(ACTION)) {
            Intent server = new Intent(context, MainActivity.class);
            server.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(server);
        }
    }
}
