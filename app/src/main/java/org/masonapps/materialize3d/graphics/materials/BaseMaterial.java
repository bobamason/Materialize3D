package org.masonapps.materialize3d.graphics.materials;

import android.content.Context;

/**
 * Created by Bob on 10/23/2014.
 */
public interface BaseMaterial {

    void createProgram();

    void createProgram(Context c, int texID);

    void createProgram(Context c, String path);

    String getVertexShader();

    String getFragmentShader();

    void dispose();
}
