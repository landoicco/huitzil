package dev.licaza.huitzil;

import dev.licaza.huitzil.game.Game;
import dev.licaza.huitzil.graphics.Image;
import dev.licaza.huitzil.graphics.Renderer;
import dev.licaza.huitzil.input.Input;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

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
    private long handCursor;
    private boolean overLink;

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
            updateCursor();

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

        setIcon();

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode != null) {
            glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        }

        glfwSetKeyCallback(window, new Input());
        // Window pixels (origin at the top left) to world units (origin at the center)
        glfwSetCursorPosCallback(window, (w, x, y) ->
                game.mouseMoved((float) (x / width - 0.5) * Game.WIDTH, (float) (0.5 - y / height) * Game.HEIGHT));
        glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) game.click();
        });
        handCursor = glfwCreateStandardCursor(GLFW_HAND_CURSOR);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        System.out.println("OpenGL: " + glGetString(GL_VERSION));

        game = new Game(new Renderer(Game.WIDTH, Game.HEIGHT));
    }

    // Title bar and taskbar icon; macOS takes it from the app bundle instead
    private void setIcon() {
        if (glfwGetPlatform() == GLFW_PLATFORM_COCOA) return;
        Image icon = Image.load("textures/icon.png");
        try (GLFWImage.Buffer images = GLFWImage.malloc(1)) {
            images.get(0).set(icon.width(), icon.height(), MemoryUtil.memByteBuffer(icon.pixels()));
            glfwSetWindowIcon(window, images);
        }
    }

    // Hand cursor over the portfolio link, default cursor everywhere else
    private void updateCursor() {
        boolean over = game.isLinkHovered();
        if (over != overLink) {
            glfwSetCursor(window, over ? handCursor : NULL);
            overLink = over;
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

}
