package org.masonapps.materialize3d.graphics.cameras;

import android.opengl.Matrix;

/**
 * Created by Bob on 10/20/2014.
 */
public class PerspectiveCamera extends BaseCamera {

    private float fov;

    public PerspectiveCamera(float fov, float width, float height, float near, float far) {
        super(width, height, near, far);
        this.fov = fov;
        float top;
        float right;
        float ratio;
        if (height > width) {
            ratio = width / height;
            top = near * (float) Math.tan(Math.toRadians(fov) * 0.5f);
            right = ratio * top;
        } else {
            ratio = height / width;
            right = near * (float) Math.tan(Math.toRadians(fov) * 0.5f);
            top = ratio * right;
        }
        float bottom = -top;
        float left = -right;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, this.near, this.far);
    }

    @Override
    public float[] getProjectionMatrix() {
        return mProjectionMatrix;
    }

    public void setFOV(float fov) {
        float top;
        float right;
        float ratio;
        if (height > width) {
            ratio = width / height;
            top = near * (float) Math.tan(Math.toRadians(fov) * 0.5f);
            right = ratio * top;
        } else {
            ratio = height / width;
            right = near * (float) Math.tan(Math.toRadians(fov) * 0.5f);
            top = ratio * right;
        }
        float bottom = -top;
        float left = -right;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, this.near, this.far);
    }
    public float getFOV() {
        return fov;
    }

    public void setScreenDimen(float w, float h) {
        width = w;
        height = h;
        set(fov, w, h, near, far);
    }

    public void set(float fov, float width, float height, float near, float far) {
        this.fov = fov;
        this.width = width;
        this.height = height;
        this.near = near;
        this.far = far;
        float top;
        float right;
        float ratio;
        if (height > width) {
            ratio = width / height;
            top = near * (float) Math.tan(Math.toRadians(fov) * 0.5f);
            right = ratio * top;
        } else {
            ratio = height / width;
            right = near * (float) Math.tan(Math.toRadians(fov) * 0.5f);
            top = ratio * right;
        }
        float bottom = -top;
        float left = -right;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, this.near, this.far);
    }

}