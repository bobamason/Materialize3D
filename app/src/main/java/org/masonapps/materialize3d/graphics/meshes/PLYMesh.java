package org.masonapps.materialize3d.graphics.meshes;

import android.content.Context;

import org.masonapps.materialize3d.graphics.materials.Material;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Bob on 10/20/2014.
 */
public class PLYMesh extends Mesh {

    public PLYMesh(Context context, int id, Material m) {
        super(context, m);
        init(id);
    }

    @Override
    public boolean loadModel(int id) {
        InputStream stream = null;
        BufferedReader reader = null;
        int vCount = 0;
        int fCount = 0;
        int i, j;
        boolean isOk = false;
        ArrayList<String> header = new ArrayList<>();

        try {
            stream = context.getResources().openRawResource(id);
            reader = new BufferedReader(new InputStreamReader(stream));

            String line = reader.readLine();

            while (line != null && !line.contains("end_header")) {
                header.add(line);
                line = reader.readLine();
            }
            boolean hasTexture = false;

            for (i = 0; i < header.size(); i++) {
                line = header.get(i);
                if(line.contains("property float u"))hasTexture = true;
                if (line.contains("element vertex")) {
                    int p = line.lastIndexOf(" ") + 1;
                    try {
                        vCount = Integer.parseInt(line.substring(p));
//                        Log.d("PLYModel", "vertex count from file: " + vCount);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                if (line.contains("element face")) {
                    int p = line.lastIndexOf(" ") + 1;
                    try {
                        fCount = Integer.parseInt(line.substring(p));
//                        Log.d("PLYModel", "face count from file: " + fCount);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (hasTexture) {
                vertexStride = 8 * 4;
                vertices = new float[vCount * 8];
                for (i = 0; i < vCount; i++) {
                    line = reader.readLine();
                    if (line != null) {
                        String[] split = line.split(" ");
                        for (j = 0; j < split.length; j++) {
                            try {
                                vertices[i * 8 + j] = Float
                                        .parseFloat(split[j]);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                vertexStride = 9 * 4;
                vertices = new float[vCount * 9];
                for (i = 0; i < vCount; i++) {
                    line = reader.readLine();
                    if (line != null) {
                        String[] split = line.split(" ");
                        for (j = 0; j < split.length; j++) {
                            try {
                                if (j >= 6) {
                                    vertices[i * 9 + j] = Float
                                            .parseFloat(split[j]) / 255f;
                                } else {
                                    vertices[i * 9 + j] = Float
                                            .parseFloat(split[j]);
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            indices = new short[fCount * 3];

            for (i = 0; i < fCount; i++) {
                line = reader.readLine();
                if (line != null) {
                    String[] split = line.split(" ");
                    for (j = 1; j < split.length; j++) {
                        try {
                            indices[i * 3 + (j - 1)] = Short
                                    .parseShort(split[j]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            isOk = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }

        return isOk;
    }
}
