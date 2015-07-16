package org.masonapps.materialize3d.graphics;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import org.masonapps.materialize3d.Prefs;
import org.masonapps.materialize3d.graphics.effects.BaseEffect;


public class GLSurface extends GLSurfaceView implements Prefs.OnPrefsChangedListener {

    private final Display display;
    private final Context context;
    private WallpaperRender renderer;

    private SensorManager mSensorManager;

    private volatile Vector3 accelVector = new Vector3();

    private boolean accelIsRegistered = false;

    private float tempX, tempY;

    public GLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        //getHolder().setFormat(PixelFormat.TRANSLUCENT);
        display = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        renderer = new MyRenderer();
        renderer.setContext(context);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onResume() {
        super.onResume();
        Prefs.getInstance().addPrefsListener(this);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.onResume();
            }
        });
//        Prefs.getInstance().setListener(prefsChangeListener);
        mSensorManager = (SensorManager) context.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        accelIsRegistered = mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
//        ((MyRenderer) renderer).setSensorEnabled(Prefs.getInstance().sensorEnabled);
//        if (!accelIsRegistered) {
//            ((MyRenderer) renderer).setSensorEnabled(false);
//            Prefs prefs = Prefs.getInstance();
//            prefs.saveSensorEnabled(false);
//            ((MyRenderer) renderer).setSensorEnabled(false);
//            try {
//                prefs.saveToFile(context.getApplicationContext(), prefs.getCurrentFilename());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onPause() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.onPause();
            }
        });
        if (mSensorManager != null && accelIsRegistered) {
            mSensorManager.unregisterListener(mSensorListener);
        }
        Prefs.getInstance().removePrefsListener(this);
        super.onPause();
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        private float[] remappedRotationMatrix = new float[16];
        private int rotation;

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (accelIsRegistered
                    && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelVector.set(event.values[0], event.values[1], event.values[2]);
                rotation = display.getRotation();
                switch (rotation) {
                    case Surface.ROTATION_0:
                        break;
                    case Surface.ROTATION_90:
                        tempX = accelVector.x;
                        tempY = accelVector.y;
                        accelVector.x = -tempY;
                        accelVector.y = tempX;
                        break;
                    case Surface.ROTATION_180:
                        tempX = accelVector.x;
                        tempY = accelVector.y;
                        accelVector.x = -tempX;
                        accelVector.y = -tempY;
                        break;
                    case Surface.ROTATION_270:
                        tempX = accelVector.x;
                        tempY = accelVector.y;
                        accelVector.x = tempY;
                        accelVector.y = -tempX;
                        break;
                }
                queueEvent(setAccelRunnable);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private final Runnable setAccelRunnable = new Runnable() {
        @Override
        public void run() {
            renderer.setSensorVector(accelVector);
        }
    };

    public void setScreenshotListener(final MyRenderer.ScreenshotListener screenshotListener) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                ((MyRenderer) renderer).setScreenshotListener(screenshotListener);
            }
        });
    }

    public void takeScreenShot() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                ((MyRenderer) renderer).takeScreenshot();
            }
        });
    }

    @Override
    public void onEffectChanged(final BaseEffect effect) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                ((MyRenderer) renderer).setEffect(effect);
            }
        });
    }

    @Override
    public void onTextureChanged(BaseEffect effect) {
        final BaseEffect effect1 = effect;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if(renderer != null){
                    ((MyRenderer)renderer).updateTexture(effect1);
                }
            }
        });
    }

    @Override
    public void onColorChanged(BaseEffect effect) {
        final BaseEffect effect1 = effect;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if(renderer != null){
                    ((MyRenderer)renderer).setColor(effect1);
                }
            }
        });
    }

    @Override
    public void onDrawModeChanged(final int drawMode) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if(renderer != null){
                    ((MyRenderer)renderer).setDrawMode(drawMode);
                }
            }
        });
    }
}
