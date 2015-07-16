package org.masonapps.materialize3d.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Bob on 10/20/2014.
 */
public class OpenGLUtils {

    public static final String POSITION_ATTRIB = "a_Position";
    private static final String TAG = "OpenGLUtils";
    public static final String NORMAL_ATTRIB = "a_Normal";
    public static final String TEX_COORDINATE_ATTRIB = "a_TexCoordinate";
    public static final String TANGENT_ATTRIB = "a_Tangent";

    public static int loadGLShader(int type, String code) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e("GLRenderer",
                    "Error compiling shader: "
                            + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    public static void checkGLError(String func) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("GLRenderer", func + ": glError " + error);
            throw new RuntimeException(func + ": glError " + error);
        }
    }

    public static int loadTextureResource(Context context, final int resourceId) {
        if(resourceId == -1){
            Log.e(TAG, "loadTextureResource() -> resourceID = -1");
            return -1;
        }
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (context == null)
            throw new RuntimeException("Error loading texture.");

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false; // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(
                    context.getResources(), resourceId, options);
            if(bitmap == null){
                Log.e(TAG, "loadTextureResource() -> bitmap is null");
                return -1;
            }

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

//        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        return textureHandle[0];
    }

    public static int loadTextureFromPath(Context context, String path) {
        if(path == null || path.isEmpty()){
            Log.e(TAG, "loadTextureFromPath() -> path is empty");
            return -1;
        }
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (context == null)
            throw new RuntimeException("Error loading texture.");

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false; // No pre-scaling

            FileInputStream inputStream = null;
            Bitmap bitmap = null;
            try {
                inputStream = context.openFileInput(path);
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            if (bitmap != null) {
//                Log.d("Decoded Image Size", bitmap.getWidth() + " x " + bitmap.getHeight());
                // Bind to the texture in OpenGL
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

                // Set filtering
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

                // Load the bitmap into the bound texture.
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

                // Recycle the bitmap, since its data has been loaded into OpenGL.
                bitmap.recycle();
            } else {
                return -1;
            }
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

//        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        return textureHandle[0];
    }

    public static int loadTextureBitmap(Bitmap bitmap) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            if (bitmap != null) {
//                Log.d("Decoded Image Size", bitmap.getWidth() + " x " + bitmap.getHeight());
                // Bind to the texture in OpenGL
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

                // Set filtering
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

                // Load the bitmap into the bound texture.
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

                // Recycle the bitmap, since its data has been loaded into OpenGL.
                bitmap.recycle();
            } else {
                return -1;
            }
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }
        return textureHandle[0];
    }
}
