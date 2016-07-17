package com.alextinekov.socialcontacts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Alex Tinekov on 14.07.2016.
 */
public class BaseActivity extends Activity {
    public static final String ACTION_SOCIAL_STATUS_CHANGED = "com.alextinekov.socialcontacts.BaseActivity.ACTION_SOCIAL_STATUS_CHANGED";
    public static final String NETWORK_NAME = "network_name";
    public static final String TOKEN_STATUS = "token_status";
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentFilter = new IntentFilter(ACTION_SOCIAL_STATUS_CHANGED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(authStateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(authStateReceiver);
    }

    private BroadcastReceiver authStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra(NETWORK_NAME);
            SocialContactsApplication.TOKEN_STATE state = (SocialContactsApplication.TOKEN_STATE)intent.getSerializableExtra(TOKEN_STATUS);
            onSocialConnectionStateChanged(name, state);
        }
    };

    protected void onSocialConnectionStateChanged(String networkName, SocialContactsApplication.TOKEN_STATE tokenState){
        if(tokenState == SocialContactsApplication.TOKEN_STATE.absent){
            Resources res = getResources();
            String message = String.format(res.getString(R.string.unable_to_connect), networkName);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
