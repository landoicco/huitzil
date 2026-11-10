package dev.licaza.huitzil.graphics;

/**
 * Draws text with the pixel font in textures/font.png: one 6 x 8 px cell per glyph,
 * in the order of GLYPHS. Characters missing from the font (like spaces) leave a gap.
 */
public class Text {

    private static final String GLYPHS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ.:";
    private static final float CELL_WIDTH = 6, CELL_HEIGHT = 8;
    private static final float PIXELS_PER_UNIT = 16;

    private final Renderer renderer;
    private final Texture font = new Texture("textures/font.png");

    public Text(Renderer renderer) {
        this.renderer = renderer;
    }

    /** Width of the text in world units. */
    public static float width(String text, int scale) {
        return text.length() * CELL_WIDTH * scale / PIXELS_PER_UNIT;
    }

    /** Height of a line of text in world units. */
    public static float height(int scale) {
        return CELL_HEIGHT * scale / PIXELS_PER_UNIT;
    }

    /** Draws text centered horizontally on x, with y as its vertical center. Scale 1 is the font's pixel size. */
    public void drawCentered(String text, float x, float y, int scale) {
        float charWidth = width("X", scale);
        float charHeight = height(scale);
        float glyphWidth = 1.0f / GLYPHS.length();

        float charX = x - text.length() * charWidth / 2 + charWidth / 2;
        for (char c : text.toUpperCase().toCharArray()) {
            int glyph = GLYPHS.indexOf(c);
            if (glyph >= 0) {
                renderer.draw(font, charX, y, charWidth, charHeight, 0, glyph * glyphWidth, 0, glyphWidth, 1);
            }
            charX += charWidth;
        }
    }
}
