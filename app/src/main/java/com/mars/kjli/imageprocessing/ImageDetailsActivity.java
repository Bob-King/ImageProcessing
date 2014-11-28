package com.mars.kjli.imageprocessing;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;


public class ImageDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);
        Uri uri = Uri.parse(getIntent().getStringExtra(ImageProcessingActivity.EXTRA_DETAILS));
        TextView textView = (TextView) findViewById(R.id.image_url);
        textView.setText(Utils.getFileUri(getApplicationContext(), uri).toString());
    }
}
