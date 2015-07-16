package org.masonapps.materialize3d.graphics;

/**
 * Created by Bob on 10/20/2014.
 */
public class Light {

    protected Vector3 pos;

    public Light() {
        pos = new Vector3();
    }

    public Light(float x, float y, float z) {
        pos = new Vector3(x, y, z);
    }

    public void set(float x, float y, float z) {
        pos.set(x, y, z);
    }

    public Vector3 getPos() {
        return pos;
    }

    public void set(Vector3 vec) {
        this.pos.set(pos);
    }
}
