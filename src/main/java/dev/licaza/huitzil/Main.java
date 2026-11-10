package dev.licaza.huitzil;

import dev.licaza.huitzil.graphics.Renderer;
import dev.licaza.huitzil.graphics.Texture;
import dev.licaza.huitzil.input.Input;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    // The world is 20 x 11.25 units (16:9), drawn at 16 px per unit and scaled x4
    private static final float WORLD_WIDTH = 20.0f;
    private static final float WORLD_HEIGHT = 11.25f;

    private final int width = 1280;
    private final int height = 720;
    private long window;

    private Renderer renderer;
    private Texture background, column, hummingbird;

    public void run() {
        init();
        while (!glfwWindowShouldClose(window)) {
            update();
            render();
        }
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        if (!glfwInit()) throw new IllegalStateException("Could not initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        window = glfwCreateWindow(width, height, "Huitzil", NULL, NULL);
        if (window == NULL) throw new IllegalStateException("Could not create the GLFW window");

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode != null) {
            glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        }

        glfwSetKeyCallback(window, new Input());

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        System.out.println("OpenGL: " + glGetString(GL_VERSION));

        renderer = new Renderer(WORLD_WIDTH, WORLD_HEIGHT);
        background = new Texture("textures/background.png", true);
        column = new Texture("textures/column.png");
        hummingbird = new Texture("textures/hummingbird.png");
    }

    private void update() {
        glfwPollEvents();
        if (Input.keys[GLFW_KEY_ESCAPE]) glfwSetWindowShouldClose(window, true);
    }

    // Static scene to check the rendering; the game logic replaces it
    private void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        renderer.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        drawColumnPair(1.0f, 0.5f);
        drawColumnPair(7.0f, -1.75f);

        int frame = (int) (glfwGetTime() / 0.05) % 2;
        renderer.draw(hummingbird, -5, 0.5f, 1.25f, 1.25f, 0, frame * 0.5f, 0, 0.5f, 1);

        glfwSwapBuffers(window);
    }

    private void drawColumnPair(float x, float gapCenter) {
        float gap = 3.5f, width = 1.5f, length = 8;
        renderer.draw(column, x, gapCenter - gap / 2 - length / 2, width, length);
        // The top column is the same sprite flipped vertically
        renderer.draw(column, x, gapCenter + gap / 2 + length / 2, width, length, 0, 0, 1, 1, -1);
    }

    public static void main(String[] args) {
        new Main().run();
    }

}
