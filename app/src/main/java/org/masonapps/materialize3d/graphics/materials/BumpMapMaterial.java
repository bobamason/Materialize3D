package org.masonapps.materialize3d.graphics.materials;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import org.masonapps.materialize3d.R;
import org.masonapps.materialize3d.graphics.Light;
import org.masonapps.materialize3d.graphics.OpenGLUtils;
import org.masonapps.materialize3d.graphics.cameras.BaseCamera;
import org.masonapps.materialize3d.graphics.effects.BaseEffect;
import org.masonapps.materialize3d.graphics.meshes.Mesh;
import org.masonapps.materialize3d.utils.Constants;


/**
 * Created by Bob on 3/1/2015.
 */
public class BumpMapMaterial extends Material {
    public static final String TEXTURE = "texture";
    public static final String NORMAL_MAP = "normalmap";
    public static final String HEIGHT_MAP = "heightmap";
    public static final String MVP_UNIFORM = "u_MVPMatrix";
    public static final String MODEL_MATRIX_UNIFORM = "u_MVMatrix";
    public static final String CAM_POS_UNIFORM = "u_CamPos";
    public static final String LIGHTING_PARAMS = "lightingParams";
    public static final String REPEAT = "u_Repeat";
    public static final String START = "u_Start";
    public static final String TINT = "u_Tint";
    public static final String TIME = "u_Time";
    public static final String FRAG_SHADER_UNIFORM_PREFIX =
            "precision mediump float;"
                    + "uniform vec4 u_Tint;"
                    + "uniform sampler2D " + TEXTURE + ";"
                    + "uniform sampler2D " + NORMAL_MAP + ";"
                    + "uniform sampler2D " + HEIGHT_MAP + ";"
                    + "uniform float " + TIME + ";"
                    + "uniform vec4 " + LIGHTING_PARAMS + ";";
    public static final String V_TEX_COORDINATE = "v_TexCoordinate";
    public static final String MAP_TEX_COORDINATE = "mapTexCoordinate";
    public static final String EYE_VEC = "eyeVec";
    public static final String LIGHT_VEC = "lightVec";
    public static final String VARYING_PREFIX = "" +
            "varying vec3 " + EYE_VEC + ";" +
            "varying vec3 " + LIGHT_VEC + ";"
            + "varying vec2 " + V_TEX_COORDINATE + ";"
            + "varying vec2 " + MAP_TEX_COORDINATE + ";";
    public static final String LIGHT_POS_UNIFORM = "lightPos";
    public static final String DEFAULT_VERTEX_SHADER = "uniform mat4 " + MVP_UNIFORM + ";"
            + "uniform mat4 " + MODEL_MATRIX_UNIFORM + ";"
            + "attribute vec4 " + OpenGLUtils.POSITION_ATTRIB + ";"
            + "attribute vec3 " + OpenGLUtils.NORMAL_ATTRIB + ";"
            + "attribute vec3 " + OpenGLUtils.TANGENT_ATTRIB + ";"
            + "attribute vec2 " + OpenGLUtils.TEX_COORDINATE_ATTRIB + ";"
            + "uniform vec2 " + REPEAT + ";"
            + "uniform vec2 " + START + ";"
            + "uniform vec3 " + LIGHT_POS_UNIFORM + ";"
            + "uniform vec3 " + CAM_POS_UNIFORM + ";"

            + VARYING_PREFIX

