package org.masonapps.materialize3d.wallpaper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import org.masonapps.materialize3d.Prefs;
import org.masonapps.materialize3d.graphics.MyRenderer;
import org.masonapps.materialize3d.graphics.Vector3;
import org.masonapps.materialize3d.graphics.WallpaperRender;
import org.masonapps.materialize3d.graphics.effects.BaseEffect;


/**
 * Created by Bob on 10/24/2014.
 */
public class MyWallpaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new GLEngine();
    }

    public class GLEngine extends Engine {

        private WallpaperGLSurfaceView glSurfaceView;
        private boolean rendererHasBeenSet;

        private WallpaperRender renderer;

        private SensorManager mSensorManager;

        private volatile Vector3 accelVector = new Vector3();

        private boolean accelIsRegistered = false;

        private float tempX, tempY;

        private static final String TAG = "Wallpaper Service";

        class WallpaperGLSurfaceView extends GLSurfaceView implements Prefs.OnPrefsChangedListener {
            private static final String TAG = "WallpaperGLSurfaceView";

            WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            @Override
            public void onResume() {
                super.onResume();
                Prefs.getInstance().addPrefsListener(this);
            }

            @Override
            public void onPause() {
                Prefs.getInstance().removePrefsListener(this);
                super.onPause();
            }

            public void onDestroy() {
                super.onDetachedFromWindow();
            }

            @Override
            public void onEffectChanged(BaseEffect effect) {
                final BaseEffect effect1 = effect;
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if(renderer != null){
                            ((MyRenderer)renderer).setEffect(effect1);
                        }
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

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Prefs prefs = Prefs.getInstance();
            if (!prefs.isLoaded()) {
                prefs.load(getApplicationContext());
            }
            glSurfaceView = new WallpaperGLSurfaceView(MyWallpaperService.this);

            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
//            try {
//                Prefs.getInstance().loadCurrentFile(getApplicationContext());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            if (supportsEs2) {
                // Request an OpenGL ES 2.0 compatible context.
                setEGLContextClientVersion(2);

                // On Honeycomb+ devices, this improves the performance when
                // leaving and resuming the live wallpaper.
                setPreserveEGLContextOnPause(false);

                // Set the renderer to our user-defined renderer.
                renderer = new MyRenderer();
                renderer.setContext(getApplicationContext());
                setRenderer(renderer);
            } else {
                // This is where you could create an OpenGL ES 1.x compatible
                // renderer if you wanted to support both ES 1 and ES 2.
                return;
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            final float offset = xOffset;
            glSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    renderer.onOffsetsChanged(offset);
                }
            });
        }

        @Override
        public void onVisibilityChanged(boolean visible) {

            super.onVisibilityChanged(visible);

            if (rendererHasBeenSet) {
                if (visible) {
                    glSurfaceView.onResume();
                    onResume();
                } else {
                    onPause();
                    glSurfaceView.onPause();
                }
            }
        }

        @Override
        public void onDestroy() {

            super.onDestroy();
            glSurfaceView.onDestroy();
        }

        protected void setRenderer(GLSurfaceView.Renderer renderer) {

            glSurfaceView.setRenderer(renderer);
            rendererHasBeenSet = true;
        }

        protected void setPreserveEGLContextOnPause(boolean preserve) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                glSurfaceView.setPreserveEGLContextOnPause(preserve);
            }
        }

        protected void setEGLContextClientVersion(int version) {

            glSurfaceView.setEGLContextClientVersion(version);
        }

        public void onPause() {
            glSurfaceView.queueEvent(pauseRunnable);
            if (mSensorManager != null && accelIsRegistered) {
                mSensorManager.unregisterListener(mSensorListener);
            }
        }

        public void onResume() {
            glSurfaceView.queueEvent(resumeRunnable);

//            if(Prefs.getInstance().sensorEnabled) {
            mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
            accelIsRegistered = mSensorManager.registerListener(mSensorListener,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI);
//                ((MyRenderer) renderer).setSensorEnabled(true);
//                if (!accelIsRegistered) {
//                    ((MyRenderer) renderer).setSensorEnabled(false);
//                    Prefs prefs = Prefs.getInstance();
//                    prefs.saveSensorEnabled(false);
//                    try {
//                        prefs.saveToFile(getApplicationContext(), prefs.getCurrentFilename());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }else{
//                ((MyRenderer) renderer).setSensorEnabled(false);
//            }
        }

        private final SensorEventListener mSensorListener = new SensorEventListener() {
            private float[] remappedRotationMatrix = new float[16];
            private int rotation;

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (accelIsRegistered
                        && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accelVector.set(event.values[0], event.values[1], event.values[2]);
                    rotation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                            .getRotation();
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
                    glSurfaceView.queueEvent(setAccelRunnable);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        private final Runnable resumeRunnable = new Runnable() {
            @Override
            public void run() {
                renderer.onResume();
            }
        };

        private final Runnable setAccelRunnable = new Runnable() {
            @Override
            public void run() {
                renderer.setSensorVector(accelVector);
            }
        };

        private final Runnable pauseRunnable = new Runnable() {
            @Override
            public void run() {
                renderer.onPause();
            }
        };
    }
}
