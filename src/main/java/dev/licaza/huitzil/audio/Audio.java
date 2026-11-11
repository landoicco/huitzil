package dev.licaza.huitzil.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.alListenerf;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// The OpenAL device all sounds play on. Without one, the game still runs, silently
public class Audio {
    private static long device;
    private static long context;

    private Audio() {
    }

    public static void init() {
        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            System.err.println("No audio device found, playing without sound");
            return;
        }
        ALCCapabilities capabilities = ALC.createCapabilities(device);
        context = alcCreateContext(device, (IntBuffer) null);
        alcMakeContextCurrent(context);
        AL.createCapabilities(capabilities);
    }

    // Silences every sound at once through the listener's volume
    public static void setMuted(boolean muted) {
        if (isAvailable()) alListenerf(AL_GAIN, muted ? 0 : 1);
    }

    public static boolean isAvailable() {
        return context != NULL;
    }

    public static void destroy() {
        if (context != NULL) {
            alcMakeContextCurrent(NULL);
            alcDestroyContext(context);
        }
        if (device != NULL) alcCloseDevice(device);
    }
}