            + "void main()"
            + "{"
            + "   vec3 pos = vec3(" + MODEL_MATRIX_UNIFORM + " * " + OpenGLUtils.POSITION_ATTRIB + ");"
            + "   " + MAP_TEX_COORDINATE + " = " + OpenGLUtils.TEX_COORDINATE_ATTRIB + " / 1.024 + vec2(0.012) ;" +
            "   " + V_TEX_COORDINATE + " = (" + OpenGLUtils.TEX_COORDINATE_ATTRIB + " * " + REPEAT + ") + " + START + ";" +
            "     mat3 normalMatrix = transpose(inverse(mat3(" + MODEL_MATRIX_UNIFORM + ")));"
            + "   vec3 t = normalMatrix * " + OpenGLUtils.TANGENT_ATTRIB + ";"
            + "   vec3 n = normalMatrix * " + OpenGLUtils.NORMAL_ATTRIB + ";"
            + "   vec3 b = cross(n, t);"
            + "   vec3 v;" +
            "     vec3 ev = normalize(" + CAM_POS_UNIFORM + " - pos);" +
            "     v.x = dot(ev, t);" +
            "     v.y = dot(ev, b);" +
            "     v.z = dot(ev, n);" +
            " " + EYE_VEC + " = v;" +
            "     vec3 lv = normalize(" + LIGHT_POS_UNIFORM + " - pos);" +
            "     v.x = dot(lv, t);" +
            "     v.y = dot(lv, b);" +
            "     v.z = dot(lv, n);" +
            " " + LIGHT_VEC + " = v;"
            + "   gl_Position = " + MVP_UNIFORM + " * " + OpenGLUtils.POSITION_ATTRIB + ";"
            + "}";
    public static final String CONST_PREFIX = "const float sqrt2 = sqrt(2.0);"
            + "const float pix = 1. / 1024.;"
            + "const float pi = 3.1415926;"
            + "const vec2 center = vec2(0.5, 0.5);" +
            "" +
            "  vec3 lighting(vec3 c, vec3 n, vec4 params){" +
            "      vec3 ambient = c * params.x;" +
//            "      float lightAtt = 0.0001;" +
//            "      float att = 1.0 / (1.0 + lightAtt * pow(length(" + LIGHT_VEC + "), 2.0));" +
            "      float diffuseCoef = max(dot(n, normalize(" + LIGHT_VEC + ")), 0.0);" +
            "      vec3 diffuse = c * diffuseCoef * params.y;" +
            "      vec3 specular = vec3(0.0);" +
            "      if(diffuseCoef > 0.0){" +
            "          float specCoef = pow(clamp(dot(normalize(" + EYE_VEC + "), reflect(-normalize(" + LIGHT_VEC + "), n)), 0.0, 1.0), params.w);" +
            "          specular = specCoef * vec3(1.0) * params.z;" +
            "      }" +
            "      return clamp(ambient + diffuse + specular, 0.0, 1.0);" +
            "  }" +
            "" +
            "  vec2 parallax(vec3 ev, float h){" +
            "      return vec2(ev.x, -ev.y) * h;" +
            "  }" +
            "" +
            "float luminance(vec3 c){" +
            "    return c.r * 0.26 + c.g * 0.62 + c.b * 0.12;" +
            "}" +
            ""
            + "vec3 edgeDetect(float factor, vec2 tc, float s){"
            + "   vec4 e = texture2D(" + BumpMapMaterial.TEXTURE + ", tc) * (4. + factor);"
            + "   e += texture2D(" + BumpMapMaterial.TEXTURE + ", tc + vec2(-pix * s, 0.)) * -1.;"
            + "   e += texture2D(" + BumpMapMaterial.TEXTURE + ", tc + vec2(pix * s, 0.)) * -1.;"
            + "   e += texture2D(" + BumpMapMaterial.TEXTURE + ", tc + vec2(0., -pix * s)) * -1.;"
            + "   e += texture2D(" + BumpMapMaterial.TEXTURE + ", tc + vec2(0., pix * s)) * -1.;"
            + "   return e.rgb;"
            + "}"
            + "vec2 polar2cartesian(float radius, float angle){"
            + "   return vec2(cos(angle), sin(angle)) * radius;"
            + "}";
    private static final String TAG = "BumpMapMaterial";
    public int normalMapDataHandle = -1;
    public int heightMapDataHandle = -1;
    public int normalMapUniformHandle;
    public int heightMapUniformHandle;
    protected int mPositionHandle;
    protected int mMVPMatrixHandle;
    protected int modelMatrixHandle;
    protected int mNormalHandle;
    protected float[] mvpMatrix = new float[16];
    protected float[] mvMatrix = new float[16];
    protected int textureUniformHandle;
    protected int textureCoordinateHandle;
    protected float[] textureRepeat = {1f, 1f};
    protected float[] textureStart = {0f, 0f};
    protected float[] tint = {1.0f, 1.0f, 1.0f, 1.0f};
    protected float time = 0;
    protected int mRepeatHandle;
    protected int mTexStartHandle;
    protected int mTintHandle;
    protected int mTimeHandle;
    protected int lightPosHandle;
    protected int lightingParamsHandle;
    protected int camPosHandle;
    private LightingParams lightingParams = new LightingParams(0.5f, 0.5f, 0.1f, 4f);
    private int tangentHandle;

