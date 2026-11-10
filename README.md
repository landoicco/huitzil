# 🐦 Huitzil

> *Huitzil* comes from *huitzilin*, "hummingbird" in Nahuatl.

A one-button arcade game about a hummingbird darting through the carved stone
columns of an ancient Mexica city at sunset. Tap to beat its wings, slip
through the gaps and see how far it can go. 🌅

Built from scratch with **Java** and **LWJGL** (OpenGL + GLFW), without a
game engine.

## 🚧 Status

Work in progress. The base is in place: the window, the OpenGL 3.3 setup, the
build and the [game design document](docs/GDD.md). The art and the gameplay
come next.

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

## 🛠️ Tech stack

| | |
|---|---|
| ☕ Language | Java 17 |
| 🖥️ Graphics | LWJGL 3 — OpenGL 3.3 core, GLFW |
| 📦 Build | Maven, packaged as a single runnable jar |
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

You need **JDK 17+** and **Maven 3.9+** on Linux (x86_64):

```sh
mvn package
java -jar target/huitzil.jar
```

## 📁 Project structure

```
src/main/java/dev/licaza/huitzil
├── Main.java     window and game loop
├── graphics/     shaders, textures, vertex arrays
├── input/        keyboard state
├── math/         vectors and matrices
└── utils/        file and buffer helpers
docs/
└── GDD.md        game design document
```

## 📖 Design

The concept, mechanics, tuning values and art direction are described in the
[game design document](docs/GDD.md).

## 👤 Author

Made by **Lando Icaza** — [licaza.dev](https://licaza.dev)
