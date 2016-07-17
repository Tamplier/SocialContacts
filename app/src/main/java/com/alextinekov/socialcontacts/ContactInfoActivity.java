package com.alextinekov.socialcontacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alextinekov.socialcontacts.data.Contact;
import com.alextinekov.socialcontacts.socialcallbacks.PhotoLoader;
import com.alextinekov.socialcontacts.utils.DialogHelper;

/**
 * Created by Alex TInekov on 17.07.2016.
 */
public class ContactInfoActivity extends BaseActivity implements View.OnClickListener{

    public static final String PHOTO_URL = "photo_url";
    public static final String NAME = "name";
    public static final String BDAY = "bday";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";

    private ImageView photo;
    private TextView name;
    private TextView bday;
    private TextView email;
    private TextView phone;
    private Contact currentContact;

    private PhotoLoader photoLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_info_layout);

        init();
    }

    private void init(){
        photo = (ImageView)findViewById(R.id.avatar);
        name = (TextView)findViewById(R.id.name);
        bday = (TextView)findViewById(R.id.bday);
        email = (TextView)findViewById(R.id.email);
        phone = (TextView)findViewById(R.id.phone);

        photo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.anonymous, null));
        Intent intent = getIntent();
        currentContact = new Contact(null, intent.getStringExtra(NAME));

        currentContact.bday = intent.getStringExtra(BDAY);
        currentContact.email = intent.getStringExtra(EMAIL);
        currentContact.phone = intent.getStringExtra(PHONE);
        currentContact.photoUrl = intent.getStringExtra(PHOTO_URL);

        photoLoader = new PhotoLoader(currentContact.photoUrl, photo);
        photoLoader.execute();

        setTextToView(name, currentContact.name, -1);
        setTextToView(bday, currentContact.bday, R.string.bday);
        setTextToView(email, currentContact.email, R.string.email);
        setTextToView(phone, currentContact.phone, R.string.phone);

        phone.setOnClickListener(this);
    }

    private void setTextToView(TextView v, String text, int formatID){
        String finalString = null;
        if(TextUtils.isEmpty(text)){
            text = "-";
        }

        if(formatID >= 0)
        {
            String format = getResources().getString(formatID);
            finalString = String.format(format, text);
        }
        else
            finalString = text;
        v.setText(finalString);
    }

    @Override
    public void onClick(View v) {
        if(v == phone){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) ==
                    PackageManager.PERMISSION_GRANTED && currentContact.phone != null)
            {
                String number = currentContact.phone.replace("-", "");
                if (PhoneNumberUtils.isGlobalPhoneNumber(number)) {
                    Intent intent = new Intent(Intent.ACTION_CALL);

                    intent.setData(Uri.parse("tel:" + number));
                    startActivity(intent);
                }
                else
                    DialogHelper.showErrorDialog(this, getResources().getString(R.string.wrong_phone_format), "");
            }
            else
                requestMultiplePermissions();
        }
    }

    public void requestMultiplePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.CALL_PHONE,
                },
                0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(photoLoader.getStatus() != AsyncTask.Status.FINISHED)
            photoLoader.cancel(true);
        else {
            Drawable drawable = photo.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                bitmap.recycle();
            }
        }
    }
}
