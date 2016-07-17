package com.alextinekov.socialcontacts.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alextinekov.socialcontacts.data.Contact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Tinekov on 16.07.2016.
 */
public class CachingHelper {
    private static final String TAG = CachingHelper.class.getSimpleName();
    private static final String CACHED_CONTACTS = "cached_contacts";
    private static final String SETTINGS = "settings";
    private static final String IS_FIRST_LAUNCH = "is_first_launch";
    private static final String LAST_SYNC_TIME = "last_sync_time";

    public static final void saveContacts(Context ctx, List<Contact> contacts){
        if(contacts != null && contacts.size() > 0) {
            SharedPreferences pref = ctx.getSharedPreferences(CACHED_CONTACTS, ctx.MODE_PRIVATE);
            String name = contacts.get(0).network;
            String value = new Gson().toJson(contacts);
            SharedPreferences.Editor edit = pref.edit();
            edit.putString(name, value);
            edit.commit();
        }
    }

    public static final Map<String, List<Contact>> getCachedContacts(Context ctx){
        SharedPreferences pref = ctx.getSharedPreferences(CACHED_CONTACTS, ctx.MODE_PRIVATE);
        Map<String, ?> cached = pref.getAll();
        Map<String, List<Contact>> contactsSeparated = new HashMap<>();
        for(Map.Entry<String, ?> entry:  cached.entrySet()){
            String network = entry.getKey();
            List<Contact> contacts = new Gson().fromJson(entry.getValue().toString(), new TypeToken<ArrayList<Contact>>(){}.getType());
            contactsSeparated.put(network, contacts);
        }

        return contactsSeparated;
    }

    public static boolean isFirstLaunch(Context ctx){
        SharedPreferences pref = ctx.getApplicationContext().getSharedPreferences(SETTINGS, ctx.MODE_PRIVATE);
        boolean fLaunch = pref.getBoolean(IS_FIRST_LAUNCH, true);
        if(fLaunch){
            SharedPreferences.Editor edit = pref.edit();
            edit.putBoolean(IS_FIRST_LAUNCH, false);
            edit.commit();
        }
        return fLaunch;
    }

    public static void saveLastSyncTime(Context ctx, long millis){
        SharedPreferences pref = ctx.getApplicationContext().getSharedPreferences(SETTINGS, ctx.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putLong(LAST_SYNC_TIME, millis);
        edit.commit();
    }

    public static long loadLastSyncTime(Context ctx){
        SharedPreferences pref = ctx.getApplicationContext().getSharedPreferences(SETTINGS, ctx.MODE_PRIVATE);
        return pref.getLong(LAST_SYNC_TIME, 0);
    }

    public static String convertMillisToDatetime(long millis){
        if(millis == 0)
            return "-:-";

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return formatter.format(c.getTime());
    }
}
