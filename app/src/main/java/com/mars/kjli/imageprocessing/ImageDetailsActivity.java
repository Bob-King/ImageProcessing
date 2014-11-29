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
        Uri uri = Uri.parse(getIntent().getStringExtra(ImageProcessingActivity.EXTRA_IMAGE_URL));
        ((TextView) findViewById(R.id.image_url)).setText(Utils.getFileUri(getApplicationContext(), uri).toString());

        ((TextView) findViewById(R.id.image_type)).setText(getIntent().getStringExtra(ImageProcessingActivity.EXTRA_IMAGE_TYPE));

        ((TextView) findViewById(R.id.image_resolution)).setText(
                formatImageResolution(
                        getIntent().getIntExtra(ImageProcessingActivity.EXTRA_IMAGE_WIDTH, 0),
                        getIntent().getIntExtra(ImageProcessingActivity.EXTRA_IMAGE_HEIGHT, 0)));
    }

    private String formatImageResolution(int width, int height) {
        return new StringBuilder().append(width).append("x").append(height).toString();
    }
}