    public BumpMapMaterial() {
        super();
    }

    @Override
    public String getVertexShader() {
        return DEFAULT_VERTEX_SHADER;
    }

    @Override
    public String getFragmentShader() {
        return FRAG_SHADER_UNIFORM_PREFIX
                + VARYING_PREFIX
                + CONST_PREFIX

                + "void main()"
                + "{" +
                "    vec2 tc = " + V_TEX_COORDINATE + ";" +
                "    vec2 map_tc = " + MAP_TEX_COORDINATE + ";" +
                "    float h = texture2D(" + BumpMapMaterial.HEIGHT_MAP + ", map_tc).r * 0.012 - 0.06;" +
                "    vec2 offset = parallax(normalize(" + BumpMapMaterial.EYE_VEC + "), h);" +
                "    tc += offset;" +
                "    map_tc += offset;" +
                "    vec3 c = texture2D(" + TEXTURE + ", tc).rgb * " + TINT + ".rgb;" +
                "    vec3 normal = normalize(2.0 * texture2D(" + BumpMapMaterial.NORMAL_MAP + ", map_tc).rgb - vec3(1.0));" +
                "    c = lighting(c, normal, " + BumpMapMaterial.LIGHTING_PARAMS + ");" +
//                "    gl_FragColor = vec4(texture2D(" + BumpMapMaterial.NORMAL_MAP + ", map_tc).rgb, 1.0);"+
                    "    gl_FragColor = vec4(c.rgb, " + TINT + ".a);"+
                "}";
    }

    @Override
    public void draw(BaseCamera camera, Light light, float[] modelMatrix, Mesh mesh) {
        if (!mesh.loaded || mProgram == -1 || heightMapDataHandle == -1 || normalMapDataHandle == -1) {
//            Log.d(TAG, "skipping frame mesh.loaded = " + mesh.loaded + " mProgram = " + mProgram + " heightMapDataHandle = " + +heightMapDataHandle + " normalMapDataHandle = " + normalMapDataHandle + " textureDataHandle = " + textureDataHandle);
            return;
        }

        GLES20.glUseProgram(mProgram);
        OpenGLUtils.checkGLError("use program");
        if (textureDataHandle != -1) {
            textureUniformHandle = GLES20.glGetUniformLocation(mProgram,
                    TEXTURE);
        }

        normalMapUniformHandle = GLES20.glGetUniformLocation(mProgram,
                NORMAL_MAP);

        heightMapUniformHandle = GLES20.glGetUniformLocation(mProgram,
                HEIGHT_MAP);

//        Log.d(TAG, "textureUniformHandle = " + textureUniformHandle + ", normalMapUniformHandle = " + normalMapUniformHandle);
        textureCoordinateHandle = GLES20.glGetAttribLocation(mProgram,
                OpenGLUtils.TEX_COORDINATE_ATTRIB);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,
                MVP_UNIFORM);
        modelMatrixHandle = GLES20.glGetUniformLocation(mProgram,
                MODEL_MATRIX_UNIFORM);
        camPosHandle = GLES20.glGetUniformLocation(mProgram,
                CAM_POS_UNIFORM);

