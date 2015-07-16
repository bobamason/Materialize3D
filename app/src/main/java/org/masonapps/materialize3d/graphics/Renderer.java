package org.masonapps.materialize3d.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by Bob on 10/24/2014.
 */
public abstract class Renderer implements GLSurfaceView.Renderer {

    public abstract void setContext(Context context);
}
