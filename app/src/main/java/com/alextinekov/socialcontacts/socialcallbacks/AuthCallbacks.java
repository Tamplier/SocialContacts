package com.alextinekov.socialcontacts.socialcallbacks;

import android.content.Context;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.api.VKError;

/**
 * Created by Alex Tinekov on 14.07.2016.
 */
public class AuthCallbacks implements VKCallback<VKAccessToken>, FacebookCallback<LoginResult> {
    public enum NETWORKS {VK, FB};
    private AuthCallbacksListener listener;

    public AuthCallbacks(){
    }

    public void setCalbacksListeer(AuthCallbacksListener listener){
        this.listener = listener;
    }

    @Override
    public void onResult(VKAccessToken res) {
        if(listener != null) listener.onSuccess(NETWORKS.VK);
    }

    @Override
    public void onError(VKError error) {
        if(listener != null) listener.onError(error.errorMessage);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        if(listener != null) listener.onSuccess(NETWORKS.FB);
    }

    @Override
    public void onCancel() {
        if(listener != null) listener.onError("");
    }

    @Override
    public void onError(FacebookException error) {
        if(listener != null) listener.onError(error.getMessage());
    }
}
