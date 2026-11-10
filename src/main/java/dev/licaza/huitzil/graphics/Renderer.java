package dev.licaza.huitzil.graphics;

import dev.licaza.huitzil.math.Matrix4f;
import dev.licaza.huitzil.math.Vector3f;

import static org.lwjgl.opengl.GL11.*;

/**
 * Draws every sprite in the game with the same unit quad and shader.
 * Coordinates are world units, with the origin at the center of the screen.
 */
public class Renderer {

    // Unit quad centered on the origin; the top of the texture maps to the top of the quad
    private static final float[] VERTICES = {
            -0.5f, -0.5f, 0.0f,
            -0.5f,  0.5f, 0.0f,
             0.5f,  0.5f, 0.0f,
             0.5f, -0.5f, 0.0f,
    };
    private static final float[] TEXTURE_COORDINATES = {
            0, 1,
            0, 0,
            1, 0,
            1, 1,
    };
    private static final byte[] INDICES = {
            0, 1, 2,
            2, 3, 0,
    };

    private final Shader shader;
    private final VertexArray quad;

    public Renderer(float worldWidth, float worldHeight) {
        shader = new Shader("shaders/sprite.vert", "shaders/sprite.frag");
        quad = new VertexArray(VERTICES, INDICES, TEXTURE_COORDINATES);

        float halfWidth = worldWidth / 2, halfHeight = worldHeight / 2;
        shader.enable();
        shader.setUniformMatrix4f("pr_matrix",
                Matrix4f.orthographic(-halfWidth, halfWidth, -halfHeight, halfHeight, -1, 1));
        shader.setUniform1i("tex", 0);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void draw(Texture texture, float x, float y, float width, float height) {
        draw(texture, x, y, width, height, 0, 0, 0, 1, 1);
    }

    /**
     * Draws a sprite centered at (x, y), rotated by angle degrees, showing the texture
     * region that starts at (u, v) and spans (uWidth, vHeight). A negative span flips it.
     */
    public void draw(Texture texture, float x, float y, float width, float height, float angle,
                     float u, float v, float uWidth, float vHeight) {
        Matrix4f model = Matrix4f.translate(new Vector3f(x, y, 0))
                .multiply(Matrix4f.rotate(angle))
                .multiply(Matrix4f.scale(width, height));

        shader.setUniformMatrix4f("ml_matrix", model);
        shader.setUniform2f("uv_offset", u, v);
        shader.setUniform2f("uv_scale", uWidth, vHeight);
        texture.bind();
        quad.render();
    }
}
