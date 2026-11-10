package dev.licaza.huitzil;

import dev.licaza.huitzil.game.Game;
import dev.licaza.huitzil.graphics.Renderer;
import dev.licaza.huitzil.input.Input;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    // The game logic always advances in steps of 1/60 s, whatever the frame rate
    private static final double STEP = 1.0 / 60.0;
    // Longest frame time accepted, so a stall (like dragging the window) doesn't fast-forward the game
    private static final double MAX_FRAME_TIME = 0.25;

    private final int width = 1280;
    private final int height = 720;
    private long window;

    private Game game;

    public void run() {
        init();

        double previous = glfwGetTime();
        double lag = 0;
        while (!glfwWindowShouldClose(window)) {
            double now = glfwGetTime();
            lag += Math.min(now - previous, MAX_FRAME_TIME);
            previous = now;

            glfwPollEvents();
            if (Input.keys[GLFW_KEY_ESCAPE]) glfwSetWindowShouldClose(window, true);

            while (lag >= STEP) {
                game.update((float) STEP);
                lag -= STEP;
            }

            glClear(GL_COLOR_BUFFER_BIT);
            game.render();
            glfwSwapBuffers(window);
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

        game = new Game(new Renderer(Game.WIDTH, Game.HEIGHT));
    }

    public static void main(String[] args) {
        new Main().run();
    }

}
