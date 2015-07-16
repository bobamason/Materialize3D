package org.masonapps.materialize3d.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import org.masonapps.materialize3d.Prefs;
import org.masonapps.materialize3d.R;
import org.masonapps.materialize3d.graphics.cameras.BaseCamera;
import org.masonapps.materialize3d.graphics.cameras.OrthographicCamera;
import org.masonapps.materialize3d.graphics.cameras.PerspectiveCamera;
import org.masonapps.materialize3d.graphics.effects.BaseEffect;
import org.masonapps.materialize3d.graphics.materials.BumpMapMaterial;
import org.masonapps.materialize3d.graphics.meshes.CubeMesh;
import org.masonapps.materialize3d.graphics.meshes.InvertedCubeMesh;
import org.masonapps.materialize3d.graphics.meshes.Mesh;
import org.masonapps.materialize3d.graphics.meshes.PlaneMesh;
import org.masonapps.materialize3d.utils.Constants;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by Bob on 10/29/2014.
 */
public class MyRenderer extends WallpaperRender {
    private static final float ALPHA = 0.05f;
    private static final float PI = (float) Math.PI;
    private static final float Z = -2000f;
    private static final float NEAR = 1f;
    private static final float FAR = 10000f;
    public static final float LIGHT_Z = 50f;
    private static final float FOV = 80f;

    private BaseCamera camera;
    private Context context;
    private double t1;
    private double eT;
    private double t0;
    private float time = 0f;
    private boolean sensorEnabled = true;
    private Vector3 smoothedVec = new Vector3();
    private Vector3 lastVec = new Vector3();
    private Vector3 sensorVector = new Vector3();
    private Vector3 location = new Vector3();
    private Object3D<Mesh> obj;

    private Mesh mesh;
    private LoadingAnimation loadingAnimation;
    private BumpMapMaterial bumpMapMaterial;
    private float[] clearColor;
    private boolean isPortrait = false;
    private BaseEffect effect;
    private boolean doScreenshot = false;
    private int width;
    private int height;
    private ScreenshotListener listener = null;
    private float camX;
    private Light light;
    private float halfSize;
    private int drawMode;

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        loadingAnimation = new LoadingAnimation(context);

