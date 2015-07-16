package org.masonapps.materialize3d;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.masonapps.materialize3d.graphics.effects.BaseEffect;
import org.masonapps.materialize3d.graphics.effects.ColorEffect;
import org.masonapps.materialize3d.graphics.effects.ImageEffect;
import org.masonapps.materialize3d.graphics.effects.ResourceTextureEffect;
import org.masonapps.materialize3d.graphics.materials.BumpMapMaterial;
import org.masonapps.materialize3d.utils.Constants;
import org.masonapps.materialize3d.utils.ImageProcessing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

/**
 * Created by ims_2 on 3/26/2015.
 */
public class Prefs {

    private static final String TAG = "Prefs";
    private static final String DEFAULT_KEY = "Image";
    private static final String EFFECT_POS_KEY = "effect_key";
    private static final String FIRST_TIME_KEY = "first_time_key";
    private static final String COLOR_KEY = "color_key";
    private static final String DRAW_MODE_KEY = "draw_mode_key";
    private static final int DEFAULT_COLOR = 0xFFCC3333;
    public static final int DRAW_MODE_PLANE = 0;
    public static final int DRAW_MODE_CUBE = 1;
    public static final int DRAW_MODE_INVERTED_CUBE = 2;
    private static final int DEFAULT_DRAW_MODE = DRAW_MODE_INVERTED_CUBE;
    private static Prefs instance;
    private volatile boolean isLoaded;
    private HashMap<String, BaseEffect> effectHashMap;
    private ArrayList<OnPrefsChangedListener> listeners;
    private String effectKey;
    private volatile int color;
    private boolean isFirstTime;
    private volatile boolean drawingBlocked = false;
    private int drawMode;

    private Prefs() {
        isLoaded = false;

        effectKey = DEFAULT_KEY;
        effectHashMap = new HashMap<>();
        listeners = new ArrayList<>();

        ColorEffect gloss = new ColorEffect() {
            @Override
            public String getEffectName() {
                return "Gloss";
            }

            @Override
            public String getCategory() {
                return "Paint";
            }

            @Override
            public BumpMapMaterial.LightingParams getLightingParams() {
                return new BumpMapMaterial.LightingParams(0.3f, 0.7f, 1f, 32f);
            }

            @Override
            public int getIconResource() {
                return R.drawable.gloss_render;
            }
        };

        effectHashMap.put(gloss.getEffectName(), gloss);

        ColorEffect matte = new ColorEffect() {
            @Override
            public String getEffectName() {
                return "Matte";
            }

            @Override
            public String getCategory() {
                return "Paint";
            }

            @Override
            public BumpMapMaterial.LightingParams getLightingParams() {
                return new BumpMapMaterial.LightingParams(0.5f, 0.5f, 0.2f, 2f);
            }

            @Override
            public int getIconResource() {
                return R.drawable.matte_render;
            }
        };

        effectHashMap.put(matte.getEffectName(), matte);

        ColorEffect metal = new ColorEffect() {
            @Override
            public String getEffectName() {
                return "Metal";
            }

            @Override
            public String getCategory() {
                return "Paint";
            }

            @Override
            public BumpMapMaterial.LightingParams getLightingParams() {
                return new BumpMapMaterial.LightingParams(0.1f, 0.5f, 1f, 4f);
            }

            @Override
            public int getIconResource() {
                return R.drawable.ic_metal_icon;
            }
        };

        effectHashMap.put(metal.getEffectName(), metal);

        ResourceTextureEffect metallic = new ResourceTextureEffect() {
            @Override
            public int getTextureResource() {
                return R.drawable.metallic1;
            }

            @Override
            public String getEffectName() {
                return "Metallic";
            }

            @Override
            public String getCategory() {
                return "Paint";
            }

            @Override
            public boolean isColorEnabled() {
                return true;
            }

            @Override
            public float getTextureRepeat() {
                return 16;
            }

            @Override
            public BumpMapMaterial.LightingParams getLightingParams() {
                return new BumpMapMaterial.LightingParams(0.2f, 0.8f, 1f, 124f);
            }

            @Override
            public int getIconResource() {
                return android.R.drawable.ic_menu_gallery;
            }
        };

        effectHashMap.put(metallic.getEffectName(), metallic);

        ResourceTextureEffect wood1 = new ResourceTextureEffect() {
            @Override
            public int getTextureResource() {
                return R.drawable.wood;
            }

            @Override
            public String getEffectName() {
                return "Wood 1";
            }

            @Override
            public String getCategory() {
                return "Wood";
            }

            @Override
            public boolean isColorEnabled() {
                return false;
            }

            @Override
            public float getTextureRepeat() {
                return 1;
            }

            @Override
            public BumpMapMaterial.LightingParams getLightingParams() {
                return new BumpMapMaterial.LightingParams(0.4f, 0.6f, 0.2f, 12f);
            }

            @Override
            public int getIconResource() {
                return R.drawable.ic_wood_icon;
            }
        };
        effectHashMap.put(wood1.getEffectName(), wood1);


        ImageEffect rawImage = new ImageEffect() {
            @Override
            public String getEffectName() {
                return "Matte Image";
            }

            @Override
            public String getCategory() {
                return "Image";
            }

            @Override
            public BumpMapMaterial.LightingParams getLightingParams() {
                return new BumpMapMaterial.LightingParams(0.2f, 1f, 0.4f, 4f);
            }

            @Override
            public int getIconResource() {
                return R.drawable.ic_matte_image;
            }
        };
        effectHashMap.put(rawImage.getEffectName(), rawImage);


        ImageEffect rawImageGloss = new ImageEffect() {
            @Override
            public String getEffectName() {
                return "Glossy Image";
            }

            @Override
            public String getCategory() {
                return "Image";
            }

            @Override
            public BumpMapMaterial.LightingParams getLightingParams() {
                return new BumpMapMaterial.LightingParams(0.1f, 0.9f, 1f, 16f);
            }

            @Override
            public int getIconResource() {
                return R.drawable.ic_glossy_image;
            }
        };
        effectHashMap.put(rawImageGloss.getEffectName(), rawImageGloss);
    }

