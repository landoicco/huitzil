package dev.licaza.huitzil.graphics;

import dev.licaza.huitzil.math.Matrix4f;
import dev.licaza.huitzil.utils.BufferUtils;
import dev.licaza.huitzil.utils.ShaderUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    public static final int VERTEX_ATTRIB = 0;
    public static final int TCOORD_ATTRIB = 1;
    private final int ID;
    private final Map<String, Integer> locationCache = new HashMap<>();
    // Reused for every matrix upload, so drawing doesn't allocate native memory each sprite
    private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(new float[16]);

    public Shader(String vertex, String fragment) {
        ID = ShaderUtils.load(vertex, fragment);
    }

    // A missing uniform is cached too (as -1, which GL ignores), so it's only reported once
    public int getUniform(String name) {
        return locationCache.computeIfAbsent(name, key -> {
            int location = glGetUniformLocation(ID, key);
            if (location == -1) System.err.println("Could not find uniform variable " + key);
            return location;
        });
    }

    public void setUniform1i(String name, int value) {
        glUniform1i(getUniform(name), value);
    }

    public void setUniform2f(String name, float x, float y) {
        glUniform2f(getUniform(name), x, y);
    }

    public void setUniform3f(String name, float x, float y, float z) {
        glUniform3f(getUniform(name), x, y, z);
    }

    public void setUniformMatrix4f(String name, Matrix4f matrix) {
        glUniformMatrix4fv(getUniform(name), false, matrixBuffer.put(0, matrix.elements));
    }

    public void enable() {
        glUseProgram(ID);
    }
}
