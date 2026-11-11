package dev.licaza.huitzil.audio;

import dev.licaza.huitzil.utils.BufferUtils;
import dev.licaza.huitzil.utils.FileUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;

import static org.lwjgl.openal.AL10.*;

// A short sound effect, loaded whole from a 16-bit mono WAV in the resources
public class Sound {
    private int source;

    public Sound(String path) {
        if (!Audio.isAvailable()) return;

        // AudioSystem needs a stream that supports mark and reset
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(FileUtils.openResource(path)))) {
            int sampleRate = (int) stream.getFormat().getSampleRate();
            int buffer = alGenBuffers();
            alBufferData(buffer, AL_FORMAT_MONO16, BufferUtils.createByteBuffer(stream.readAllBytes()), sampleRate);
            source = alGenSources();
            alSourcei(source, AL_BUFFER, buffer);
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new IllegalStateException("Could not load sound " + path, e);
        }
    }

    /** Plays the sound from the start, cutting it off if it was still playing. */
    public void play() {
        if (Audio.isAvailable()) alSourcePlay(source);
    }
}
