package org.masonapps.materialize3d.graphics;

/**
 * Created by Bob on 10/20/2014.
 */
public class Shaders {

    public static final String vertexShaderVertexColor = "uniform mat4 u_MVPMatrix;"
            + "uniform mat4 u_MVMatrix;"

            + "attribute vec4 a_Position;"
            + "attribute vec3 a_Normal;"
            + "attribute vec3 a_Color;"

            + "varying vec3 v_Position;"
            + "varying vec3 v_Color;"
            + "varying vec3 v_Normal;"
            + "void main()"
            + "{"

            + "   v_Position = vec3(u_MVMatrix * a_Position);"
            + "   v_Color = a_Color;"

            + "   v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));"
            + "   gl_Position = u_MVPMatrix * a_Position;"
            + "}";

    public static final String fragmentShaderVertexColor = "precision mediump float;"

            + "uniform vec3 u_LightPos;"
            + "uniform vec3 u_CamPos;"
            + "uniform float u_Shininess;"

            + "varying vec3 v_Position;"
            + "varying vec3 v_Color;"
            + "varying vec3 v_Normal;"

            + "void main()"
            + "{"
            + "   vec3 lightVector = normalize(u_LightPos - v_Position);"
            + "   vec3 normal = normalize(v_Normal);"
            + "   vec3 camVector = normalize(u_CamPos - v_Position);"
            + "	  vec3 ambient = v_Color * 0.05;"

            + "   float d = max(0.0, dot(normal, lightVector)); "
            + "   vec3 diffuse = v_Color * d * 0.65; "
            + "   vec3 specular = vec3(0.0,0.0,0.0);"
            + "	  if(d > 0.0){"
            + "   	specular = vec3(1.0, 1.0, 1.0) * pow(max(0.0, dot(camVector, reflect(-lightVector, normal))), u_Shininess);"
            + "   }"
            + "   gl_FragColor = vec4(diffuse + specular + ambient, 1.0) ;"
            + "}";

    public static final String vertexShaderTexture = "uniform mat4 u_MVPMatrix;"
            + "uniform mat4 u_MVMatrix;"

            + "attribute vec4 a_Position;"
            + "attribute vec3 a_Normal;"
            + "attribute vec2 a_TexCoordinate;"
            + "uniform vec2 u_Repeat;"

            + "varying vec3 v_Position;"
            + "varying vec3 v_Normal;"
            + "varying vec2 v_TexCoordinate;"

            + "void main()"
            + "{"

            + "   v_Position = vec3(u_MVMatrix * a_Position);"
            + "   v_TexCoordinate = a_TexCoordinate * u_Repeat;"
            + "   v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));"
            + "   gl_Position = u_MVPMatrix * a_Position;"
            + "}";

    public static final String fragmentShaderTexture = "precision mediump float;"

            + "uniform vec3 u_LightPos;"
            + "uniform vec3 u_CamPos;"
            + "uniform float u_Shininess;"
            + "uniform vec4 u_Tint;"
            + "uniform sampler2D u_Texture;"

            + "varying vec3 v_Position;"
            + "varying vec3 v_Normal;"
            + "varying vec2 v_TexCoordinate;"

            + "void main()"
            + "{"
            + "   vec4 v_Color = texture2D(u_Texture, v_TexCoordinate) * u_Tint;"
            + "   vec3 lightVector = normalize(u_LightPos - v_Position);"
            + "   vec3 normal = normalize(v_Normal);"
            + "   vec3 camVector = normalize(u_CamPos - v_Position);"
            + "	  vec3 ambient = v_Color.rgb * 0.05;"

            + "   float d = max(0.0, dot(normal, lightVector)); "
            + "   vec3 diffuse = v_Color.rgb * d * 0.65; "
            + "   vec3 specular = vec3(0.0,0.0,0.0);"
            + "	  if(d > 0.0){"
            + "   	specular = vec3(1.0, 1.0, 1.0) * pow(max(0.0, dot(camVector, reflect(-lightVector, normal))), u_Shininess);"
            + "   }"
            + "   gl_FragColor = vec4(diffuse + specular + ambient, v_Color.a) ;"
            + "}";

    public static final String vertexShaderColored = "uniform mat4 u_MVPMatrix;"
            + "uniform mat4 u_MVMatrix;"
            + "uniform mat4 u_MMatrix;"

            + "attribute vec4 a_Position;"
            + "attribute vec3 a_Normal;"
            + "uniform vec2 u_Skew;"

            + "varying vec3 v_Position;"
            + "varying vec3 v_Normal;"

            + "void main()"
            + "{"

            + "   vec4 pos = u_MMatrix * a_Position;"
            + "   vec4 position = a_Position;"
            + "   position.x = position.x + (pos.z * pos.z / 100.0 * u_Skew[0]);"
            + "   position.y = position.y + (pos.z * pos.z / 100.0 * u_Skew[1]);"

            + "   v_Position = vec3(u_MVMatrix * position);"
            + "   v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));"
            + "   gl_Position = u_MVPMatrix * position;"
            + "}";

    public static final String fragmentShaderColored = "precision mediump float;"

            + "uniform vec3 u_LightPos;"
            + "uniform vec3 u_LightColor;"
            + "uniform vec3 u_CamPos;"
            + "uniform float u_Shininess;"
            + "uniform float u_Alpha;"
            + "uniform vec3 u_Color;"

            + "varying vec3 v_Position;"
            + "varying vec3 v_Normal;"

            + "void main()"
            + "{"
            + "   vec3 lightVector = normalize(u_LightPos - v_Position);"
            + "   vec3 normal = normalize(v_Normal);"
            + "   vec3 camVector = normalize(u_CamPos - v_Position);"
            + "	  vec3 ambient = u_Color * 0.5;"

            + "   float d = max(0.0, dot(normal, lightVector)); "
            + "   vec3 diffuse = u_Color * d * 0.5; "
            + "   vec3 specular = vec3(0.0,0.0,0.0);"
            + "	  if(d > 0.0){"
            + "   	specular = 0.05f * u_LightColor * pow(max(0.0, dot(camVector, reflect(-lightVector, normal))), u_Shininess);"
            + "   }"
            + "   gl_FragColor = vec4(diffuse + ambient + specular, u_Alpha);"
            + "}";
}
