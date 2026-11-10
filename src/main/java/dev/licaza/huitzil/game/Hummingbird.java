package dev.licaza.huitzil.game;

import dev.licaza.huitzil.graphics.Renderer;
import dev.licaza.huitzil.graphics.Texture;

public class Hummingbird {

    public static final float X = -5.0f;
    public static final float HITBOX_WIDTH = 0.75f;
    public static final float HITBOX_HEIGHT = 0.55f;

    private static final float SIZE = 1.25f;
    private static final float GRAVITY = 30.0f;
    private static final float FLAP_SPEED = 9.0f;
    private static final float MAX_FALL_SPEED = 15.0f;
    private static final float FRAME_TIME = 0.05f;
    private static final float HOVER_AMPLITUDE = 0.25f;
    private static final float HOVER_SPEED = 4.0f;

    private final Texture texture = new Texture("textures/hummingbird.png");
    private float y, velocity, time;
    private boolean alive;

    public void reset() {
        y = 0;
        velocity = 0;
        time = 0;
        alive = true;
    }

    public void flap() {
        velocity = FLAP_SPEED;
    }

    public void die() {
        alive = false;
    }

    /** Gentle up-and-down bob while waiting for the player to start. */
    public void hover(float dt) {
        time += dt;
        y = (float) Math.sin(time * HOVER_SPEED) * HOVER_AMPLITUDE;
    }

    public void fall(float dt) {
        time += dt;
        velocity = Math.max(velocity - GRAVITY * dt, -MAX_FALL_SPEED);
        y += velocity * dt;
    }

    public float getY() {
        return y;
    }

    public void render(Renderer renderer) {
        // Tilt with the vertical speed: nose up while rising, nose down while falling
        float angle = Math.max(-60, Math.min(25, velocity * 4));
        // The wings stop when it dies
        int frame = alive ? (int) (time / FRAME_TIME) % 2 : 0;
        renderer.draw(texture, X, y, SIZE, SIZE, angle, frame * 0.5f, 0, 0.5f, 1);
    }
}
