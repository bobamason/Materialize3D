package org.masonapps.materialize3d;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.Toast;

import org.masonapps.materialize3d.utils.ImageInfo;


public class ImageSelectActivity extends AppCompatActivity implements ThumbnailFragment.OnThumbnailSelectedListener,
        ImageCropFragment.OnCropDoneListener {

    private static final String THUMBNAIL_TAG = "thumbnailFragment";
    private static final String IMAGE_TAG = "imageFragment";
    private boolean isBusyLoading;
    private boolean launchedFromSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null && type.startsWith("image/")) {
                launchedFromSend = true;
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, ImageCropFragment.newInstance(imageUri), IMAGE_TAG)
                            .commit();
                } else {
                    Toast.makeText(this, "unable to open image", Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                launchedFromSend = false;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, ThumbnailFragment.newInstance(), THUMBNAIL_TAG)
                        .commit();
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void thumbnailSelected(ImageInfo info) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, ImageCropFragment.newInstance(info.getUri()), IMAGE_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isBusyLoading) return false;
        if (launchedFromSend) return super.onSupportNavigateUp();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            ThumbnailFragment frag = (ThumbnailFragment) getSupportFragmentManager().findFragmentByTag(THUMBNAIL_TAG);
            if (frag != null) {
                if (frag.blockBackPress()) {
                    return false;
                }
            }
        } else {
            getSupportFragmentManager().popBackStack();
            return false;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (isBusyLoading) return;
        if (launchedFromSend) super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            ThumbnailFragment frag = (ThumbnailFragment) getSupportFragmentManager().findFragmentByTag(THUMBNAIL_TAG);
            if (frag != null) {
                if (frag.blockBackPress()) {
                    return;
                }
            }
        } else {
            getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    private class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {

        private final RectF rect;
        private ProgressDialog dialog;

        private SaveImageTask(RectF rect) {
            super();
            this.rect = rect;
        }

        @Override
        protected void onPreExecute() {
            isBusyLoading = true;
            dialog = new ProgressDialog(ImageSelectActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setIndeterminate(true);
            dialog.setMessage("preparing image...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Bitmap... params) {
            boolean ok = false;
            try {
                ok = Prefs.getInstance().saveImage(getApplicationContext(), params[0], rect, 1024);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            return ok;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (dialog != null)
                dialog.dismiss();
            if (!aBoolean)
                Toast.makeText(ImageSelectActivity.this, "failed to save image", Toast.LENGTH_SHORT).show();
            isBusyLoading = false;
            if (aBoolean) {
                if (launchedFromSend) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                }
            }
            finish();
        }
    }

    @Override
    public void cropDone(Bitmap bitmap, RectF rect) {
        if (!isBusyLoading) {
            new SaveImageTask(rect).execute(bitmap);
        }
    }
}