        mPositionHandle = GLES20
                .glGetAttribLocation(mProgram, OpenGLUtils.POSITION_ATTRIB);
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, OpenGLUtils.NORMAL_ATTRIB);
        tangentHandle = GLES20.glGetAttribLocation(mProgram, OpenGLUtils.TANGENT_ATTRIB);

        mRepeatHandle = GLES20.glGetUniformLocation(mProgram,
                REPEAT);
        mTexStartHandle = GLES20.glGetUniformLocation(mProgram, START);
        mTintHandle = GLES20.glGetUniformLocation(mProgram,
                TINT);
        mTimeHandle = GLES20.glGetUniformLocation(mProgram,
                TIME);
        lightPosHandle = GLES20.glGetUniformLocation(mProgram,
                LIGHT_POS_UNIFORM);
        lightingParamsHandle = GLES20.glGetUniformLocation(mProgram,
                LIGHTING_PARAMS);

        GLES20.glUniform2f(mRepeatHandle, textureRepeat[0], textureRepeat[1]);
        GLES20.glUniform2f(mTexStartHandle, textureStart[0], textureStart[1]);

        GLES20.glUniform3f(lightPosHandle, light.getPos().x, light.getPos().y, light.getPos().z);
        GLES20.glUniform3f(camPosHandle, camera.getPos().x, camera.getPos().y, camera.getPos().z);
        GLES20.glUniform4f(lightingParamsHandle, lightingParams.getAmbient(), lightingParams.getDiffuse(), lightingParams.getSpecular(), lightingParams.getShininess());

        GLES20.glUniformMatrix4fv(modelMatrixHandle, 1, false, modelMatrix, 0);

        Matrix.multiplyMM(mvMatrix, 0, camera.getViewMatrix(), 0, modelMatrix, 0);
