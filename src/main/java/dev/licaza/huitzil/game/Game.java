package dev.licaza.huitzil.game;

import dev.licaza.huitzil.audio.Audio;
import dev.licaza.huitzil.audio.Music;
import dev.licaza.huitzil.audio.Sound;
import dev.licaza.huitzil.graphics.Renderer;
import dev.licaza.huitzil.graphics.Text;
import dev.licaza.huitzil.graphics.Texture;
import dev.licaza.huitzil.input.Input;
import dev.licaza.huitzil.utils.Browser;

import java.util.Random;
import java.util.prefs.Preferences;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class Game {

    // The world is 20 x 11.25 units (16:9), drawn at 16 px per unit and scaled x4
    public static final float WIDTH = 20.0f;
    public static final float HEIGHT = 11.25f;

    private static final float SCROLL_SPEED = 4.0f;
    private static final float BACKGROUND_SPEED = 1.0f;
    private static final float PAIR_SPACING = 6.0f;
    private static final int PAIR_COUNT = 4;
    private static final float FIRST_PAIR_X = WIDTH / 2 + 3.0f;
    private static final float GAME_OVER_DELAY = 0.5f;

    private static final String LINK_TEXT = "LICAZA.DEV";
    private static final String LINK_URL = "https://licaza.dev";
    private static final float LINK_Y = -4.75f;
    private static final int LINK_SCALE = 1;
    private static final float LINK_PADDING = 0.25f;
    // Turns the cream text (#fff3d6) into sun yellow (#ffc767)
    private static final float[] LINK_HOVER_TINT = {1.0f, 0.82f, 0.48f};

    // Comes from the pom through the jar manifest; there is no manifest when running outside the jar
    private static final String VERSION = Game.class.getPackage().getImplementationVersion() != null
            ? "V" + Game.class.getPackage().getImplementationVersion()
            : "DEV";
    // The version and the sound toggle hint sit in the bottom corners, small
    private static final float CORNER_SCALE = 0.75f;
    private static final float CORNER_MARGIN = 0.5f;

    private enum State { READY, PLAYING, GAME_OVER }

    private final Renderer renderer;
    private final Text text;
    private final Texture background = new Texture("textures/background.png", true);
    private final Texture column = new Texture("textures/column.png");
    private final Texture overlay = new Texture("textures/overlay.png");
    private final Sound flapSound = new Sound("sounds/flap.wav");
    private final Sound scoreSound = new Sound("sounds/score.wav");
    private final Sound hitSound = new Sound("sounds/hit.wav");
    // Well below the sound effects, so it stays in the background
    private final Music music = new Music("sounds/apple_cider.ogg", 0.35f);

    private final Hummingbird hummingbird = new Hummingbird();
    private final ColumnPair[] pairs = new ColumnPair[PAIR_COUNT];
    private final Random random = new Random();
    private final Preferences preferences = Preferences.userNodeForPackage(Game.class);

    private State state;
    private float backgroundOffset, gameOverTime;
    private float mouseX, mouseY;
    private int score;
    private int best = preferences.getInt("best", 0);
    private boolean muted = preferences.getBoolean("muted", false);

    public Game(Renderer renderer) {
        this.renderer = renderer;
        this.text = new Text(renderer);
        Audio.setMuted(muted);
        music.play();
        for (int i = 0; i < PAIR_COUNT; i++) pairs[i] = new ColumnPair();
        reset();
    }

    private void reset() {
        state = State.READY;
        score = 0;
        hummingbird.reset();
        for (int i = 0; i < PAIR_COUNT; i++) pairs[i].reset(FIRST_PAIR_X + i * PAIR_SPACING, random);
    }

    public void update(float dt) {
        // Read every step, so a press made while input is ignored doesn't fire later
        boolean flap = Input.wasPressed(GLFW_KEY_SPACE);
        if (Input.wasPressed(GLFW_KEY_M)) toggleMute();
        music.update();

        switch (state) {
            case READY -> {
                scrollBackground(dt);
                hummingbird.hover(dt);
                if (flap) {
                    state = State.PLAYING;
                    flap();
                }
            }
            case PLAYING -> {
                scrollBackground(dt);
                if (flap) flap();
                hummingbird.fall(dt);
                moveColumns(dt);
                if (collides()) gameOver();
            }
            case GAME_OVER -> {
                gameOverTime += dt;
                hummingbird.fall(dt);
                if (flap && gameOverTime >= GAME_OVER_DELAY) reset();
            }
        }
    }

    // Works in every state and is remembered between sessions
    private void toggleMute() {
        muted = !muted;
        Audio.setMuted(muted);
        preferences.putBoolean("muted", muted);
    }

    private void flap() {
        hummingbird.flap();
        flapSound.play();
    }

    private void scrollBackground(float dt) {
        backgroundOffset = (backgroundOffset + BACKGROUND_SPEED * dt / WIDTH) % 1.0f;
    }

    private void moveColumns(float dt) {
        for (ColumnPair pair : pairs) {
            pair.move(-SCROLL_SPEED * dt);
            // A pair that leaves the screen on the left comes back on the right with a new gap
            if (pair.getX() < -WIDTH / 2 - ColumnPair.WIDTH) {
                pair.reset(pair.getX() + PAIR_COUNT * PAIR_SPACING, random);
            }
            if (pair.score(Hummingbird.X)) {
                score++;
                scoreSound.play();
            }
        }
    }

    private boolean collides() {
        float y = hummingbird.getY();
        float halfHeight = Hummingbird.HITBOX_HEIGHT / 2;
        if (y + halfHeight > HEIGHT / 2 || y - halfHeight < -HEIGHT / 2) return true;

        for (ColumnPair pair : pairs) {
            if (pair.collides(Hummingbird.X, y, Hummingbird.HITBOX_WIDTH, Hummingbird.HITBOX_HEIGHT)) return true;
        }
        return false;
    }

    private void gameOver() {
        state = State.GAME_OVER;
        gameOverTime = 0;
        hummingbird.die();
        hitSound.play();
        if (score > best) {
            best = score;
            preferences.putInt("best", best);
        }
    }

    /** Updates the mouse position, in world units. */
    public void mouseMoved(float x, float y) {
        mouseX = x;
        mouseY = y;
    }

    /** True when the mouse is over the portfolio link of a splash screen. */
    public boolean isLinkHovered() {
        if (state == State.PLAYING) return false;
        float halfWidth = Text.width(LINK_TEXT, LINK_SCALE) / 2 + LINK_PADDING;
        float halfHeight = Text.height(LINK_SCALE) / 2 + LINK_PADDING;
        return Math.abs(mouseX) < halfWidth && Math.abs(mouseY - LINK_Y) < halfHeight;
    }

    public void click() {
        if (isLinkHovered()) Browser.open(LINK_URL);
    }

    public void render() {
        renderer.draw(background, 0, 0, WIDTH, HEIGHT, 0, backgroundOffset, 0, 1, 1);
        for (ColumnPair pair : pairs) pair.render(renderer, column);
        hummingbird.render(renderer);

        switch (state) {
            case READY -> drawTitleScreen();
            case PLAYING -> text.drawCentered(String.valueOf(score), 0, 4.5f, 2);
            case GAME_OVER -> drawGameOverScreen();
        }
    }

    private void drawTitleScreen() {
        renderer.draw(overlay, 0, 0, WIDTH, HEIGHT);
        text.drawCentered("HUITZIL", 0, 2.0f, 4);
        text.drawCentered("PRESS SPACE", 0, -0.75f, 2);
        drawLink();
    }

    private void drawGameOverScreen() {
        renderer.draw(overlay, 0, 0, WIDTH, HEIGHT);
        text.drawCentered("GAME OVER", 0, 2.5f, 3);
        text.drawCentered("SCORE " + score, 0, 0.5f, 2);
        text.drawCentered("BEST " + best, 0, -0.75f, 2);
        // Only invite a restart once input is accepted again
        if (gameOverTime >= GAME_OVER_DELAY) text.drawCentered("PRESS SPACE", 0, -2.75f, 1);
        drawLink();
    }

    // Footer of both splash screens: the sound toggle hint in the bottom left corner,
    // the portfolio link in the center and the version in the bottom right corner
    private void drawLink() {
        // The link turns sun yellow on hover, so it reads as clickable
        if (isLinkHovered()) renderer.setTint(LINK_HOVER_TINT[0], LINK_HOVER_TINT[1], LINK_HOVER_TINT[2]);
        text.drawCentered(LINK_TEXT, 0, LINK_Y, LINK_SCALE);
        renderer.resetTint();

        float versionX = WIDTH / 2 - CORNER_MARGIN - Text.width(VERSION, CORNER_SCALE) / 2;
        text.drawCentered(VERSION, versionX, LINK_Y, CORNER_SCALE);

        String sound = muted ? "M SOUND OFF" : "M SOUND ON";
        float soundX = -WIDTH / 2 + CORNER_MARGIN + Text.width(sound, CORNER_SCALE) / 2;
        text.drawCentered(sound, soundX, LINK_Y, CORNER_SCALE);
    }
}
