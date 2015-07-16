package org.masonapps.materialize3d.graphics.materials;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import org.masonapps.materialize3d.graphics.Light;
import org.masonapps.materialize3d.graphics.OpenGLUtils;
import org.masonapps.materialize3d.graphics.cameras.BaseCamera;
import org.masonapps.materialize3d.graphics.meshes.Mesh;


/**
 * Created by Bob on 10/22/2014.
 */
public abstract class Material implements BaseMaterial {

    public int mProgram = -1;
    public int textureDataHandle = -1;


    public Material() {
        createProgram();
    }

    public Material(Context c, int texID) {
        createProgram(c, texID);
    }

    public Material(Context c, String path) {
        createProgram(c, path);
    }

    public Material(Bitmap bitmap) {
        createProgram(bitmap);
    }

    @Override
    public void createProgram() {
        String vert = getVertexShader();
        String frag = getFragmentShader();

        int vertexShader = OpenGLUtils.loadGLShader(GLES20.GL_VERTEX_SHADER,
                vert);
        int fragmentShader = OpenGLUtils.loadGLShader(GLES20.GL_FRAGMENT_SHADER,
                frag);

        OpenGLUtils.checkGLError("Load Shader");

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glBindAttribLocation(mProgram, 1, OpenGLUtils.POSITION_ATTRIB);
        GLES20.glBindAttribLocation(mProgram, 2, OpenGLUtils.NORMAL_ATTRIB);
        GLES20.glBindAttribLocation(mProgram, 3, OpenGLUtils.TEX_COORDINATE_ATTRIB);
        GLES20.glBindAttribLocation(mProgram, 4, OpenGLUtils.TANGENT_ATTRIB);

        GLES20.glLinkProgram(mProgram);
        OpenGLUtils.checkGLError("Link Program");
    }

    @Override
    public void createProgram(Context c, int texID) {
        String vert = getVertexShader();
        String frag = getFragmentShader();

        int vertexShader = OpenGLUtils.loadGLShader(GLES20.GL_VERTEX_SHADER,
                vert);
        int fragmentShader = OpenGLUtils.loadGLShader(GLES20.GL_FRAGMENT_SHADER,
                frag);
        OpenGLUtils.checkGLError("Load Shader");

        textureDataHandle = OpenGLUtils.loadTextureResource(c, texID);
        OpenGLUtils.checkGLError("Load Texture");

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glBindAttribLocation(mProgram, 1, OpenGLUtils.POSITION_ATTRIB);
        GLES20.glBindAttribLocation(mProgram, 2, OpenGLUtils.NORMAL_ATTRIB);
        GLES20.glBindAttribLocation(mProgram, 3, OpenGLUtils.TEX_COORDINATE_ATTRIB);

        GLES20.glLinkProgram(mProgram);
        OpenGLUtils.checkGLError("Link Program");
    }

    @Override
    public void createProgram(Context c, String path) {
        String vert = getVertexShader();
        String frag = getFragmentShader();

        int vertexShader = OpenGLUtils.loadGLShader(GLES20.GL_VERTEX_SHADER,
                vert);
        int fragmentShader = OpenGLUtils.loadGLShader(GLES20.GL_FRAGMENT_SHADER,
                frag);
        OpenGLUtils.checkGLError("Load Shader");

        textureDataHandle = OpenGLUtils.loadTextureFromPath(c, path);
//        Log.d("TextureDataHandle", "" + textureDataHandle);
        OpenGLUtils.checkGLError("Load Texture");

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glBindAttribLocation(mProgram, 1, OpenGLUtils.POSITION_ATTRIB);
        GLES20.glBindAttribLocation(mProgram, 2, OpenGLUtils.NORMAL_ATTRIB);
        GLES20.glBindAttribLocation(mProgram, 3, OpenGLUtils.TEX_COORDINATE_ATTRIB);

        GLES20.glLinkProgram(mProgram);
        OpenGLUtils.checkGLError("Link Program");
    }

    public void createProgram(Bitmap bitmap) {
        String vert = getVertexShader();
        String frag = getFragmentShader();

        int vertexShader = OpenGLUtils.loadGLShader(GLES20.GL_VERTEX_SHADER,
                vert);
        int fragmentShader = OpenGLUtils.loadGLShader(GLES20.GL_FRAGMENT_SHADER,
                frag);
        OpenGLUtils.checkGLError("Load Shader");

        textureDataHandle = OpenGLUtils.loadTextureBitmap(bitmap);
//        Log.d("TextureDataHandle", "" + textureDataHandle);
        OpenGLUtils.checkGLError("Load Texture");

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glBindAttribLocation(mProgram, 1, OpenGLUtils.POSITION_ATTRIB);
        GLES20.glBindAttribLocation(mProgram, 2, OpenGLUtils.NORMAL_ATTRIB);
        GLES20.glBindAttribLocation(mProgram, 3, OpenGLUtils.TEX_COORDINATE_ATTRIB);

        GLES20.glLinkProgram(mProgram);
        OpenGLUtils.checkGLError("Link Program");
    }

    public void draw(BaseCamera camera, Light light, float[] modelMatrix, Mesh mesh) {
    }

    public void draw(BaseCamera camera, float[] modelMatrix, Mesh mesh) {
    }
}