        clearColor = new float[]{0.1f, 0.1f, 0.1f};
        bumpMapMaterial = new BumpMapMaterial();
        if(obj == null) {
            obj = new Object3D<>(null);
        }
        if(light == null){
            light = new Light(0f, 0f, LIGHT_Z);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
        final Prefs prefs = Prefs.getInstance();
        if(!prefs.isDrawingBlocked()) {
            setEffect(prefs.getEffect());
            bumpMapMaterial.setHeightMap(context, Constants.HEIGHTMAP_FILENAME);
            bumpMapMaterial.setNormalMap(context, Constants.NORMALMAP_FILENAME);

            if(bumpMapMaterial.heightMapDataHandle == -1 || bumpMapMaterial.normalMapDataHandle == -1) {
                bumpMapMaterial.setHeightMap(context, R.drawable.default_heightmap);
                bumpMapMaterial.setNormalMap(context, R.drawable.default_normalmap);
            }
        }
//        setDrawMode(Prefs.getInstance().getDrawMode());
        setDrawMode(Prefs.DRAW_MODE_CUBE);
        isPortrait = height > width;
        loadingAnimation.setDimen(width, height);
        if (isPortrait) {
            camX = (camera.getHeight() - camera.getWidth()) * -0.5f;
        } else {
            camX = 0f;
        }
        camera.setPos(camX, 0f, 0f);

        t0 = t1 = getTime();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        t0 = t1;
        t1 = getTime();
        eT = t1 - t0;
        if (run()) {
            GLES20.glClearColor(clearColor[0], clearColor[1], clearColor[2], 1f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            if (sensorEnabled) {
                smoothSensor();
            }

            halfSize = Math.max(width, height) / 2f;
            
            if (doScreenshot) {
                camera.setPos(0f, 0f, 0f);
                if(camera instanceof OrthographicCamera) {
                    ((OrthographicCamera)camera).setScreenDimen(halfSize * 2f, halfSize * 2f);
                }
                if(camera instanceof PerspectiveCamera) {
                    ((PerspectiveCamera)camera).setScreenDimen(halfSize * 2f, halfSize * 2f);
                }
                final int size = isPortrait ? width : height;
                final int x = isPortrait ? 0 : (width - size) / 2;
                final int y = isPortrait ? (height - size) / 2 : 0;
                GLES20.glViewport(x, y, size, size);
            }
            
            bumpMapMaterial.setTime(time);
            bumpMapMaterial.setAlpha(1f);
            
            switch (drawMode){
                case Prefs.DRAW_MODE_PLANE:
                    light.set(-smoothedVec.x * halfSize, -smoothedVec.y * halfSize, LIGHT_Z);
                    obj.setIdentity();
                    obj.translate(location);
                    obj.scale(1.024f);
                    break;
                case Prefs.DRAW_MODE_CUBE:
//                    obj.setlookAt(smoothedVec.x * halfSize, smoothedVec.y * halfSize, location.z + 1f,
//                            location.x, location.y, location.z,
//                            0f, 1f, 0f);
                    break;
                case Prefs.DRAW_MODE_INVERTED_CUBE:
                    obj.setIdentity();
                    obj.translate(location);
                    camera.setPos(smoothedVec.x * halfSize + camX, smoothedVec.y * halfSize, 0f);
                    camera.setLookAt(camX, 0f, -halfSize);
                    break;
            }
            obj.draw(camera, light);
            lastVec.set(smoothedVec);
            
            if (doScreenshot) {
                final int size = isPortrait ? width : height;
                final int x = isPortrait ? 0 : (width - size) / 2;
                final int y = isPortrait ? (height - size) / 2 : 0;
                final int numPix = size * size;
                int[] pixels = new int[numPix];
                IntBuffer outBuffer = IntBuffer.wrap(pixels);
                outBuffer.position(0);
                GLES20.glReadPixels(x, y, size, size, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, outBuffer);

                for (int i = 0; i < pixels.length; i++) {
                    pixels[i] = ((pixels[i] & 0xff00ff00)) | ((pixels[i] << 16) & 0x00ff0000) | ((pixels[i] >> 16) & 0xff);
                }
                Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
                if (listener != null) listener.screenshotDone(bitmap);
                outBuffer.clear();
                GLES20.glViewport(0, 0, width, height);
                camera.setPos(camX, 0f, 0f);
                if(camera instanceof OrthographicCamera) {
                    ((OrthographicCamera)camera).setScreenDimen(width, height);
                }
                if(camera instanceof PerspectiveCamera) {
                    ((PerspectiveCamera)camera).setScreenDimen(width, height);
                }
                doScreenshot = false;
            }
        } else {
            loadingAnimation.draw(time);
        }

        time += PI * 0.25f * eT;
        time %= 2f * PI;
    }

    private boolean run() {
        return mesh != null && mesh.loaded && !Prefs.getInstance().isDrawingBlocked();
    }

    private double getTime() {
        return System.currentTimeMillis() * 0.001d;
    }

    @Override
    public void onPause() {
//        if (mesh != null) {
//            mesh.dispose();
//            mesh = null;
//        } else Log.e("wallpaper renderer", "mesh = null");
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onOffsetsChanged(float offset) {
        if (camera != null && isPortrait) {
            final float diff = camera.getHeight() - camera.getWidth();
            camX = -diff * 0.5f + offset * diff;
            camera.setPos(camX, 0f, 0f);
        }
    }

    @Override
    public void setSensorVector(Vector3 accelVector) {
        sensorVector.set(accelVector);
        sensorVector.normalize();
        sensorVector.multC(-1);
    }


    public void setEffect(BaseEffect effect) {
        this.effect = effect;
        if (bumpMapMaterial != null) {
            Log.d(MyRenderer.class.getSimpleName(), "set effect called");
            bumpMapMaterial.setEffect(context, effect);
        }
    }

    public void updateTexture(BaseEffect effect){
        if(bumpMapMaterial != null){
            bumpMapMaterial.updateTexture(context, effect);
        }
    }

    public void setColor(BaseEffect effect){
        if(bumpMapMaterial != null){
            final int color = effect.getColor();
            bumpMapMaterial.setTintRGB(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF);
        }
    }

    public void setDrawMode(int drawMode){
        final int size = Math.max(width, height);
        if(mesh != null) {
            mesh.dispose();
        }
        this.drawMode = drawMode;
        switch (drawMode){
            case Prefs.DRAW_MODE_PLANE:
                mesh = new PlaneMesh(bumpMapMaterial, size, size);
                camera = new OrthographicCamera(width, height, NEAR, FAR);
                location.set(0f, 0f, Z);
                break;
            case Prefs.DRAW_MODE_CUBE:
                mesh = new CubeMesh(bumpMapMaterial, size, size, size);
                camera = new PerspectiveCamera(FOV, width, height, NEAR, FAR);
                location.set(0f, 0f, size * -1.1f);
                break;
            case Prefs.DRAW_MODE_INVERTED_CUBE:
                mesh = new InvertedCubeMesh(bumpMapMaterial, size, size, size);
                camera = new PerspectiveCamera(FOV, width, height, NEAR, FAR);
                location.set(0f, 0f, size * -0.25f);
                break;
        }
        obj.setMesh(mesh);
    }

//    public void setColorEffect(ColorEffect effect) {
//        this.colorEffect = effect;
//        if (effectMaterial != null) {
//            effectMaterial.setEffect(context, this.effect, colorEffect);
//        }
//    }


    private void smoothSensor() {
        smoothedVec.x = ALPHA * sensorVector.x + (1f - ALPHA) * smoothedVec.x;
        smoothedVec.y = ALPHA * sensorVector.y + (1f - ALPHA) * smoothedVec.y;
        smoothedVec.z = ALPHA * sensorVector.z + (1f - ALPHA) * smoothedVec.z;
    }

    public void takeScreenshot() {
        this.doScreenshot = true;
    }

    public void setScreenshotListener(ScreenshotListener listener) {
        this.listener = listener;
    }

    public interface ScreenshotListener {
        void screenshotDone(Bitmap bitmap);
    }
}
