package com.lando.flappy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    private FileUtils() {
    }

    // Opens a file bundled in src/main/resources, so it also works inside the jar
    public static InputStream openResource(String path) {
        InputStream stream = FileUtils.class.getResourceAsStream("/" + path);
        if (stream == null) throw new IllegalArgumentException("Resource not found: " + path);
        return stream;
    }

    public static String loadAsString(String path) {
        try (InputStream stream = openResource(path)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
