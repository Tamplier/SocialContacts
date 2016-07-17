package com.alextinekov.socialcontacts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alextinekov.socialcontacts.socialcallbacks.AuthCallbacks;
import com.alextinekov.socialcontacts.socialcallbacks.AuthCallbacksListener;
import com.alextinekov.socialcontacts.utils.DialogHelper;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.vk.sdk.VKSdk;

public class MainActivity extends BaseActivity implements View.OnClickListener, AuthCallbacksListener{
    private Button vkAuth;
    private TextView vkStatus;
    private LoginButton fbAuth;
    private TextView fbStatus;
    private Button continueButton;
    private CallbackManager callbackManager;
    private AuthCallbacks authCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        vkAuth = (Button)findViewById(R.id.vk_login);
        vkStatus = (TextView)findViewById(R.id.vk_status);

        fbAuth = (LoginButton)findViewById(R.id.fb_login);
        fbStatus = (TextView)findViewById(R.id.fb_status);

        continueButton = (Button)findViewById(R.id.next_activity);

        fbAuth.setReadPermissions(Constants.FB_SCOPES);

        vkAuth.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        authCallbacks = new AuthCallbacks();
        authCallbacks.setCalbacksListeer(this);
        callbackManager = CallbackManager.Factory.create();
        fbAuth.registerCallback(callbackManager, authCallbacks);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SocialContactsApplication.TOKEN_STATE vkTokenState = SocialContactsApplication.getInstance().getVKTokenState();
        if(vkTokenState == SocialContactsApplication.TOKEN_STATE.fine){
            hideLoginVK();
        }
        else if(vkTokenState == SocialContactsApplication.TOKEN_STATE.absent){
            showLoginVK();
        }
        SocialContactsApplication.TOKEN_STATE fbState = SocialContactsApplication.getInstance().getFBTokenState();
        if(fbState == SocialContactsApplication.TOKEN_STATE.fine){
            hideLoginFB();
        }
        else if(fbState == SocialContactsApplication.TOKEN_STATE.absent){
            showLoginFB();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == vkAuth)
            VKSdk.login(this, Constants.VK_SCOPES);
        else if(v == continueButton){
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, authCallbacks)
                && !callbackManager.onActivityResult(requestCode, resultCode, data))
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void hideLoginVK(){
        vkAuth.setVisibility(View.GONE);
        vkAuth.setEnabled(false);
        vkStatus.setVisibility(View.VISIBLE);
        vkStatus.setText(getResources().getString(R.string.vk_succeeded));
    }

    private void hideLoginFB(){
        fbAuth.setVisibility(View.GONE);
        fbAuth.setEnabled(false);
        fbStatus.setVisibility(View.VISIBLE);
        fbStatus.setText(getResources().getString(R.string.fb_succeeded));

    }

    private void showLoginVK(){
        vkAuth.setVisibility(View.VISIBLE);
        vkAuth.setEnabled(true);
        vkStatus.setVisibility(View.GONE);
    }

    private void showLoginFB(){
        fbAuth.setVisibility(View.VISIBLE);
        fbAuth.setEnabled(true);
        fbStatus.setVisibility(View.GONE);
    }

    @Override
    public void onError(String errorMessage) {
        DialogHelper.showErrorDialog(MainActivity.this, getResources().getString(R.string.auth_error), errorMessage);
    }

    @Override
    public void onSuccess(AuthCallbacks.NETWORKS network) {
        switch (network){
            case FB:
                hideLoginFB();
                break;
            case VK:
                hideLoginVK();
                break;
        }

    }

    @Override
    protected void onSocialConnectionStateChanged(String networkName, SocialContactsApplication.TOKEN_STATE tokenState) {
        super.onSocialConnectionStateChanged(networkName, tokenState);

        if(networkName.equals(Constants.VK_NETWORK_NAME)){
            if(tokenState == SocialContactsApplication.TOKEN_STATE.fine)
                hideLoginVK();
            else
                showLoginVK();
        }
        else if(networkName.equals(Constants.FB_NETWORK_NAME)){
            if(tokenState == SocialContactsApplication.TOKEN_STATE.fine)
                hideLoginFB();
            else
                showLoginFB();
        }
    }
}
