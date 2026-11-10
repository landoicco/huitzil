package dev.licaza.huitzil.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class Texture {

    private int width, height;
    private int texture;

    public Texture(String path) {
        this(path, false);
    }

    // A repeating texture wraps around, so it can scroll by moving its texture coordinates
    public Texture(String path, boolean repeat) {
        this.texture = load(path, repeat ? GL_REPEAT : GL_CLAMP_TO_EDGE);
    }

    private int load(String path, int wrap) {
        Image image = Image.load(path);
        width = image.width();
        height = image.height();

        int result = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, result);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.pixels());
        unbind();
        return result;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

}
