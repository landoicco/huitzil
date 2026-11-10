package dev.licaza.huitzil.graphics;

import dev.licaza.huitzil.utils.BufferUtils;
import dev.licaza.huitzil.utils.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.IntBuffer;

// A PNG from the resources as RGBA pixels, the layout OpenGL and GLFW expect
public record Image(int width, int height, IntBuffer pixels) {

    public static Image load(String path) {
        int width, height;
        int[] pixels;
        try (InputStream stream = FileUtils.openResource(path)) {
            BufferedImage image = ImageIO.read(stream);
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load image " + path, e);
        }

        int[] data = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);

            data[i] = a << 24 | b << 16 | g << 8 | r;
        }
        return new Image(width, height, BufferUtils.createIntBuffer(data));
    }
}
