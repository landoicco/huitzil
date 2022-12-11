package com.lando.flappy;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.lando.flappy.graphics.Shader;
import com.lando.flappy.input.Input;
import com.lando.flappy.math.Matrix4f;
import com.lando.flappy.level.Level;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements Runnable {

    private int width = 1280;
    private int height = 720;
    private Thread thread;
    private Level level;
    private boolean running = false;
    private long window;

    public void start() {
        running = true;
        thread = new Thread(this, "Game");
        thread.start();
    }

    public void run() {
        init();
        while (running) {
            update();
            render();

            if (glfwWindowShouldClose(window)) running = false;
        }
    }

    private void init() {
        if (!glfwInit()) {
//            TODO: Handle it....
            return;
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        window = glfwCreateWindow(width, height, "Flappy", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Error creating GLFW window!");

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        glfwSetKeyCallback(window, new Input());

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 1.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);
        System.out.println("OpenGL: " + glGetString(GL_VERSION));
        Shader.loadAll();

        Shader.BACKGROUND.enable();
        Matrix4f pr_matrix = Matrix4f.orthographic(
                -10.0f,
                10.0f,
                -10.0f * 9.0f / 16.0f,
                10.0f * 9.0f / 16.0f,
                -1.0f, 1.0f);
        Shader.BACKGROUND.setUniformMatrix4f("pr_matrix", pr_matrix);
        Shader.BACKGROUND.disable();

        level = new Level();

    }

    private void update() {
        glfwPollEvents();
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        level.render();

        // Catch errors
        int error = glGetError();
        if (error != GL_NO_ERROR) System.out.println(error);

        glfwSwapBuffers(window);
    }

    public static void main(String[] args) {
        new Main().start();
    }

}
