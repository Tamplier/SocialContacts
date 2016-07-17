package com.alextinekov.socialcontacts.socialcallbacks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Asus on 17.07.2016.
 */
public class PhotoLoader extends AsyncTask<Void, Void, Bitmap> {
    private static final String TAG = PhotoLoader.class.getSimpleName();

    private String url;
    private ImageView target;

    public PhotoLoader(String url, ImageView target){
        this.url = url;
        this.target = target;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap photo = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            photo = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(TAG, ""+e.getMessage());
            e.printStackTrace();
        }
        return photo;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null && target != null){
            target.setImageBitmap(bitmap);
        }
    }
}
