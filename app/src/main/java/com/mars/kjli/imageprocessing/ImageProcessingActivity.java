package com.mars.kjli.imageprocessing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.IOException;


public class ImageProcessingActivity extends Activity {

    public static final String EXTRA_DETAILS = "com.mars.kjli.imageprocessing.DETAILS";
    private static final String KEY_IMAGE_URI = "IMAGE_URI";
    private static final String TAG = ImageProcessingActivity.class.getCanonicalName();
    private static final int SELECT_IMAGE_REQUEST = 0;
    private Uri mUri;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        if (savedInstanceState != null) {
            String uri = savedInstanceState.getString(KEY_IMAGE_URI);
            if (uri != null) {
                loadImageAsync(Uri.parse(uri));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mUri != null) {
            outState.putString(KEY_IMAGE_URI, mUri.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_processing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_open:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_activity_title)),
                        SELECT_IMAGE_REQUEST);
                return true;

            case R.id.action_details:
                intent = new Intent(this, ImageDetailsActivity.class);
                intent.putExtra(EXTRA_DETAILS, mUri.toString());
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_IMAGE_REQUEST:
                if (resultCode == RESULT_OK) {
                    loadImageAsync(data.getData());
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadImageAsync(Uri uri) {
        mUri = uri;
        new LoadImageTask().execute(uri);
    }

    private class LoadImageTask extends AsyncTask<Uri, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(Uri... uris) {
            try {
                return MediaStore.Images.Media.getBitmap(getContentResolver(), uris[0]);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mBitmap = bitmap;
            ImageView imageView = (ImageView) findViewById(R.id.image_view);
            imageView.setImageBitmap(mBitmap);
        }
    }
}
