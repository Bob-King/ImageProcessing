package com.mars.kjli.imageprocessing;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;


public class ImageProcessingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
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
                    /*
                    Uri uri = data.getData();
                    final String[] projection = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getApplicationContext().getContentResolver().query(data.getData(), projection, null, null, null);
                    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    mBitmap = BitmapFactory.decodeFile(cursor.getString(index));
                    */
                    try {
                        mUri = data.getData();
                        mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mUri);
                        ImageView imageView = (ImageView) findViewById(R.id.image_view);
                        imageView.setImageBitmap(mBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static final String EXTRA_DETAILS = "com.mars.kjli.imageprocessing.DETAILS";

    private Uri mUri;
    private Bitmap mBitmap;

    private static final int SELECT_IMAGE_REQUEST = 0;
}
