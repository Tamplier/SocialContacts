package com.alextinekov.socialcontacts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.alextinekov.socialcontacts.data.Contact;
import com.alextinekov.socialcontacts.data.ContactsAdapter;
import com.alextinekov.socialcontacts.socialcallbacks.GetUsersCallbacks;
import com.alextinekov.socialcontacts.socialcallbacks.GetUsersCallbacksListener;
import com.alextinekov.socialcontacts.utils.CachingHelper;
import com.alextinekov.socialcontacts.utils.DialogHelper;
import com.alextinekov.socialcontacts.utils.RecyclerItemClickListener;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

/**
 * Created by Alex Tinekov on 15.07.2016.
 */
public class ContactsActivity extends BaseActivity implements GetUsersCallbacksListener, CompoundButton.OnCheckedChangeListener{
    private static final String SETTINGS = "settings";
    private static final String IS_FIRST_LAUNCH = "is_first_launch";
    private static final String LAST_SYNC_TIME = "last_sync_time";

    private RecyclerView contactsList;
    private RecyclerView.LayoutManager layoutManager;
    private GetUsersCallbacks callbacks;
    private ContactsAdapter adapter;
    private Switch vkSwitch;
    private Switch fbSwitch;
    private TextView syncTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_layout);
        init();
    }

    private void init(){
        if(CachingHelper.isFirstLaunch(this)){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }

        contactsList = (RecyclerView)findViewById(R.id.contacts_list);
        vkSwitch = (Switch)findViewById(R.id.vk_contacts);
        fbSwitch = (Switch)findViewById(R.id.fb_contacts);
        syncTime = (TextView)findViewById(R.id.sync_time);

        vkSwitch.setOnCheckedChangeListener(this);
        fbSwitch.setOnCheckedChangeListener(this);

        layoutManager = new LinearLayoutManager(this);
        contactsList.setLayoutManager(layoutManager);

        adapter = new ContactsAdapter(null, this);
        contactsList.setAdapter(adapter);

        contactsList.addOnItemTouchListener(
                new RecyclerItemClickListener(this, contactsList, new RecyclerItemClickListener.OnItemClickListener(){

                    @Override
                    public void onItemClick(View view, int position) {
                        Contact c = adapter.getContactOnPosition(position);
                        Intent intent = new Intent(ContactsActivity.this, ContactInfoActivity.class);
                        intent.putExtra(ContactInfoActivity.NAME, c.name);
                        intent.putExtra(ContactInfoActivity.BDAY, c.bday);
                        intent.putExtra(ContactInfoActivity.EMAIL, c.email);
                        intent.putExtra(ContactInfoActivity.PHONE, c.phone);
                        intent.putExtra(ContactInfoActivity.PHOTO_URL, c.photoUrl);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

        callbacks = new GetUsersCallbacks();
        callbacks.setListener(this);

        long syncTime = CachingHelper.loadLastSyncTime(this);
        setSyncTime(CachingHelper.convertMillisToDatetime(syncTime));
        loadCachedContacts();

        syncContactsList();
    }

    private void loadCachedContacts(){
        Map<String, List<Contact>> cachedContacts = CachingHelper.getCachedContacts(this);
        for(Map.Entry<String, List<Contact>> entry: cachedContacts.entrySet()){
            adapter.addContacts(entry.getValue());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.social_auth:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;
            case R.id.sync:
                syncContactsList();
                return true;
            case R.id.sorting:
                adapter.changeSorting();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void syncContactsList(){
        if(SocialContactsApplication.getInstance().getVKTokenState() == SocialContactsApplication.TOKEN_STATE.fine) {
            VKRequest friendsRequest = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, Constants.VK_FIELDS));
            friendsRequest.executeWithListener(callbacks);
        }

        if(SocialContactsApplication.getInstance().getFBTokenState() == SocialContactsApplication.TOKEN_STATE.fine) {
            //facebook can return only those friends who use same application.
            //other way is to use /me/taggable_friends, but this permission requires review by fb
            GraphRequest request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), callbacks);
            Bundle parameters = new Bundle();
            parameters.putString("fields", Constants.FB_FIELDS);
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    @Override
    public void onError(String title, String message) {
        DialogHelper.showErrorDialog(this, title, message);
    }

    @Override
    public void onSuccess(List<Contact> contacts) {
        adapter.addContacts(contacts);
        CachingHelper.saveContacts(this, contacts);
        long syncTime = System.currentTimeMillis();
        CachingHelper.saveLastSyncTime(this, syncTime);
        setSyncTime(CachingHelper.convertMillisToDatetime(syncTime));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String network = buttonView == vkSwitch ? Constants.VK_NETWORK_NAME : Constants.FB_NETWORK_NAME;
        adapter.changeVisibilityForNetworkContacts(isChecked, network);
    }

    private void setSyncTime(String time){
        String s = getResources().getString(R.string.sync_time);
        s = String.format(s, time);
        syncTime.setText(s);
    }

}
