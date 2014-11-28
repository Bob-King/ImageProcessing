package com.mars.kjli.imageprocessing;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by King on 2014/11/23.
 */
public abstract class Utils {

    public static final String TAG = Utils.class.getCanonicalName();

    public static Uri getFileUri(Context context, Uri uri) {
        try {
            Log.d(TAG, uri.toString());
            Log.d(TAG, uri.getScheme() + " + " + uri.getPath());
            final String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return Uri.parse(cursor.getString(index));
        } catch (Exception e) {
            // Log.w(TAG, e.getMessage());
            return uri;
        }
    }

}
