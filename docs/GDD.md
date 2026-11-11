# Huitzil — Game Design Document

> *Huitzil* comes from *huitzilin*, "hummingbird" in Nahuatl.

| | |
|---|---|
| **Genre** | One-button arcade, endless side-scroller |
| **Platform** | Desktop: Windows, macOS and Linux |
| **Session length** | 30 seconds to a few minutes per run |
| **Audience** | Anyone; easy to learn, hard to master |

## 1. Pitch

A hummingbird darts through the carved stone columns of an ancient city at
sunset. Tap to beat its wings, slip through the gaps and see how far it can go.

## 2. Setting

In Mexica tradition, the souls of fallen warriors returned to the world as
hummingbirds to follow the sun. Huitzil is one of them: a small, fast spirit
flying west through the ruins as the sun goes down.

The story is never told in game: it lives in the art (the sunset, the temples,
the greca patterns on the stone) and in this document.

## 3. Core loop

1. The hummingbird hovers in place. The player presses **Space** to start.
2. Gravity pulls it down; every press gives it an upward burst.
3. Pairs of stone columns scroll in from the right, with a gap between them.
4. Passing a pair scores **1 point**.
5. Touching a column or the top or bottom edge of the screen ends the run.
6. The score and the best score are shown; **Space** starts a new run.

## 4. Controls

| Key | Action |
|---|---|
| `Space` | Flap / start / restart |
| `M` | Sound on / off, remembered between sessions |
| `Esc` | Quit |
| Click `LICAZA.DEV` | Open the author's portfolio (splash screens only) |

## 5. Game states

```
READY ──Space──▶ PLAYING ──collision──▶ GAME OVER
  ▲                                         │
  └──────────────── Space ──────────────────┘
```

- **Ready:** the hummingbird hovers with a gentle up-and-down bob. Columns are
  not spawned yet. Shows the title and `PRESS SPACE`.
- **Playing:** physics, scrolling, spawning and scoring are active.
- **Game over:** the world stops scrolling and the hummingbird falls off the
  bottom of the screen. Shows `GAME OVER`, the score and the best score. Input is ignored for
  the first 0.5 s so a late flap does not skip the screen.

## 6. Mechanics

### World and camera

- The camera is fixed. The world is **20 × 11.25 units** (16:9), centered on
  the origin: x goes from −10 to 10 and y from −5.625 to 5.625.
- The art is drawn at **16 px per unit**, a 320 × 180 base resolution scaled
  ×4 to the 1280 × 720 window.
- The game updates at a **fixed 60 steps per second**, independent of the
  frame rate.

### Hummingbird

- Fixed at x = −5; only its vertical position changes.
- Gravity accelerates it downward; a flap **sets** its vertical speed to the
  flap speed (it does not add to it), so every press feels the same.
- Its fall speed is capped.
- Its sprite tilts with its vertical speed: nose up while rising, nose down
  while falling.

### Columns

- Columns come in pairs: one from the ground, one from the top, with a gap
  between them.
- The gap center is random within a safe range, so it is always reachable.
- Pairs are evenly spaced and scroll left at a constant speed. A pair that
  leaves the screen on the left is recycled to the right with a new gap.

### Collision

- The hummingbird uses an axis-aligned box **smaller than its sprite**, so
  near misses feel fair.
- Columns use their full box.
- Leaving the screen at the bottom or the top is a collision.

### Scoring

- +1 when the hummingbird passes the center of a pair.
- The best score is saved between sessions (Java Preferences API).

### Tuning

All values live as constants in the code so they are easy to adjust.

| Parameter | Value |
|---|---|
| Gravity | 30 units/s² |
| Flap speed | 9 units/s |
| Max fall speed | 15 units/s |
| Hummingbird sprite | 1.25 × 1.25 units |
| Hummingbird hitbox | 0.75 × 0.55 units |
| Column width | 1.5 units |
| Column length | 8 units |
| Gap height | 3.5 units |
| Gap center range | −2.5 to 2.5 units |
| Distance between pairs | 6 units |
| Scroll speed | 4 units/s |
| Background scroll speed | 1 unit/s |
| Game over input delay | 0.5 s |

Difficulty stays constant: the challenge comes from consistency, not from the
game speeding up.

