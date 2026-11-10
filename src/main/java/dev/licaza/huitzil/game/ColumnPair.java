package dev.licaza.huitzil.game;

import dev.licaza.huitzil.graphics.Renderer;
import dev.licaza.huitzil.graphics.Texture;

import java.util.Random;

/** A column from the ground and one from the top, with a gap between them. */
public class ColumnPair {

    public static final float WIDTH = 1.5f;

    private static final float LENGTH = 8.0f;
    private static final float GAP = 3.5f;
    private static final float GAP_RANGE = 2.5f;

    private float x, gapCenter;
    private boolean scored;

    public void reset(float x, Random random) {
        this.x = x;
        this.gapCenter = (random.nextFloat() * 2 - 1) * GAP_RANGE;
        this.scored = false;
    }

    public void move(float dx) {
        x += dx;
    }

    public float getX() {
        return x;
    }

    /** True the first time the hummingbird is past the center of the pair. */
    public boolean score(float birdX) {
        if (scored || birdX < x) return false;
        scored = true;
        return true;
    }

    public boolean collides(float boxX, float boxY, float boxWidth, float boxHeight) {
        boolean overlapsX = Math.abs(boxX - x) < (boxWidth + WIDTH) / 2;
        boolean outsideGap = boxY - boxHeight / 2 < gapCenter - GAP / 2 || boxY + boxHeight / 2 > gapCenter + GAP / 2;
        return overlapsX && outsideGap;
    }

    public void render(Renderer renderer, Texture texture) {
        renderer.draw(texture, x, gapCenter - GAP / 2 - LENGTH / 2, WIDTH, LENGTH);
        // The top column is the same sprite flipped vertically
        renderer.draw(texture, x, gapCenter + GAP / 2 + LENGTH / 2, WIDTH, LENGTH, 0, 0, 1, 1, -1);
    }
}
