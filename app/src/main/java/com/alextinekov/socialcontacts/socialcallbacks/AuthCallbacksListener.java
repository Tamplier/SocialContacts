package com.alextinekov.socialcontacts.socialcallbacks;

/**
 * Created by Alex Tinekov on 14.07.2016.
 */
public interface AuthCallbacksListener {
    public void onError(String errorMessage);
    public void onSuccess(AuthCallbacks.NETWORKS network);
}
