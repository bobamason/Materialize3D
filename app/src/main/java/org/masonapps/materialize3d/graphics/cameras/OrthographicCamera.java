package org.masonapps.materialize3d.graphics.cameras;

import android.opengl.Matrix;

/**
 * Created by ims_2 on 3/31/2015.
 */
public class OrthographicCamera extends BaseCamera {

    public OrthographicCamera(float width, float height, float near, float far) {
        super(width, height, near, far);
        final float hw = width / 2f;
        final float hh = height / 2f;
        Matrix.orthoM(mProjectionMatrix, 0, -hw, hw, -hh, hh, near, far);
    }

    @Override
    public float[] getProjectionMatrix() {
        return mProjectionMatrix;
    }

    public void setScreenDimen(float w, float h) {
        width = w;
        height = h;
        set(w, h, near, far);
    }

    public void set(float width, float height, float near, float far){
        this.width = width;
        this.height = height;
        this.near = near;
        this.far = far;
        final float hw = width / 2f;
        final float hh = height / 2f;
        Matrix.orthoM(mProjectionMatrix, 0, -hw, hw, -hh, hh, near, far);
    }

}
