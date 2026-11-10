# 🐦 Huitzil

> *Huitzil* comes from *huitzilin*, "hummingbird" in Nahuatl.

A one-button arcade game about a hummingbird darting through the carved stone
columns of an ancient Mexica city at sunset. Tap to beat its wings, slip
through the gaps and see how far it can go. 🌅

Built from scratch with **Java** and **LWJGL** (OpenGL + GLFW), without a
game engine.

## 🚧 Status

Playable! 🎉 The full game loop from the [game design document](docs/GDD.md)
is in: art, physics, columns, scoring, best score and splash screens. Ready-to-play
builds for Windows, macOS and Linux are made automatically. Next up: sound
effects.

## ▶️ Play

**[⬇️ Download the latest version](https://github.com/landoicco/huitzil/releases/latest)**
and pick the file for your computer. Nothing else to install: Java comes inside
the game.

<details>
<summary>🪟 <b>Windows</b> — <code>Huitzil-windows.zip</code></summary>

1. Unzip the file and open the `Huitzil` folder.
2. Double-click **`Huitzil.exe`**.
3. The first time, Windows shows *"Windows protected your PC"*: click
   **More info → Run anyway**.

> Why the warning? Windows shows it for any app that isn't signed with a paid
> certificate. Huitzil is a free hobby project, so it isn't.

</details>

<details>
<summary>🍎 <b>macOS</b> (Apple Silicon: M1 or newer) — <code>Huitzil-macos.dmg</code></summary>

1. Open the `.dmg` and drag **Huitzil** into **Applications**.
2. Open Huitzil. macOS says it can't verify the app: click **Done**.
3. Go to **System Settings → Privacy & Security**, scroll down and click
   **Open Anyway** next to the Huitzil message, then confirm.

From then on, it opens like any other app.

> Why the warning? macOS blocks apps that aren't notarized through Apple's paid
> developer program. Huitzil is a free hobby project, so it isn't. Macs with an
> Intel processor aren't supported.

</details>

<details>
<summary>🐧 <b>Linux</b> (x86_64) — <code>Huitzil-linux.tar.gz</code></summary>

```sh
tar -xzf Huitzil-linux.tar.gz
./Huitzil/bin/Huitzil
```

</details>

## ✨ Features (v1.0)

- 🪶 Simple controls: one key to flap.
- 🏛️ Endless columns with randomly placed gaps.
- 🏆 Score and best score, saved between sessions.
- 🎨 Original pixel art with a sunset palette and Mexica greca patterns.
- ⏱️ Fixed-timestep game loop: the game plays the same at any frame rate.

## 🎮 Controls

| Key | Action |
|---|---|
| `Space` | Flap / start / restart |
| `Esc` | Quit |
| 🖱️ Click `LICAZA.DEV` | Open my portfolio (title and game over screens) |

## 🛠️ Tech stack

| | |
|---|---|
| ☕ Language | Java 17 |
| 🖥️ Graphics | LWJGL 3 — OpenGL 3.3 core, GLFW |
| 📦 Build | Maven, packaged as a single runnable jar |
| 🚚 Releases | GitHub Actions + `jpackage`: Windows, macOS and Linux apps with Java inside |
| ❄️ Dev environment | Nix flake |

## 🚀 Getting started

### With Nix (recommended)

The flake provides the JDK, Maven and the native libraries OpenGL needs:

```sh
nix develop
mvn package
java -jar target/huitzil.jar
```

### Without Nix

You need **JDK 17+** and **Maven 3.9+** on Linux (x86_64), Windows (x86_64) or
macOS (Apple Silicon). The build picks the native libraries for your system:

```sh
mvn package
java -jar target/huitzil.jar
```

On macOS, add `-XstartOnFirstThread` before `-jar`.

## 📁 Project structure

```
src/main/java/dev/licaza/huitzil
├── Main.java     window and game loop
├── game/         game states, hummingbird, columns, scoring
├── graphics/     sprite renderer, shaders, textures, text
├── input/        keyboard state
├── math/         vectors and matrices
└── utils/        file, buffer and browser helpers
docs/
└── GDD.md        game design document
```

## 📖 Design

The concept, mechanics, tuning values and art direction are described in the
[game design document](docs/GDD.md).

## 👤 Author

Made by **Lando Icaza** — [licaza.dev](https://licaza.dev)
