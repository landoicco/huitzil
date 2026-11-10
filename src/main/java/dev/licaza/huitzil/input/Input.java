package dev.licaza.huitzil.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class Input extends GLFWKeyCallback {

    public static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST + 1];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        // Unknown keys arrive as GLFW_KEY_UNKNOWN (-1)
        if (key < 0 || key >= keys.length) return;
        keys[key] = action != GLFW.GLFW_RELEASE;
    }
}
