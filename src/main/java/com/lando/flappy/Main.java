package com.lando.flappy;

import com.lando.flappy.input.Input;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    private final int width = 1280;
    private final int height = 720;
    private long window;

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

        window = glfwCreateWindow(width, height, "Flappy", NULL, NULL);
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

        glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        System.out.println("OpenGL: " + glGetString(GL_VERSION));
    }

    private void update() {
        glfwPollEvents();
        if (Input.keys[GLFW_KEY_ESCAPE]) glfwSetWindowShouldClose(window, true);
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        glfwSwapBuffers(window);
    }

    public static void main(String[] args) {
        new Main().run();
    }

}
