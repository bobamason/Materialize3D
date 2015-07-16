package org.masonapps.materialize3d.graphics.meshes;

import org.masonapps.materialize3d.graphics.materials.Material;

/**
 * Created by Bob on 6/14/2015.
 */
public class CubeMesh extends Mesh {

    private float width;
    private float height;
    private float depth;

    public CubeMesh(Material m, float w, float h, float d) {
        super(m);
        this.width = w;
        this.height = h;
        this.depth = d;
        init();
    }

    @Override
    public boolean loadModel() {
        final float hw = width / 2f;
        final float hh = height / 2f;
        final float hd = depth / 2f;
        vertexStride = 11 * 4;
        // vec3 position
        // vec3 normal
        // vec2 texture coordinate
        // vec3 tangent
        vertices = new float[]{
                //front
                //0
                -hw, -hh, hd,
                0f, 0f, 1f,
                0f, 1f,
                1f, 0f, 0f,
                //1
                hw, -hh, hd,
                0f, 0f, 1f,
                1f, 1f,
                1f, 0f, 0f,
                //2
                hw, hh, hd,
                0f, 0f, 1f,
                1f, 0f,
                1f, 0f, 0f,
                //3
                -hw, hh, hd,
                0f, 0f, 1f,
                0f, 0f,
                1f, 0f, 0f,

                // left side
                //4
                -hw, -hh, -hd,
                -1f, 0f, 0f,
                0f, 1f,
                0f, 0f, 1f,
                //5
                -hw, -hh, hd,
                -1f, 0f, 0f,
                1f, 1f,
                0f, 0f, 1f,
                //6
                -hw, hh, hd,
                -1f, 0f, 0f,
                1f, 0f,
                0f, 0f, 1f,
                //7
                -hw, hh, -hd,
                -1f, 0f, 0f,
                0f, 0f,
                0f, 0f, 1f,

                // right side
                //8
                hw, -hh, hd,
                1f, 0f, 0f,
                0f, 1f,
                0f, 0f, -1f,
                //9
                hw, -hh, -hd,
                1f, 0f, 0f,
                1f, 1f,
                0f, 0f, -1f,
                //10
                hw, hh, -hd,
                1f, 0f, 0f,
                1f, 0f,
                0f, 0f, -1f,
                //11
                hw, hh, hd,
                1f, 0f, 0f,
                0f, 0f,
                0f, 0f, -1f,


                // top
                //12
                -hw, hh, hd,
                0f, 1f, 0f,
                0f, 1f,
                1f, 0f, 0f,
                //13
                hw, hh, hd,
                0f, 1f, 0f,
                1f, 1f,
                1f, 0f, 0f,
                //14
                hw, hh, -hd,
                0f, 1f, 0f,
                1f, 0f,
                1f, 0f, 0f,
                //15
                -hw, hh, -hd,
                0f, 1f, 0f,
                0f, 0f,
                1f, 0f, 0f,


                // bottom
                //16
                -hw, -hh, -hd,
                0f, -1f, 0f,
                0f, 1f,
                1f, 0f, 0f,
                //17
                hw, -hh, -hd,
                0f, -1f, 0f,
                1f, 1f,
                1f, 0f, 0f,
                //18
                hw, -hh, hd,
                0f, -1f, 0f,
                1f, 0f,
                1f, 0f, 0f,
                //19
                -hw, -hh, hd,
                0f, -1f, 0f,
                0f, 0f,
                1f, 0f, 0f,


                // back
                //20
                hw, -hh, -hd,
                0f, 0f, -1f,
                0f, 1f,
                -1f, 0f, 0f,
                //21
                -hw, -hh, -hd,
                0f, 0f, -1f,
                1f, 1f,
                -1f, 0f, 0f,
                //22
                -hw, hh, -hd,
                0f, 0f, -1f,
                1f, 0f,
                -1f, 0f, 0f,
                //23
                hw, hh, -hd,
                0f, 0f, -1f,
                0f, 0f,
                -1f, 0f, 0f
                };
        indices = new short[]{
                //front
                0, 2, 3, 2, 0, 1,
                //left side
                4, 6, 7, 6, 4, 5,
                //right side
                8, 10, 11, 10, 8, 9,
                //top
                12, 14, 15, 14, 12, 13,
                //bottom
                16, 18, 19, 18, 16, 17,
                //back
                20, 22, 23, 22, 20, 21

        };
        return true;
    }
}
