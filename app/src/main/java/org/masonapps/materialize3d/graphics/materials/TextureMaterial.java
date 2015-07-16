package org.masonapps.materialize3d.graphics.materials;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import org.masonapps.materialize3d.graphics.OpenGLUtils;
import org.masonapps.materialize3d.graphics.cameras.BaseCamera;
import org.masonapps.materialize3d.graphics.meshes.Mesh;

/**
 * Created by Bob on 10/23/2014.
 */
public class TextureMaterial extends Material {
    public static final String TEXTURE = "u_Texture";
    public static final String MVP_UNIFORM = "u_MVPMatrix";
    public static final String MV_UNIFORM = "u_MVMatrix";
    public static final String FRAG_SHADER_UNIFORM_PREFIX =
            "precision mediump float;"
                    + "uniform sampler2D " + TEXTURE + ";";
    public static final String V_POSITION = "v_Position";
    public static final String V_NORMAL = "v_Normal";
    public static final String V_TEX_COORDINATE = "v_TexCoordinate";
    public static final String VARYING_PREFIX = "varying vec3 " + V_POSITION + ";"
            + "varying vec3 " + V_NORMAL + ";"
            + "varying vec2 " + V_TEX_COORDINATE + ";";
    public static final String DEFAULT_VERTEX_SHADER = "uniform mat4 " + MVP_UNIFORM + ";"
            + "uniform mat4 " + MV_UNIFORM + ";"

            + "attribute vec4 " + OpenGLUtils.POSITION_ATTRIB + ";"
            + "attribute vec3 " + OpenGLUtils.NORMAL_ATTRIB + ";"
            + "attribute vec2 " + OpenGLUtils.TEX_COORDINATE_ATTRIB + ";"

            + VARYING_PREFIX

            + "void main()"
            + "{"
            + "   vec4 pos = " + OpenGLUtils.POSITION_ATTRIB + ";"
            + "   " + V_POSITION + " = vec3(" + MV_UNIFORM + " * pos);"
            + "   " + V_TEX_COORDINATE + " = " + OpenGLUtils.TEX_COORDINATE_ATTRIB + ";"
            + "   " + V_NORMAL + " = vec3(" + MV_UNIFORM + " * vec4(" + OpenGLUtils.NORMAL_ATTRIB + ", 0.0));"
            + "   gl_Position = " + MVP_UNIFORM + " * pos;"
            + "}";
    protected int mPositionHandle;
    protected int mMVPMatrixHandle;
    protected int mMVMatrixHandle;
    protected int mNormalHandle;
    protected float[] mvpMatrix = new float[16];
    protected float[] mvMatrix = new float[16];
    protected int textureUniformHandle;
    protected int textureCoordinateHandle;

    public TextureMaterial(Context c, int texID) {
        super(c, texID);
    }

    public TextureMaterial(Context c, String path) {
        super(c, path);
    }

    public TextureMaterial(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public String getVertexShader() {
        return DEFAULT_VERTEX_SHADER;
    }

    @Override
    public String getFragmentShader() {
        return FRAG_SHADER_UNIFORM_PREFIX
                + VARYING_PREFIX

                + "void main()"
                + "{"
                + "   vec2 tc = " + V_TEX_COORDINATE + ";"
                + "   vec4 c = texture2D(" + TEXTURE + ", tc);"
                + "   gl_FragColor = c;"
                + "}";
    }


    @Override
    public void draw(BaseCamera camera, float[] modelMatrix, Mesh mesh) {
        if (!mesh.loaded || textureDataHandle == -1 || mProgram == -1) return;

        GLES20.glUseProgram(mProgram);
        OpenGLUtils.checkGLError("use program");
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
    }

    @Override
    public void dispose() {
        GLES20.glDeleteProgram(mProgram);
        GLES20.glDeleteTextures(1, new int[]{textureDataHandle}, 0);
        GLES20.glFlush();
        textureDataHandle = -1;
    }

    public void setTexture(Context c, int texID) {
        dispose();
        textureDataHandle = OpenGLUtils.loadTextureResource(c, texID);
        OpenGLUtils.checkGLError("Load Texture");
    }
}
