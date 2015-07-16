package org.masonapps.materialize3d.graphics.meshes;


import org.masonapps.materialize3d.graphics.materials.Material;

/**
 * Created by ims_2 on 3/23/2015.
 */
public class PlaneMesh extends Mesh {

    private float width;
    private float height;

    public PlaneMesh(Material m, float w, float h) {
        super(m);
        this.width = w;
        this.height = h;
        init();
    }

    @Override
    public boolean loadModel() {
        final float hw = width / 2f;
        final float hh = height / 2f;
        vertexStride = 11 * 4;
        vertices = new float[]{-hw, -hh, 0f,
                0f, 0f, 1f,
                0f, 1f,
                0f, 0f, 1f,

                hw, -hh, 0f,
                0f, 0f, 1f,
                1f, 1f,
                0f, 0f, 1f,

                hw, hh, 0f,
                0f, 0f, 1f,
                1f, 0f,
                0f, 0f, 1f,

                -hw, hh, 0f,
                0f, 0f, 1f,
                0f, 0f,
                0f, 0f, 1f,};
        indices = new short[]{0, 2, 3, 2, 0, 1};
        return true;
    }
}
