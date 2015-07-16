package org.masonapps.materialize3d;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.masonapps.materialize3d.graphics.GLSurface;
import org.masonapps.materialize3d.graphics.MyRenderer;
import org.masonapps.materialize3d.graphics.effects.BaseEffect;
import org.masonapps.materialize3d.utils.BitmapHelper;
import org.masonapps.materialize3d.utils.Constants;
import org.masonapps.materialize3d.utils.ImageProcessing;
import org.masonapps.materialize3d.wallpaper.MyWallpaperService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class SettingsActivity extends AppCompatActivity{

    private static final int WALLPAPER_REQUEST_CODE = 1001;
    private static final String COLOR_FRAGMENT_TAG = "colorFragment";
    private Handler handler;
    private DrawerLayout drawerLayout;
    private View drawerView;
    private ActionBarDrawerToggle drawerToggle;
    private GLSurface glSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Prefs.getInstance().load(this);

        setContentView(R.layout.activity_settings);
        glSurface = (GLSurface) findViewById(R.id.glsurface);
        glSurface.setScreenshotListener(new MyRenderer.ScreenshotListener() {
            @Override
            public void screenshotDone(Bitmap bitmap) {
                saveScreenshot(bitmap);
            }
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handler = new Handler();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawer_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_closed) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);

        findViewById(R.id.colorButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(COLOR_FRAGMENT_TAG);
                if (fragment != null && fragment.isVisible()) {
                    setColorPickerVisibility(false);
                } else {
                    setColorPickerVisibility(true);
                }
            }
        });
        
        setupButtons();

        if (Prefs.getInstance().isFirstTime()) drawerLayout.openDrawer(drawerView);
    }

    private void setupButtons() {
        View materialButton = findViewById(R.id.material_btn);
        ((TextView)materialButton.findViewById(R.id.label_text)).setText("Material");
        ((ImageView)materialButton.findViewById(R.id.icon_imageview)).setImageResource(android.R.drawable.ic_menu_gallery);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_options, MaterialListFragment.newInstance()).commit();
                v.setBackgroundColor(getResources().getColor(R.color.primary));
            }
        });
        View colorBtn = findViewById(R.id.color_btn);
        ((TextView)colorBtn.findViewById(R.id.label_text)).setText("Color");
        ((ImageView)colorBtn.findViewById(R.id.icon_imageview)).setImageResource(android.R.drawable.ic_menu_gallery);
        View viewButton = findViewById(R.id.view_btn);
        ((TextView)viewButton.findViewById(R.id.label_text)).setText("View");
        ((ImageView)viewButton.findViewById(R.id.icon_imageview)).setImageResource(android.R.drawable.ic_menu_gallery);
        View otherButton = findViewById(R.id.other_btn);
        ((TextView)otherButton.findViewById(R.id.label_text)).setText("Other");
        ((ImageView)otherButton.findViewById(R.id.icon_imageview)).setImageResource(android.R.drawable.ic_menu_gallery);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurface.onResume();
    }

    @Override
    protected void onPause() {
        Prefs.getInstance().save(this);
        glSurface.onPause();
        setColorPickerVisibility(false);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == WALLPAPER_REQUEST_CODE) {
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        if (id == R.id.action_wallpapers) {
            final Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(
                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(SettingsActivity.this,
                                MyWallpaperService.class));
            } else {
                intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
            }
            startActivityForResult(intent, WALLPAPER_REQUEST_CODE);
            return true;
        } else if (id == R.id.action_select_image) {
            final Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ImageSelectActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_item_share) {
            takeScreenshot();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setColorPickerVisibility(boolean visible){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if(visible){
            fragmentManager.beginTransaction().replace(R.id.colorContainer, ColorFragment.newInstance(Prefs.getInstance().getColor()), COLOR_FRAGMENT_TAG).commit();
        }else{
            Fragment fragment = fragmentManager.findFragmentByTag(COLOR_FRAGMENT_TAG);
            if(fragment != null){
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }
    }

    public void saveScreenshot(final Bitmap bitmap) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                new SaveScreenshotTask().execute(bitmap);
            }
        });
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(drawerView);
    }

    public void takeScreenshot() {
        glSurface.takeScreenShot();
    }

    private class SaveScreenshotTask extends AsyncTask<Bitmap, Void, Uri> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(SettingsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setIndeterminate(true);
            dialog.setMessage(getString(R.string.loading_screenshot));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Uri doInBackground(Bitmap... params) {
            final Bitmap bitmap = params[0];
            prepareBitmap(bitmap);
            File dirFile = new File(getFilesDir(), "images");
            if (!dirFile.exists()) dirFile.mkdirs();
            File imgFile = new File(dirFile, Constants.FILENAME_SHARED_IMAGE);
            FileOutputStream stream = null;
            Uri uri = null;
            try {
                stream = new FileOutputStream(imgFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                uri = FileProvider.getUriForFile(SettingsActivity.this, getPackageName() + ".fileprovider", imgFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.flush();
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                bitmap.recycle();
            }
            return uri;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (dialog != null)
                dialog.dismiss();
            if (uri != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name));
                startActivity(Intent.createChooser(intent, getString(R.string.share_string)));
            } else {
                Toast.makeText(getApplicationContext(), "unable to create screenshot", Toast.LENGTH_LONG).show();
            }
        }

        private void prepareBitmap(Bitmap bitmap) {
            ImageProcessing.flipY(bitmap);
            final float margin = 10f;
            final Canvas c = new Canvas(bitmap);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher, options);
            options.inSampleSize = BitmapHelper.calculateInSampleSize(options, 64, 64);
            options.inJustDecodeBounds = false;
            final Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher, options);
            final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextAlign(Paint.Align.LEFT);

            final Paint paint = new Paint();
            paint.setColor(0xAA2B2B2B);

            final String appName = getResources().getString(R.string.app_name);
            final BaseEffect effect = Prefs.getInstance().getEffect();
            final String effectName = effect != null ? effect.getEffectName() : "";
            final int textSize = bitmap.getWidth() / 24;
            final int textSizeSmall = 3 * textSize / 4;
            textPaint.setTextSize(textSize);
            float appNameWidth = textPaint.measureText(appName);
            textPaint.setTextSize(textSizeSmall);
            float effectNameWidth = textPaint.measureText(effectName);
            float left = c.getWidth() - Math.max(appNameWidth, effectNameWidth) - margin;
            float h = textSize + textSizeSmall + margin;
            final float bottom = c.getHeight() - margin;
            RectF iconRect = new RectF(left - h - margin, bottom - h, left - margin, bottom);

            c.drawRect(iconRect.left - margin, iconRect.top - margin, c.getWidth(), c.getHeight(), paint);

            textPaint.setTextSize(textSize);
            c.drawText(appName, left, bottom - textSizeSmall - textPaint.descent(), textPaint);

            textPaint.setTextSize(20);
            c.drawText(effectName, left, bottom - textPaint.descent(), textPaint);

            c.drawBitmap(icon, null, iconRect, new Paint(Paint.FILTER_BITMAP_FLAG));
            icon.recycle();
        }
    }
}
