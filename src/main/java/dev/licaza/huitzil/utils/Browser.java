package dev.licaza.huitzil.utils;

import java.io.IOException;

public class Browser {
    private Browser() {
    }

    /** Opens a URL in the default browser using each OS's own launcher. */
    public static void open(String url) {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder builder;
        if (os.contains("win")) builder = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
        else if (os.contains("mac")) builder = new ProcessBuilder("open", url);
        else builder = new ProcessBuilder("xdg-open", url);

        // The game may run with its own native libraries on the path; the browser shouldn't inherit them
        builder.environment().remove("LD_LIBRARY_PATH");
        // Nobody reads its output; a browser left writing into the game's pipes could block or die when the game exits
        builder.redirectOutput(ProcessBuilder.Redirect.DISCARD).redirectError(ProcessBuilder.Redirect.DISCARD);
        try {
            builder.start();
        } catch (IOException e) {
            System.err.println("Could not open " + url + ": " + e.getMessage());
        }
    }
}
