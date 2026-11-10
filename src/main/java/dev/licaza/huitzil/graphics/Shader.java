package dev.licaza.huitzil.graphics;

import dev.licaza.huitzil.math.*;
import dev.licaza.huitzil.utils.ShaderUtils;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    public static final int VERTEX_ATTRIB = 0;
    public static final int TCOORD_ATTRIB = 1;
    private final int ID;
    private Map<String, Integer> locationCache = new HashMap<>();

    public Shader(String vertex, String fragment) {
        ID = ShaderUtils.load(vertex, fragment);
    }

    public int getUniform(String name) {
        if (locationCache.containsKey(name)) return locationCache.get(name);

        int result = glGetUniformLocation(ID, name);

        if (result == -1) System.err.println("Could not find uniform variable " + name + " !");
        else locationCache.put(name, result);

        return result;
    }

    public void setUniform1i(String name, int value) {
        glUniform1i((getUniform(name)), value);
    }

    public void setUniform1f(String name, float value) {
        glUniform1f((getUniform(name)), value);
    }

    public void setUniform2f(String name, float x, float y) {
        glUniform2f((getUniform(name)), x, y);
    }

    public void setUniform3f(String name, Vector3f vector) {
        glUniform3f((getUniform(name)), vector.x, vector.y, vector.z);
    }

    public void setUniformMatrix4f(String name, Matrix4f matrix) {
        // Obs: glUniformMatrix4 was recommended but not available
        glUniformMatrix4fv(getUniform(name), false, matrix.toFloatBuffer());
    }

    public void enable() {
        glUseProgram(ID);
    }

    public void disable() {
        glUseProgram(0);
    }

}
