package org.masonapps.materialize3d.graphics.meshes;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import org.masonapps.materialize3d.graphics.Light;
import org.masonapps.materialize3d.graphics.cameras.BaseCamera;
import org.masonapps.materialize3d.graphics.materials.Material;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Bob on 10/20/2014.
 */
public class Mesh {


    public static final int POSITION_DATA_SIZE = 3;
    public static final int NORMAL_DATA_SIZE = 3;
    public static final int TEXTURE_DATA_SIZE = 2;
    public static final int TANGENT_DATA_SIZE = 3;
    protected Material material;
    public static final int NORMAL_OFFSET = 3 * 4;
    public static final int TEXTURE_OFFSET = 6 * 4;
    public static final int TANGENT_OFFSET = 8 * 4;
    public volatile boolean loaded;
    public int vertexStride;
    protected float[] vertices;

    private int vertexBufferID = 0;
    private int indexBufferID = 0;

    protected Context context;

    protected short[] indices;

//    protected boolean hasTexture;

    public int indicesLength = 0;

    public int verticesLength = 0;

    public Mesh(Material m) {
        loaded = false;
        this.context = null;
//        hasTexture = m.textured;
        material = m;
    }

    protected void init() {
        boolean b = loadModel();
        if (vertices != null && indices != null && b) {
            bindBuffers();
            loaded = true;
        } else {
            Log.e("Mesh", "failed to load model");
            loaded = false;
        }
    }

    public Mesh(Context context, Material m) {
        loaded = false;
        this.context = context;
//        hasTexture = m.textured;
        material = m;
    }

    protected void init(int id) {
        boolean b = loadModel(id);
        if (vertices != null && indices != null && b) {
            bindBuffers();
            loaded = true;
        } else {
            Log.e("Mesh", "failed to load model");
            loaded = false;
        }
    }

    public boolean loadModel(int id) {
        return false;
    }

    public boolean loadModel() {
        return false;
    }

    public void draw(BaseCamera camera, Light light, float[] modelMatrix) {
        if (!loaded) return;
        material.draw(camera, light, modelMatrix, this);
    }

    public void draw(BaseCamera camera, float[] modelMatrix) {
        if (!loaded) return;
        material.draw(camera, modelMatrix, this);
    }

    private void bindBuffers() {
        int[] vboIDs = {0, 0};
        GLES20.glGenBuffers(2, vboIDs, 0);
        vertexBufferID = vboIDs[0];
        indexBufferID = vboIDs[1];
//        Log.d("Mesh Buffer IDs", "{" + vertexBufferID + ", " + indexBufferID + "}");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferID);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferID);

        verticesLength = vertices.length;
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);

        indicesLength = indices.length;
        ShortBuffer indexBuffer = ByteBuffer.allocateDirect(indices.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 2, indexBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        vertexBuffer.limit(0);
        indexBuffer.limit(0);

        vertices = null;
        indices = null;
    }

    public void dispose() {
        loaded = false;
        int[] vboIDs = {vertexBufferID, indexBufferID};
        GLES20.glDeleteBuffers(2, vboIDs, 0);
        vertexBufferID = 0;
        indexBufferID = 0;
//        material.dispose();
    }

    public int getIndexBufferID() {
        return indexBufferID;
    }

    public int getVertexBufferID() {
        return vertexBufferID;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material m) {
        material = m;
    }

    public void setResource(int id) {
        loaded = false;
        int[] vboIDs = {vertexBufferID, indexBufferID};
        GLES20.glDeleteBuffers(2, vboIDs, 0);
        vertexBufferID = 0;
        indexBufferID = 0;
        boolean b = loadModel(id);
        if (vertices != null && indices != null && b) {
            bindBuffers();
            loaded = true;
        } else loaded = false;
    }
}
