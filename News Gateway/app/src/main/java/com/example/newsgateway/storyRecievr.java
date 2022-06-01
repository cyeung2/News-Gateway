package com.example.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class storyRecievr extends BroadcastReceiver {
    private final MainActivity mainActivity;

    public storyRecievr(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        switch (intent.getAction()) {

            default:
                Toast.makeText(mainActivity, "Unexpected boradcast: " + intent.getAction(), Toast.LENGTH_LONG).show();

            case MainActivity.ARTICLE_BROADCAST:
                String value = "";
                if (intent.hasExtra(MainActivity.DATA_EXTRA)) {
                    value = intent.getStringExtra(MainActivity.DATA_EXTRA);
                }
                break;


        }
    }
}
