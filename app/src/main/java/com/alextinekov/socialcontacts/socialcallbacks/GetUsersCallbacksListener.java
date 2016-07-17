package com.alextinekov.socialcontacts.socialcallbacks;

import com.alextinekov.socialcontacts.data.Contact;

import java.util.List;

/**
 * Created by Alex Tinekov on 15.07.2016.
 */
public interface GetUsersCallbacksListener {
    public void onError(String title, String message);
    public void onSuccess(List<Contact> contacts);
}