    public static Prefs getInstance() {
        if (instance == null) {
            synchronized (Prefs.class) {
                instance = new Prefs();
            }
        }
        return instance;
    }

    public void load(Context context) {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        setEffectByKey(sharedPreferences.getString(EFFECT_POS_KEY, DEFAULT_KEY));

        Log.i("Prefs", "effectKey = " + effectKey);
        isFirstTime = sharedPreferences.getBoolean(FIRST_TIME_KEY, true);
        setColor(sharedPreferences.getInt(COLOR_KEY, DEFAULT_COLOR));
        setDrawMode(sharedPreferences.getInt(DRAW_MODE_KEY, DEFAULT_DRAW_MODE));

        isLoaded = true;
    }

    public void save(Context context) {
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit();
        editor.putString(EFFECT_POS_KEY, effectKey);
        editor.putInt(COLOR_KEY, color);
        editor.putInt(DRAW_MODE_KEY, drawMode);
        if (isFirstTime) {
            editor.putBoolean(FIRST_TIME_KEY, false);
        }
        editor.apply();
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

    @Nullable
    public BaseEffect getEffect() {
        return effectHashMap.get(effectKey);
    }

    public void setEffectByKey(String key) {
        effectKey = key;
        notifyEffectChanged();
    }

    private void notifyEffectChanged() {
        try {
            for (OnPrefsChangedListener listener : listeners) {
                if (listener != null) {
                    listener.onEffectChanged(getEffect());
                }
            }
        } catch (ConcurrentModificationException e){
            Log.e(TAG, e.getMessage());
        }
    }

    private void notifyTextureChanged() {
        try {
            for (OnPrefsChangedListener listener : listeners) {
                if (listener != null) {
                    listener.onTextureChanged(getEffect());
                }
            }
        } catch (ConcurrentModificationException e){
            Log.e(TAG, e.getMessage());
        }
    }

    private void notifyColorChanged() {
        try {
            for (OnPrefsChangedListener listener : listeners) {
                if (listener != null) {
                    listener.onColorChanged(getEffect());
                }
            }
        } catch (ConcurrentModificationException e){
            Log.e(TAG, e.getMessage());
        }
    }

    private void notifyDrawModeChanged() {
        try {
            for (OnPrefsChangedListener listener : listeners) {
                if (listener != null) {
                    listener.onDrawModeChanged(getDrawMode());
                }
            }
        } catch (ConcurrentModificationException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public HashMap<String, BaseEffect> getEffectHashMap() {
        return effectHashMap;
    }

    public boolean saveImage(Context context, Bitmap bitmap, RectF rect, int size) throws OutOfMemoryError {
        drawingBlocked = true;
        boolean ok = false;
        Bitmap originalBitmap = bitmap;

        Bitmap croppedBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(croppedBitmap);
        rect.left *= (float) size;
        rect.top *= (float) size;
        rect.right *= (float) size;
        rect.bottom *= (float) size;
        Log.d("Image Rect", rect.left + ", " + rect.top + ", " + rect.right + ", " + rect.bottom);
        final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(originalBitmap, null, rect, paint);

        Bitmap processedBitmap = Bitmap.createBitmap(size / 2, size / 2, Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(processedBitmap);
        canvas1.drawBitmap(croppedBitmap, null, new Rect(0, 0, canvas1.getWidth(), canvas1.getHeight()), paint);
        int[] pixels = new int[processedBitmap.getWidth() * processedBitmap.getHeight()];
        int[] histogram = new int[256];
        processedBitmap.getPixels(pixels, 0, processedBitmap.getWidth(), 0, 0, processedBitmap.getWidth(), processedBitmap.getHeight());
        ImageProcessing.toGrayScale(pixels);

        FileOutputStream outStream = null;
        try {
            outStream = context.openFileOutput(Constants.FILENAME_DEFAULT, Context.MODE_PRIVATE);
            croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            croppedBitmap.recycle();
        }
//        ImageProcessing.equalize(pixels, histogram);
//        ImageProcessing.updateHistogram(pixels, histogram);
//        int t = 0;
//        int n = 0;
//        while (n < pixels.length / 2 && t < histogram.length){
//            n += histogram[t];
//            t++;
//        }
//        ImageProcessing.threshold(pixels, t);
        final int N = 1;
        for (int i = 0; i < N; i++) {
            ImageProcessing.boxBlur(pixels, processedBitmap.getWidth(), processedBitmap.getHeight());
        }
        ImageProcessing.generateHeightMap(processedBitmap, pixels);
        try {

            outStream = context.openFileOutput(Constants.HEIGHTMAP_FILENAME, Context.MODE_PRIVATE);
            processedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        ImageProcessing.generateNormalMap(processedBitmap, pixels);
        try {
            outStream = context.openFileOutput(Constants.NORMALMAP_FILENAME, Context.MODE_PRIVATE);
            processedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            ok = true;
            drawingBlocked = false;
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            processedBitmap.recycle();
        }
        if(ok){
            notifyTextureChanged();
        }
        return ok;
    }

    public boolean isDrawingBlocked(){
        return drawingBlocked;
    }

    public void setColor(int color) {
        for (BaseEffect effect : effectHashMap.values()) {
            if(effect.isColorEnabled()){
                effect.setColor(color);
            }
        }
        this.color = color;
        notifyColorChanged();
    }

    public void removePrefsListener(OnPrefsChangedListener listener) {
        try {
            listeners.remove(listener);
        } catch (ConcurrentModificationException e){
            Log.e(TAG, e.getMessage());
        }
    }

    public void addPrefsListener(OnPrefsChangedListener prefsListener) {
        try {
            listeners.add(prefsListener);
        } catch (ConcurrentModificationException e){
            Log.e(TAG, e.getMessage());
        }
    }

    public int getColor() {
        return color;
    }

    public void setDrawMode(int drawMode) {
        this.drawMode = drawMode;
        notifyDrawModeChanged();
    }

    public int getDrawMode() {
        return drawMode;
    }

    public interface OnPrefsChangedListener {
        void onEffectChanged(BaseEffect effect);
        void onTextureChanged(BaseEffect effect);
        void onColorChanged(BaseEffect effect);
        void onDrawModeChanged(int drawMode);
    }
}
