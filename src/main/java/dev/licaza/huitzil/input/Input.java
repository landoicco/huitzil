package dev.licaza.huitzil.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class Input extends GLFWKeyCallback {

    public static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST + 1];
    private static final boolean[] pressed = new boolean[GLFW.GLFW_KEY_LAST + 1];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        // Unknown keys arrive as GLFW_KEY_UNKNOWN (-1)
        if (key < 0 || key >= keys.length) return;
        keys[key] = action != GLFW.GLFW_RELEASE;
        if (action == GLFW.GLFW_PRESS) pressed[key] = true;
    }

    /** True once per key press: reading it clears it, so holding a key doesn't repeat. */
    public static boolean wasPressed(int key) {
        boolean result = pressed[key];
        pressed[key] = false;
        return result;
    }
}