//        GLES20.glUniformMatrix4fv(modelMatrixHandle, 1, false, mvMatrix, 0);

        Matrix.multiplyMM(mvpMatrix, 0, camera.getProjectionMatrix(), 0, mvMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        if (textureDataHandle != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
            GLES20.glUniform1i(textureUniformHandle, 0);
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, normalMapDataHandle);
        GLES20.glUniform1i(normalMapUniformHandle, 1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, heightMapDataHandle);
        GLES20.glUniform1i(heightMapUniformHandle, 2);


        GLES20.glUniform4fv(mTintHandle, 1, tint, 0);
        GLES20.glUniform1f(mTimeHandle, time);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mesh.getVertexBufferID());
        OpenGLUtils.checkGLError("bind ARRAY_BUFFER");
        GLES20.glVertexAttribPointer(mPositionHandle, Mesh.POSITION_DATA_SIZE,
                GLES20.GL_FLOAT, false, mesh.vertexStride, 0);
        OpenGLUtils.checkGLError("use mPositionHandle");
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mNormalHandle, Mesh.NORMAL_DATA_SIZE,
                GLES20.GL_FLOAT, false, mesh.vertexStride, Mesh.NORMAL_OFFSET);
        OpenGLUtils.checkGLError("use mNormalHandle");
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        GLES20.glVertexAttribPointer(tangentHandle, Mesh.TANGENT_DATA_SIZE,
                GLES20.GL_FLOAT, false, mesh.vertexStride, Mesh.TANGENT_OFFSET);
        OpenGLUtils.checkGLError("use tangentHandle");
        GLES20.glEnableVertexAttribArray(tangentHandle);

        GLES20.glVertexAttribPointer(textureCoordinateHandle,
                Mesh.TEXTURE_DATA_SIZE, GLES20.GL_FLOAT, false, mesh.vertexStride,
                Mesh.TEXTURE_OFFSET);
        OpenGLUtils.checkGLError("use textureCoordinateHandle");
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mesh.getIndexBufferID());
        OpenGLUtils.checkGLError("bind ELEMENT_ARRAY_BUFFER");
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.indicesLength,
                GLES20.GL_UNSIGNED_SHORT, 0);
        OpenGLUtils.checkGLError("use GL_ELEMENT_ARRAY_BUFFER Texture");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);

    }

    public void setTextureRepeat(float s, float t) {
        textureRepeat[0] = s;
        textureRepeat[1] = t;
    }

    public void setTextureStart(float s, float t) {
        textureStart[0] = -s;
        textureStart[1] = t;
    }

    public void setTint(float[] tint) {
        this.tint = tint;
    }

    public void setTintRGB(int r, int g, int b) {
        tint[0] = r / 255f;
        tint[1] = g / 255f;
        tint[2] = b / 255f;
        tint[3] = 1f;
    }

    @Override
    public void dispose() {
        GLES20.glDeleteProgram(mProgram);
        GLES20.glDeleteTextures(1, new int[]{textureDataHandle}, 0);
        GLES20.glFlush();
        textureDataHandle = -1;
    }

    public void setTime(float t) {
        time = t;
    }

    public void setTexture(Context c, int texID) {
        GLES20.glDeleteTextures(1, new int[]{textureDataHandle}, 0);
        GLES20.glFlush();
        textureDataHandle = -1;
        if (texID != -1) {
            textureDataHandle = OpenGLUtils.loadTextureResource(c, texID);
            OpenGLUtils.checkGLError("Load Texture");
        }
    }

    public void setNormalMap(Context c, int texID) {
        GLES20.glDeleteTextures(1, new int[]{normalMapDataHandle}, 0);
        GLES20.glFlush();
        normalMapDataHandle = -1;
        if (texID != -1) {
            normalMapDataHandle = OpenGLUtils.loadTextureResource(c, texID);
            OpenGLUtils.checkGLError("Load Texture");
        }
    }

    public void setHeightMap(Context c, int texID) {
        GLES20.glDeleteTextures(1, new int[]{heightMapDataHandle}, 0);
        GLES20.glFlush();
        heightMapDataHandle = -1;
        if (texID != -1) {
            heightMapDataHandle = OpenGLUtils.loadTextureResource(c, texID);
            OpenGLUtils.checkGLError("Load Texture");
        }
    }

    public void setNormalMap(Context c, String path) {
        GLES20.glDeleteTextures(1, new int[]{normalMapDataHandle}, 0);
        GLES20.glFlush();
        normalMapDataHandle = -1;
        normalMapDataHandle = OpenGLUtils.loadTextureFromPath(c, path);
        OpenGLUtils.checkGLError("Load Texture");
    }

    public void setHeightMap(Context c, String path) {
        GLES20.glDeleteTextures(1, new int[]{heightMapDataHandle}, 0);
        GLES20.glFlush();
        heightMapDataHandle = -1;
        heightMapDataHandle = OpenGLUtils.loadTextureFromPath(c, path);
        OpenGLUtils.checkGLError("Load Texture");
    }

    public void setTexture(Context c, String path) {
        GLES20.glDeleteTextures(1, new int[]{textureDataHandle}, 0);
        GLES20.glFlush();
        textureDataHandle = -1;
        textureDataHandle = OpenGLUtils.loadTextureFromPath(c, path);
        OpenGLUtils.checkGLError("Load Texture");
    }

    public void setTint(float x, float y, float z) {
        tint[0] = x;
        tint[1] = y;
        tint[2] = z;
        tint[3] = 1f;
    }

    public void setEffect(Context context, BaseEffect effect) {
        BaseEffect.MaterialType type = effect.getType();
            String fragmentShader = FRAG_SHADER_UNIFORM_PREFIX
                    + VARYING_PREFIX
                    + CONST_PREFIX

                    + "void main()"
                    + "{" +
                    "    vec2 tc = " + V_TEX_COORDINATE + ";" +
                    "    vec2 map_tc = " + MAP_TEX_COORDINATE + ";" +
                    "    float h = texture2D(" + BumpMapMaterial.HEIGHT_MAP + ", map_tc).r * 0.012 - 0.006;" +
                    "    vec2 offset = parallax(normalize(" + BumpMapMaterial.EYE_VEC + "), h);" +
                    "    tc += offset;" +
                    "    map_tc += offset;" +
                    (type == BaseEffect.MaterialType.COLOR ? "    vec3 c = " + TINT + ".rgb;" : "    vec3 c = texture2D(" + TEXTURE + ", tc).rgb * " + TINT + ".rgb;") +
                    "    vec3 normal = normalize(2.0 * texture2D(" + BumpMapMaterial.NORMAL_MAP + ", map_tc).rgb - vec3(1.0));" +
                    "    c = lighting(c, normal, " + BumpMapMaterial.LIGHTING_PARAMS + ");" +
//                    "    gl_FragColor = vec4(texture2D(" + BumpMapMaterial.NORMAL_MAP + ", map_tc).rgb, 1.0);"+
                    "    gl_FragColor = vec4(c.rgb, " + TINT + ".a);"+
                    "}";


        if(type == BaseEffect.MaterialType.RESOURCE){
            setTexture(context, effect.getTextureResource());
        }else if(type == BaseEffect.MaterialType.IMAGE){
            setTexture(context, Constants.FILENAME_DEFAULT);
            if(textureDataHandle == -1) setTexture(context, R.drawable.default_image);
        }

        final int color = effect.getColor();
        setTintRGB(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF);
        setTextureRepeat(effect.getTextureRepeat() / 1.024f, effect.getTextureRepeat() / 1.024f);
        setTextureStart(-0.012f, 0.012f);
        setLightingParams(effect.getLightingParams());
        setShader(DEFAULT_VERTEX_SHADER, fragmentShader);
//        setTexture(context, R.drawable.default_image);
//        setHeightMap(context, R.drawable.default_heightmap);
//        setNormalMap(context, R.drawable.default_normalmap);
    }

    public void setShader(String vert, String frag) {

        GLES20.glDeleteProgram(mProgram);
        mProgram = -1;
        OpenGLUtils.checkGLError("Delete Program");

        int vertexShader = OpenGLUtils.loadGLShader(GLES20.GL_VERTEX_SHADER,
                vert);
        OpenGLUtils.checkGLError("Load Vertex Shader");
        int fragmentShader = OpenGLUtils.loadGLShader(GLES20.GL_FRAGMENT_SHADER,
                frag);
        OpenGLUtils.checkGLError("Load Fragment Shader");

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

    public void setLightingParams(LightingParams lightingParams) {
        this.lightingParams = lightingParams;
    }

    public void setAlpha(float a) {
        tint[3] = a;
    }

    public void updateTexture(Context context, BaseEffect effect) {
        BaseEffect.MaterialType type = effect.getType();
        if(type == BaseEffect.MaterialType.RESOURCE){
            setTexture(context, effect.getTextureResource());
        }else if(type == BaseEffect.MaterialType.IMAGE){
            setTexture(context, Constants.FILENAME_DEFAULT);
            if(textureDataHandle == -1) setTexture(context, R.drawable.default_image);
        }

        if(heightMapDataHandle == -1 || normalMapDataHandle == -1) {
            setHeightMap(context, R.drawable.default_heightmap);
            setNormalMap(context, R.drawable.default_normalmap);
        }
    }

    public static class LightingParams{

        private float diffuse;
        private float specular;
        private float ambient;
        private float shininess;

        public LightingParams(float ambient, float diffuse, float specular, float shininess) {
            this.ambient = ambient;
            this.diffuse = diffuse;
            this.specular = specular;
            this.shininess = shininess;
        }

        public float getDiffuse() {
            return diffuse;
        }

        public float getSpecular() {
            return specular;
        }

        public float getAmbient() {
            return ambient;
        }

        public float getShininess() {
            return shininess;
        }
    }
}
