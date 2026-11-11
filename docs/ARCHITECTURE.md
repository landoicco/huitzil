# Huitzil — Architecture

How the game is built and why. For what the game is, see the
[game design document](GDD.md).

## History

Started in 2022 as a learning exercise; rebuilt in 2026 as an original game.
The early commits are a Flappy Bird clone made while learning LWJGL. The 2026
rebuild replaced its art with original assets and added the design document,
sound, packaging and releases, with [Claude Code](https://claude.com/claude-code)
as an AI pair programmer.

## Stack

| | |
|---|---|
| Language | Java 17 |
| Graphics | LWJGL 3: OpenGL 3.3 core, GLFW |
| Audio | LWJGL 3: OpenAL, stb_vorbis |
| Build | Maven, single runnable jar (shade plugin) |
| Packaging | `jpackage`: Windows, macOS and Linux apps with a trimmed Java runtime |
| CI | GitHub Actions |
| Dev environment | Nix flake (`nix develop`) |

## Code structure

```
dev.licaza.huitzil
├── Main          window, game loop
├── audio         audio device, sound effects, music
├── game          game states, hummingbird, columns, scoring, mute
├── graphics      sprite renderer, shader, textures, images, text
├── input         keyboard state
├── math          vector and matrix
└── utils         resource, buffer, shader and browser helpers
```

`Main` owns the window and the loop; `Game` owns everything that happens in
it. Assets live in `src/main/resources` and are loaded from the classpath, so
they work the same from the IDE and from inside the jar.

## Decisions

### Game

- **No game engine.** LWJGL gives direct access to OpenGL, GLFW and OpenAL.
  *Why:* the game is small, and building the loop, renderer and audio by hand
  is the point of the project.
- **Fixed-timestep loop.** The game advances in steps of 1/60 s whatever the
  frame rate, and frames longer than 0.25 s are capped. *Why:* physics behave
  the same on every machine, and a stall (like dragging the window) doesn't
  fast-forward the game.
- **One shader and one unit quad for every sprite.** Position, size, rotation
  and the part of the texture to show are passed on each draw. *Why:* a single
  draw path for the hummingbird, the columns, the background and the text.
- **Pixel art at native resolution.** Textures use nearest-neighbor filtering
  and text scales are multiples of 0.25. *Why:* every pixel stays sharp.
- **Java Preferences for the best score and the mute setting.** *Why:* it
  persists per user on every OS (registry, plist or a file under the home
  folder) without the game managing files.
- **Links open with each OS's own launcher** (`rundll32`, `open`,
  `xdg-open`). *Why:* `java.awt.Desktop` would start AWT next to GLFW, which
  conflicts on macOS.
- **No explicit cleanup of GPU and audio resources.** Textures, shaders,
  sounds and the music decoder live as long as the game, and the OS frees them
  when it exits; only the window and the audio device are closed. *Why:*
  freeing them by hand would add code with no visible effect.

### Audio

- **OpenAL through LWJGL.** *Why:* it comes with the library the game already
  uses, with low latency and natives for every platform.
- **Silent fallback.** Without an audio device the game runs without sound
  instead of failing.
- **Sound effects as WAV, loaded whole.** They are tiny and read with
  `javax.sound`, part of the JDK. Only 16-bit mono is accepted: any other
  format fails with a clear error instead of playing wrong.
- **Music as OGG, streamed.** `stb_vorbis` decodes the track into four queued
  buffers of ~0.4 s. *Why:* decoding the 3:20 track whole would take 35 MB of
  memory.
- **Mute through the listener volume.** *Why:* one call silences the music and
  every effect.

### Assets

- **Everything is original or CC0.** The art and sound effects were made for
  the game; the music is CC0 (see the README credits).
- **Generated assets.** The sound effects were synthesized from band-limited
  waveforms, and the app icon, the README banner and the social preview were
  composed from the game's own sprites, by small one-off programs. *Why:* consistent style and
  no third-party assets. The programs aren't kept in the repository, only
  their output.

### Distribution

- **Native apps with Java inside, not a jar.** `jpackage` bundles a runtime
  trimmed to the modules the game uses (`java.desktop`, `java.prefs`,
  `jdk.unsupported`). *Why:* players, recruiters included, should download and
  play without installing Java or typing commands.
- **Zero cost.** The apps aren't signed with paid certificates, so Windows
  SmartScreen and macOS Gatekeeper warn on first launch; the README explains
  how to open them. The macOS app gets a free ad-hoc signature, so macOS asks
  to allow it instead of calling it damaged.
- **Platforms:** Windows x64, Linux x64 and macOS on Apple Silicon. *Why:*
  GitHub no longer offers free Intel Mac runners, and Macs sold since 2020 are
  Apple Silicon.
- **Natives per OS through Maven profiles.** The build picks the LWJGL natives
  of the OS it runs on. *Why:* each CI runner builds its own platform with the
  same command.
- **`-XstartOnFirstThread` set in the macOS package.** *Why:* GLFW must run on
  the main thread on macOS; players never see the flag.

### CI and releases

- **Every pull request** builds the three packages and uploads them as
  workflow artifacts, unzipped so each download is the file itself.
- **A `v*` tag** publishes them on a GitHub Release, which is the public
  download link. The job fails if the tag doesn't match the pom version.
- **The version lives only in `pom.xml`.** It reaches the game through the jar
  manifest and is shown on the splash screens.

### Development environment

- **Nix flake with only what's needed:** the JDK, Maven, `gh` and the native
  libraries GLFW, OpenGL and OpenAL load on Linux. *Why:* a reproducible setup
  with one command, and nothing else to install.
