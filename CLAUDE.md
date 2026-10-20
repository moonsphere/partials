# partials

Music composed by Claude, performed by Overtone (Clojure → SuperCollider's scsynth).
Claude is the composer here: it writes pieces as Clojure, plays them, and judges them
from renders it can inspect, since it cannot listen.

## Loop

1. `bin/repl` (run in the background; once per session). Wait for port 7888, then `bin/eval '(boot!)'`.
2. Write or edit `src/partials/pieces/opNN_name.clj` (see conventions below).
3. `bin/render opNN` → `out/opNN-name.wav`. It blocks for the piece's duration plus about 3 seconds.
4. `bin/look out/opNN-name.wav [max-hz]` → prints peak and RMS levels and writes a PNG
   spectrogram; Read the PNG to see the piece. The frequency axis is linear from 0 to max-hz
   (default 4000). A low max-hz resolves bass pitches but smears time.
5. Iterate. `bin/eval "(play! 'opNN)"` plays the piece live for the user; `bin/eval '(stop!)'` silences it.

Every render begins with about 1.5 s of silence, a side effect of Overtone's disk recorder.

## Piece conventions

- Namespace `partials.pieces.opNN-name` with ns metadata `{:opus N :title "…" :duration secs}`
  and a docstring describing the idea.
- `(:refer-clojure :exclude [abs]) (:use overtone.core)`. Never `overtone.live`: it boots a
  server as a side effect of `require`.
- A no-arg `play` that schedules the whole piece relative to `(now)` with `at`/`apply-by`
  and returns at once. `render!` waits for `:duration`.
- Keep the master peak below about -1 dBFS (check with `bin/look`).
- Add each new piece to the table in README.md.

## Environment notes

- scsynth runs through PipeWire's JACK (`pw-jack`). `bin/env` sets `XDG_RUNTIME_DIR` and
  `JAVA_CMD` when the shell doesn't provide them.
- `SC_PORT=<port>` makes `boot!` connect to an already running scsynth instead of booting one.
