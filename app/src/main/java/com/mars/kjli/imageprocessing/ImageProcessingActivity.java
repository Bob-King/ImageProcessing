package com.mars.kjli.imageprocessing;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageProcessingActivity extends Activity implements ImageFilterFactorPickerDialogFragment.ImageFilterFactorPickerDialogListener {

    public static final String EXTRA_IMAGE_URL = "com.mars.kjli.imageprocessing.IMAGE_URL";
    public static final String EXTRA_IMAGE_TYPE = "com.mars.kjli.imageprocessing.IMAGE_TYPE";
    public static final String EXTRA_IMAGE_WIDTH = "com.mars.kjli.imageprocessing.IMAGE_WIDTH";
    public static final String EXTRA_IMAGE_HEIGHT = "com.mars.kjli.imageprocessing.IMAGE_HEIGHT";
    private static final String KEY_IMAGE_URI = "IMAGE_URI";
    private static final String TAG = ImageProcessingActivity.class.getCanonicalName();
    private static final int SELECT_IMAGE_REQUEST = 0;
    private Uri mUri;
    private Bitmap mBitmap;
    private State mState = State.Uninitialized;

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
                if (mState != State.Working) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_activity_title)),
                            SELECT_IMAGE_REQUEST);
                }
                return true;

            case R.id.action_details:
                if (mState == State.Idle) {
                    intent = new Intent(this, ImageDetailsActivity.class);
                    intent.putExtra(EXTRA_IMAGE_URL, mUri.toString());
                    intent.putExtra(EXTRA_IMAGE_TYPE, mBitmap.getConfig().toString());
                    intent.putExtra(EXTRA_IMAGE_WIDTH, mBitmap.getWidth());
                    intent.putExtra(EXTRA_IMAGE_HEIGHT, mBitmap.getHeight());
                    startActivity(intent);
                }
                return true;

            case R.id.action_histogram_equalize:
                if (mState == State.Idle) {
                    new HistogramEqualizeTask().execute(mBitmap);
                }
                return true;

            case R.id.action_laplace_filter:
                if (mState == State.Idle) {
                    DialogFragment dialogFragment = new ImageFilterFactorPickerDialogFragment();
                    dialogFragment.show(getFragmentManager(), "image_filter_factor_picker");
                    return true;
                }
                return true;

            case R.id.action_about:
                if (mState != State.Working) {
                    intent = new Intent(this, AboutActivity.class);
                    startActivity(intent);
                }
                return true;

            case R.id.action_save:
                if (mState == State.Idle) {
                    saveBitmap();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveBitmap() {
        FileOutputStream out = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "D" + simpleDateFormat.format(new Date()) + ".jpg";
            Log.d(TAG, "Path to save image: " + path);
            out = new FileOutputStream(path);
            if (mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
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
        Log.d(TAG, "Load image from " + uri.toString());
        new LoadImageTask().execute(uri);
    }

    @Override
    public void OnFactorSelected(float factor) {
        final int SCALE = 10;

        int f = (int) (factor * SCALE) + 8 * SCALE;
        int b = 1;

        if ((f & 0xff) != 0) {
            b = 10;
        } else {
            f /= 10;
        }

        new ImageFilterTask(new int[][]
                {
                        {-b, -b, -b},
                        {-b, f, -b},
                        {-b, -b, -b}
                }).execute(mBitmap);
    }

    private static enum State {
        Uninitialized, Idle, Working
    }

    private class LoadImageTask extends AsyncTask<Uri, Integer, Bitmap> {
        @Override
        protected void onPreExecute() {
            mState = State.Working;
        }

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
            if (bitmap != null) {
                mBitmap = bitmap;
                ImageView imageView = (ImageView) findViewById(R.id.image_view);
                imageView.setImageBitmap(mBitmap);
            }

            mState = mBitmap != null ? State.Idle : State.Uninitialized;
        }
    }

    private abstract class ImageProcessingTask extends AsyncTask<Bitmap, Integer, Bitmap> {
        @Override
        protected void onPreExecute() {
            mState = State.Working;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                mBitmap = bitmap;
                ImageView imageView = (ImageView) findViewById(R.id.image_view);
                imageView.setImageBitmap(mBitmap);
            }
            mState = State.Idle;
        }
    }

    private class HistogramEqualizeTask extends ImageProcessingTask {
        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = Bitmap.createBitmap(bitmaps[0].getWidth(), bitmaps[0].getHeight(), Bitmap.Config.ARGB_8888);
            int[][] gls = new int[bitmap.getWidth()][bitmap.getHeight()];
            for (int x = 0; x != gls.length; ++x) {
                for (int y = 0; y != gls[x].length; ++y) {
                    gls[x][y] = ImageLibrary.rgb2Gray(bitmaps[0].getPixel(x, y));
                }
            }

            ImageLibrary.histogramEqualize(gls);

            for (int x = 0; x != gls.length; ++x) {
                for (int y = 0; y != gls[x].length; ++y) {
                    gls[x][y] = ImageLibrary.gray2Color(gls[x][y]);
                    bitmap.setPixel(x, y, gls[x][y]);
                }
            }

            return bitmap;
        }
    }

    private class ImageFilterTask extends ImageProcessingTask {
        private final int[][] mFilter;

        ImageFilterTask(int[][] filter) {
            mFilter = filter;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = Bitmap.createBitmap(bitmaps[0].getWidth(), bitmaps[0].getHeight(), Bitmap.Config.ARGB_8888);
            int[][] gls = new int[bitmap.getWidth()][bitmap.getHeight()];
            for (int x = 0; x != gls.length; ++x) {
                for (int y = 0; y != gls[x].length; ++y) {
                    gls[x][y] = ImageLibrary.rgb2Gray(bitmaps[0].getPixel(x, y));
                }
            }

            gls = ImageLibrary.imageFilter(gls, mFilter);

            for (int x = 0; x != gls.length; ++x) {
                for (int y = 0; y != gls[x].length; ++y) {
                    // Log.d(TAG, "<PIXELS>gls[" + x + "][" + y + "] = " + Integer.toHexString(gls[x][y]));
                    gls[x][y] = ImageLibrary.gray2Color(gls[x][y]);
                    // Log.d(TAG, ">PIXELS<gls[" + x + "][" + y + "] = " + Integer.toHexString(gls[x][y]));
                    bitmap.setPixel(x, y, gls[x][y]);
                }
            }

            return bitmap;
        }
    }
}
