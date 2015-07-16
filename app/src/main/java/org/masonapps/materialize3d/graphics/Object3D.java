package org.masonapps.materialize3d.graphics;

import android.opengl.Matrix;

import org.masonapps.materialize3d.graphics.cameras.BaseCamera;
import org.masonapps.materialize3d.graphics.meshes.Mesh;

/**
 * Created by Bob on 10/20/2014.
 */
public class Object3D<T extends Mesh> {

    public T mesh;

    protected float[] modelMatrix = new float[16];

    public Object3D(T mesh) {
        this.mesh = mesh;
    }

    public void draw(BaseCamera camera, Light light) {
        if (mesh != null && mesh.loaded)
            mesh.draw(camera, light, modelMatrix);
    }

    public void draw(BaseCamera camera) {
        if (mesh != null && mesh.loaded)
            mesh.draw(camera, modelMatrix);
    }

    public void setIdentity() {
        Matrix.setIdentityM(modelMatrix, 0);
    }

    public void translate(float x, float y, float z) {
        Matrix.translateM(modelMatrix, 0, x, y, z);
    }

    public void translate(Vector3 v) {
        Matrix.translateM(modelMatrix, 0, v.x, v.y, v.z);
    }
    
    public void setlookAt(Vector3 forward, Vector3 center, Vector3 up){
        Matrix.setLookAtM(modelMatrix, 0, forward.x, forward.y, forward.z, center.x, center.y, center.z, up.x, up.y, up.z);
    }
    
    public void setlookAt(float forwardX, float forwardY, float forwardZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ){
        Matrix.setLookAtM(modelMatrix, 0, forwardX, forwardY, forwardZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public void rotateEuler(float z, float x, float y) {
        Matrix.rotateM(modelMatrix, 0, z, 0f, 0f, 1f);
        Matrix.rotateM(modelMatrix, 0, x, 1f, 0f, 0f);
        Matrix.rotateM(modelMatrix, 0, y, 0f, 1f, 0f);
    }

    public void rotateAxis(float a, float x, float y, float z) {
        Matrix.rotateM(modelMatrix, 0, a, x, y, z);
    }

    public void scale(float s) {
        Matrix.scaleM(modelMatrix, 0, s, s, s);
    }

    public void scale(float sx, float sy, float sz) {
        Matrix.scaleM(modelMatrix, 0, sx, sy, sz);
    }

    public void setMesh(T mesh) {
        this.mesh = mesh;
    }
}
