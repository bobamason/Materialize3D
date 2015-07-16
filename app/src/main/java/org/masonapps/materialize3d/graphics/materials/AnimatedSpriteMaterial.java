package org.masonapps.materialize3d.graphics.materials;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import org.masonapps.materialize3d.graphics.OpenGLUtils;
import org.masonapps.materialize3d.graphics.cameras.BaseCamera;
import org.masonapps.materialize3d.graphics.meshes.Mesh;


/**
 * Created by Bob on 5/7/2015.
 */
public class AnimatedSpriteMaterial extends TextureMaterial {

    private static final String COUNT_UNIFORM = "count";
    private static final String OFFSET_UNIFORM = "offset";
    private int countHandle;
    private int offsetHandle;
    private int frameCount;
    private float offset;
    private boolean autoChangeFrame = true;
    public int currentFrame = 0;

    public AnimatedSpriteMaterial(Context c, int texID, int frameCount) {
        super(c, texID);
        this.frameCount = frameCount;
    }

    @Override
    public String getVertexShader() {
        return "uniform mat4 " + MVP_UNIFORM + ";"
                + "uniform mat4 " + MV_UNIFORM + ";"
                + "uniform float " + COUNT_UNIFORM + ";"
                + "uniform float " + OFFSET_UNIFORM + ";"

                + "attribute vec4 " + OpenGLUtils.POSITION_ATTRIB + ";"
                + "attribute vec3 " + OpenGLUtils.NORMAL_ATTRIB + ";"
                + "attribute vec2 " + OpenGLUtils.TEX_COORDINATE_ATTRIB + ";"

                + VARYING_PREFIX

                + "void main()"
                + "{"
                + "   vec4 pos = " + OpenGLUtils.POSITION_ATTRIB + ";"
                + "   " + V_POSITION + " = vec3(" + MV_UNIFORM + " * pos);"
                + "   vec2 coord = " + OpenGLUtils.TEX_COORDINATE_ATTRIB + ";"
                + "   coord.x = coord.x / " + COUNT_UNIFORM + " + " + OFFSET_UNIFORM + ";"
                + "   " + V_TEX_COORDINATE + " = coord;"
                + "   " + V_NORMAL + " = vec3(" + MV_UNIFORM + " * vec4(" + OpenGLUtils.NORMAL_ATTRIB + ", 0.0));"
                + "   gl_Position = " + MVP_UNIFORM + " * pos;"
                + "}";
    }

    @Override
    public void draw(BaseCamera camera, float[] modelMatrix, Mesh mesh) {
        if (!mesh.loaded || textureDataHandle == -1 || mProgram == -1) return;

        offset = 1f / frameCount * currentFrame;

        GLES20.glUseProgram(mProgram);
        OpenGLUtils.checkGLError("use program");

        countHandle = GLES20.glGetUniformLocation(mProgram,
                COUNT_UNIFORM);
        offsetHandle = GLES20.glGetUniformLocation(mProgram,
                OFFSET_UNIFORM);
        textureUniformHandle = GLES20.glGetUniformLocation(mProgram,
                TEXTURE);
        textureCoordinateHandle = GLES20.glGetAttribLocation(mProgram,
                OpenGLUtils.TEX_COORDINATE_ATTRIB);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,
                MVP_UNIFORM);
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram,
                MV_UNIFORM);

        mPositionHandle = GLES20
                .glGetAttribLocation(mProgram, OpenGLUtils.POSITION_ATTRIB);
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, OpenGLUtils.NORMAL_ATTRIB);

        Matrix.multiplyMM(mvMatrix, 0, camera.getViewMatrix(), 0, modelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

        Matrix.multiplyMM(mvpMatrix, 0, camera.getProjectionMatrix(), 0, mvMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glUniform1f(countHandle, frameCount);
        GLES20.glUniform1f(offsetHandle, offset);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
        GLES20.glUniform1i(textureUniformHandle, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mesh.getVertexBufferID());
        OpenGLUtils.checkGLError("bind ARRAY_BUFFER");
        GLES20.glVertexAttribPointer(mPositionHandle, Mesh.POSITION_DATA_SIZE,
                GLES20.GL_FLOAT, false, mesh.vertexStride, 0);
        OpenGLUtils.checkGLError("use mPositionHandle");
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mNormalHandle, Mesh.NORMAL_DATA_SIZE,
                GLES20.GL_FLOAT, false, mesh.vertexStride, Mesh.NORMAL_OFFSET);
        OpenGLUtils.checkGLError("use mNormalHandle");
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        GLES20.glVertexAttribPointer(textureCoordinateHandle,
                Mesh.TEXTURE_DATA_SIZE, GLES20.GL_FLOAT, false, mesh.vertexStride,
                Mesh.TEXTURE_OFFSET);
        OpenGLUtils.checkGLError("use textureCoordinateHandle");
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mesh.getIndexBufferID());
        OpenGLUtils.checkGLError("bind ELEMENT_ARRAY_BUFFER");
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.indicesLength,
                GLES20.GL_UNSIGNED_SHORT, 0);
        OpenGLUtils.checkGLError("use GL_ELEMENT_ARRAY_BUFFER Texture");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);

        if (autoChangeFrame) {
            currentFrame++;
            currentFrame %= frameCount;
        }
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setAutoChangeFrame(boolean autoChangeFrame) {
        this.autoChangeFrame = autoChangeFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
        this.currentFrame %= frameCount;
    }
}
