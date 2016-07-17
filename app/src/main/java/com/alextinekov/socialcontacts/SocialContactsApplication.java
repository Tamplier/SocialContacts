package com.alextinekov.socialcontacts;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

/**
 * Created by Alex Tinekov on 14.07.2016.
 */
public class SocialContactsApplication extends Application {
    public enum TOKEN_STATE{unknown, absent, fine};
    private TOKEN_STATE vkState = TOKEN_STATE.unknown;
    private TOKEN_STATE fbState = TOKEN_STATE.unknown;
    private static SocialContactsApplication instance;

    private VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            updateVKTokenState(newToken);
            sendStatusChangedBroadcast(Constants.VK_NETWORK_NAME, vkState);
        }
    };

    private AccessTokenTracker fbAccessTokenTracker = null;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        vkAccessTokenTracker.startTracking();


        VKSdk.initialize(this);
        VKAccessToken vkToken = VKAccessToken.currentToken();
        updateVKTokenState(vkToken);

        FacebookSdk.sdkInitialize(this, new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                fbAccessTokenTracker = new AccessTokenTracker(){
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                        updateFBTokenState(currentAccessToken);
                        sendStatusChangedBroadcast(Constants.FB_NETWORK_NAME, fbState);
                    }
                };
                fbAccessTokenTracker.startTracking();
                updateFBTokenState(AccessToken.getCurrentAccessToken());
                sendStatusChangedBroadcast(Constants.FB_NETWORK_NAME, fbState);
            }
        });
    }

    private void sendStatusChangedBroadcast(String networkName, TOKEN_STATE status){
        Intent intent = new Intent(BaseActivity.ACTION_SOCIAL_STATUS_CHANGED);
        intent.putExtra(BaseActivity.NETWORK_NAME, networkName);
        intent.putExtra(BaseActivity.TOKEN_STATUS, status);
        sendBroadcast(intent);
    }

    public TOKEN_STATE getVKTokenState(){
        return vkState;
    }

    public TOKEN_STATE getFBTokenState(){
        return fbState;
    }

    public static SocialContactsApplication getInstance(){
        return instance;
    }

    private void updateVKTokenState(VKAccessToken newToken){
        if (newToken == null) {
            vkState = TOKEN_STATE.absent;
        }
        else{
            vkState = TOKEN_STATE.fine;
        }
    }

    private void updateFBTokenState(AccessToken newToken){
        if (newToken == null) {
            fbState = TOKEN_STATE.absent;
        }
        else{
            fbState = TOKEN_STATE.fine;
        }
    }
}
