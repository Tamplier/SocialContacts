package com.alextinekov.socialcontacts.socialcallbacks;

import android.text.TextUtils;
import android.util.Log;

import com.alextinekov.socialcontacts.Constants;
import com.alextinekov.socialcontacts.data.Contact;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Alex Tinekov on 15.07.2016.
 */
public class GetUsersCallbacks extends VKRequest.VKRequestListener implements GraphRequest.GraphJSONArrayCallback {
    private static final String TAG = GetUsersCallbacks.class.getSimpleName();
    private GetUsersCallbacksListener listener;

    public void setListener(GetUsersCallbacksListener listener){
        this.listener = listener;
    }

    @Override
    public void onComplete(VKResponse response) {
        super.onComplete(response);
        VKUsersArray friends = (VKUsersArray) response.parsedModel;
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        for(VKApiUserFull friend : friends){
            if(!friend.is_deleted && !friend.is_banned) {
                String name = friend.first_name + " " + friend.last_name;
                Contact contact = new Contact(Constants.VK_NETWORK_NAME, name);
                contact.bday = friend.bdate;
                contact.photoUrl = friend.photo_max_orig;
                contact.phone = TextUtils.isEmpty(friend.mobile_phone) ? friend.home_phone : friend.mobile_phone;
                contacts.add(contact);
            }
        }
        listener.onSuccess(contacts);
    }

    @Override
    public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
        super.attemptFailed(request, attemptNumber, totalAttempts);
    }

    @Override
    public void onError(VKError error) {
        super.onError(error);
        listener.onError("", error.errorMessage);
    }

    @Override
    public void onCompleted(JSONArray objects, GraphResponse response) {
        String errorMessage = null;
        Log.i(TAG, ""+response);
        try {
            if(response.getError() == null) {
                Log.i(TAG, "success");
                ArrayList<Contact> contacts = new ArrayList<Contact>();
                for (int i = 0; i < objects.length(); i++) {
                    JSONObject friend = objects.getJSONObject(i);
                    Contact contact = new Contact(Constants.FB_NETWORK_NAME, friend.getString("name"));
                    contact.bday = friend.getString("birthday");
                    contact.email = friend.has("email") ? friend.getString("email") : null;
                    String url = null;
                    try{
                        url = friend.getJSONObject("picture").getJSONObject("data").getString("url");
                    }catch (JSONException e){e.printStackTrace();}
                    contact.photoUrl = url;

                    contacts.add(contact);
                    Log.i(TAG, contact.toString());
                }
                listener.onSuccess(contacts);
                return;
            }
            else
                errorMessage = response.getError().getErrorMessage();
        }
        catch (JSONException e){
            Log.e(TAG, e.getMessage());
            errorMessage = e.getMessage();
        }
        listener.onError("", errorMessage);
    }
}
