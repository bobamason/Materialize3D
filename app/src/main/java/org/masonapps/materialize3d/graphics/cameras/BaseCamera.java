package org.masonapps.materialize3d.graphics.cameras;


import android.opengl.Matrix;

import org.masonapps.materialize3d.graphics.Vector3;

/**
 * Created by ims_2 on 3/20/2015.
 */
public abstract class BaseCamera {

    protected float width;
    protected float height;
    protected Vector3 up;
    protected Vector3 lookAt;
    protected Vector3 pos;
    protected float[] mProjectionMatrix = new float[16];
    protected float[] mViewMatrix = new float[16];
    protected float near;
    protected float far;

    protected BaseCamera(float width, float height, float near, float far) {
        this.width = width;
        this.height = height;
        this.near = near;
        this.far = far;
        this.up = new Vector3(0f, 1f, 0f);
        this.pos = new Vector3(0f, 0f, 0f);
        this.lookAt = new Vector3(0f, 0f, -1f);
    }

    public abstract float[] getProjectionMatrix();

    public float[] getViewMatrix() {
        Matrix.setLookAtM(mViewMatrix, 0, pos.x, pos.y, pos.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
        return mViewMatrix;
    }

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    public void setPos(float x, float y, float z) {
        pos.set(x, y, z);
    }

    public Vector3 getUp() {
        return up;
    }

    public void setUp(float x, float y, float z) {
        up.set(x, y, z);
    }


    public Vector3 getLookAt() {
        return lookAt;
    }

    public void setLookAt(float x, float y, float z) {
        lookAt.set(x, y, z);
    }

    public void setUp(Vector3 up) {
        this.up = up;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
