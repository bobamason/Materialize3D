package org.masonapps.materialize3d.graphics;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import org.masonapps.materialize3d.R;
import org.masonapps.materialize3d.graphics.cameras.OrthographicCamera;
import org.masonapps.materialize3d.graphics.materials.TextureMaterial;
import org.masonapps.materialize3d.graphics.meshes.PlaneMesh;
import org.masonapps.materialize3d.utils.Constants;

public class LoadingAnimation {
    private final PlaneMesh mesh;
    private float[] modelMatrix = new float[16];
    private float c;
    private float[] color = {1f, 1f, 1f, 1f};
    private float scale;
    private OrthographicCamera camera;

    public LoadingAnimation(Context context) {
        mesh = new PlaneMesh(new TextureMaterial(context, R.drawable.progress_icon), 1f, 1f);
        camera = new OrthographicCamera(1f, 1f, 1f, 100f);
    }

    public void setDimen(int width, int height) {
        camera.setScreenDimen(width, height);
        scale = Math.max(width, height) * 0.1f;
    }

    public void draw(float time) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -14f);
        Matrix.rotateM(modelMatrix, 0, -time * 1080f / Constants.PI, 0f, 0f, 1f);
        Matrix.scaleM(modelMatrix, 0, scale, scale, scale);
        mesh.draw(camera, modelMatrix);
    }
}