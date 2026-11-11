package dev.licaza.huitzil.audio;

import dev.licaza.huitzil.utils.BufferUtils;
import dev.licaza.huitzil.utils.FileUtils;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// A looping OGG track, decoded a little at a time: decoding it whole would take tens of MB
public class Music {
    // Four buffers of ~0.4 s each keep it playing through short stalls
    private static final int BUFFER_COUNT = 4;
    private static final int BUFFER_FRAMES = 16384;

    private ByteBuffer file; // stb_vorbis reads from it while playing
    private long decoder;
    private int source, channels, sampleRate;
    private ShortBuffer pcm;

    public Music(String path, float volume) {
        if (!Audio.isAvailable()) return;

        try (InputStream stream = FileUtils.openResource(path)) {
            file = BufferUtils.createByteBuffer(stream.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load music " + path, e);
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer error = stack.mallocInt(1);
            decoder = stb_vorbis_open_memory(file, error, null);
            if (decoder == NULL) throw new IllegalStateException("Could not decode music " + path + ": error " + error.get(0));
            STBVorbisInfo info = STBVorbisInfo.malloc(stack);
            stb_vorbis_get_info(decoder, info);
            channels = info.channels();
            sampleRate = info.sample_rate();
        }
        pcm = ByteBuffer.allocateDirect(BUFFER_FRAMES * channels * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        source = alGenSources();
        alSourcef(source, AL_GAIN, volume);
    }

    public void play() {
        if (!Audio.isAvailable()) return;
        for (int i = 0; i < BUFFER_COUNT; i++) {
            int buffer = alGenBuffers();
            fill(buffer);
            alSourceQueueBuffers(source, buffer);
        }
        alSourcePlay(source);
    }

    /** Refills the buffers OpenAL has finished playing. Call it often, every game step. */
    public void update() {
        if (!Audio.isAvailable()) return;
        for (int processed = alGetSourcei(source, AL_BUFFERS_PROCESSED); processed > 0; processed--) {
            int buffer = alSourceUnqueueBuffers(source);
            fill(buffer);
            alSourceQueueBuffers(source, buffer);
        }
        // A long stall (like dragging the window) can drain every buffer, which stops the source
        if (alGetSourcei(source, AL_SOURCE_STATE) != AL_PLAYING) alSourcePlay(source);
    }

    // Decodes the next piece of the track into the buffer, back from the start at the end
    private void fill(int buffer) {
        pcm.clear();
        int frames = stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
        if (frames == 0) {
            stb_vorbis_seek_start(decoder);
            frames = stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
        }
        pcm.limit(frames * channels);
        alBufferData(buffer, channels == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, sampleRate);
    }
}
