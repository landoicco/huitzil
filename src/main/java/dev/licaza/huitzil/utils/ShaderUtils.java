package dev.licaza.huitzil.utils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderUtils {
    private ShaderUtils() {
    }

    public static int load(String vertPath, String fragPath) {
        String vert = FileUtils.loadAsString(vertPath);
        String frag = FileUtils.loadAsString(fragPath);
        return create(vert, frag);
    }

    // Fails loudly: a broken shader would otherwise leave the screen black
    private static int create(String vert, String frag) {
        int vertID = compile(GL_VERTEX_SHADER, vert);
        int fragID = compile(GL_FRAGMENT_SHADER, frag);

        int program = glCreateProgram();
        glAttachShader(program, vertID);
        glAttachShader(program, fragID);
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Could not link shader program:\n" + glGetProgramInfoLog(program));
        }

        glDeleteShader(vertID);
        glDeleteShader(fragID);
        return program;
    }

    private static int compile(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String kind = type == GL_VERTEX_SHADER ? "vertex" : "fragment";
            throw new IllegalStateException("Could not compile " + kind + " shader:\n" + glGetShaderInfoLog(shader));
        }
        return shader;
    }
}