## 7. Art direction

Pixel art drawn at the base resolution and scaled with nearest-neighbor
filtering, so every pixel stays sharp. All sprites are original.

### Palette

| Use | Color |
|---|---|
| Sky (top → horizon) | `#2b1b3d` → `#8c2f5a` → `#f08a4b` |
| Sun | `#ffd27a` |
| Temples and cacti (silhouettes) | `#1e1230` |
| Stone | `#c9b79c`, shadow `#8f7a63` |
| Greca accents (jade) | `#2fa39a` |
| Hummingbird body | `#2bb673`, dark `#1a7a4c` |
| Hummingbird throat | `#e0405a` |
| Beak and eye | `#2a2a2a` |
| Text | `#fff3d6` |

### Assets

| Asset | Size (px) | Notes |
|---|---|---|
| `hummingbird.png` | 40 × 20 | Two 20 × 20 frames, wings up and down; alternates every 0.05 s |
| `column.png` | 24 × 128 | Carved stone with jade bands of stepped greca; capital at the top. Flipped vertically for the top column |
| `background.png` | 320 × 180 | Sunset sky, sun, temple and cactus silhouettes; tiles horizontally |
| `font.png` | 228 × 8 | 5 × 7 pixel glyphs (`0–9`, `A–Z`, `.`, `:`) in 6 × 8 cells, with a 1 px shadow |
| `icon.png` | 32 × 32 | Game icon: the hummingbird over a crop of the sunset, with stepped rounded corners. Scaled up for the app packages in `packaging/` |
| `overlay.png` | 1 × 1 | Translucent dark pixel, stretched over the screen behind the splash text |

### Parallax

The background scrolls at a quarter of the column speed, which gives depth with
a single layer.

## 8. User interface

All text uses the pixel font, centered horizontally, at scales in multiples of
0.25 so every font pixel covers whole screen pixels and stays sharp.

The **Ready** and **Game over** states are splash screens: the world keeps
being drawn underneath, dimmed by a translucent overlay, and both end with a
link to the author's portfolio, `LICAZA.DEV`: on hover it turns sun yellow
(`#ffc767`) and the cursor turns into a hand, and clicking it opens
https://licaza.dev in the default browser.

| Screen | Content (scale) |
|---|---|
| Ready | `HUITZIL` (×4), `PRESS SPACE` (×2), `LICAZA.DEV` (×1) |
| Playing | Current score at the top center (×2), no overlay |
| Game over | `GAME OVER` (×3), `SCORE n` and `BEST n` (×2), `PRESS SPACE` (×1), `LICAZA.DEV` (×1) |

Both splash screens also show the game version (`V1.1.0`, ×0.75) in the bottom
right corner. It comes from the Maven project version through the jar
manifest, so it is only set in `pom.xml`; outside the jar it reads `DEV`. The
bottom left corner shows the sound toggle, `M SOUND ON` or `M SOUND OFF`
(×0.75).

## 9. Audio

Three short retro sound effects, synthesized from simple waveforms so they
match the pixel art:

| Sound | When | Description |
|---|---|---|
| `flap.wav` | Every flap, including the one that starts a run | Quick, soft upward chirp (70 ms) |
| `score.wav` | Passing a pair of columns | Two bright rising notes, B5 → E6 |
| `hit.wav` | Hitting a column or an edge | Dull stone thud: a falling low tone under a burst of noise |

They are 44.1 kHz, 16-bit mono WAV files with smooth envelopes, so they play
without clicks.

Background music: [*Apple Cider*](https://opengameart.org/content/apple-cider)
by Zane Little Music (CC0), a cozy, relaxed chiptune with flute and guitar. It
loops from the moment the game opens, at 35 % volume so the sound effects stay
in front.

`M` mutes the music and the sound effects. Without an audio device the game
runs silently.

## 10. Scope

How the game is built is described in the [architecture document](ARCHITECTURE.md).

| Version | Content |
|---|---|
| 1.0 | The full game loop: art, physics, columns, scoring, best score and splash screens |
| 1.1 | Sound effects, music and mute; ready-to-play apps for Windows, macOS and Linux |

The game is complete at version 1.1. It is meant to stay small and polished,
so no more features are planned.
