package org.masonapps.materialize3d.graphics;

/**
 * Created by Bob on 10/28/2014.
 */
public abstract class WallpaperRender extends Renderer {

    public abstract void onPause();

    public abstract void onResume();

    public abstract void onOffsetsChanged(float offset);

    public abstract void setSensorVector(Vector3 accelVector);

}