# Predator–Prey Simulator

A JavaFX ecosystem simulation: three predator species hunt three prey species on a
rectangular field, while plants regrow, disease spreads within species, and a simple
genetic model drives natural selection through crossover and mutation.

## Features

- **6 animal species** — predators: wolf, bear, coyote; prey (herbivores): sheep, deer, squirrel
- **Plant regrowth** — every vacated or death cell grows a plant, so grazers always have food
- **Predator competition** — all three predators hunt the same shared prey
- **Disease** — spontaneous infection per the animal's gene, same-species transmission to neighbours, death after a species-specific illness duration
- **Genetics** — each animal carries a 14-digit gene (breeding age, lifespan, breeding probability, litter size, disease probability, metabolism); offspring inherit half from each parent with per-digit mutation
- **Live JavaFX view** — grid rendering, generation counter, per-species population counts, Step / Run–Pause / Reset controls, automatic stop when the ecosystem collapses

## Tech stack

- Java 17+ (uses pattern-matching `instanceof`; developed on JDK 25)
- JavaFX 21 (`javafx.controls`, pulls in `graphics` and `base`)
- No other dependencies, no build tool required

## Project structure

| File | Role |
|---|---|
| `SimulatorView.java` | JavaFX application: canvas, toolbar, animation loop, entry point (`main`) |
| `Simulator.java` | Engine: owns the population and field, advances one step, (re)populates the field |
| `Animal.java` | Template method (`act`) shared by all animals: age, hunger, sicken, breed, move |
| `Predator.java` | Shared hunting behaviour for wolf, bear, coyote |
| `Herbivore.java` | Shared grazing behaviour for sheep, deer, squirrel (also `Prey`) |
| `Wolf/Bear/Coyote.java` | Predator species: disease timing and newborn factory only |
| `Sheep/Deer/Squirrel.java` | Prey species: food value, graze chance, disease timing, newborn factory |
| `Plant.java` | Ground cover; regrows in freed cells, never acts |
| `Gene.java` | Immutable genetic traits: encoding, crossover (`combine`), mutation |
| `Field.java` | Grid: placement, shuffled neighbour/free-cell queries |
| `FieldStats.java` | Per-species census and the viability check |
| `Location.java` | Immutable grid coordinate |
| `Randomizer.java` | Shared seeded RNG (deterministic runs) |
| `Counter.java` / `Prey.java` / `FieldCanvas.java` | Population counter, food-value contract, grid renderer |

## Prerequisites

- JDK 17 or newer (`java -version`)
- JavaFX 21 SDK **or** the three jars from Maven Central (`javafx-base`, `javafx-graphics`, `javafx-controls`, all with the `win`/`linux`/`mac` classifier matching your OS)
- A display (the UI cannot run headless)

## Quickstart

Set `JAVAFX` to your JavaFX SDK `lib` directory (or any folder containing the three jars):

```bash
# Linux/macOS
export JAVAFX=/path/to/javafx-sdk-21/lib
javac --module-path "$JAVAFX" --add-modules javafx.controls *.java
java  --module-path "$JAVAFX" --add-modules javafx.controls SimulatorView
```

```bat
:: Windows
set JAVAFX=C:\path\to\javafx-sdk-21\lib
javac --module-path "%JAVAFX%" --add-modules javafx.controls *.java
java  --module-path "%JAVAFX%" --add-modules javafx.controls SimulatorView
```

A ready-built `simulation.jar` is also included; run it the same way:

```bash
java --module-path "$JAVAFX" --add-modules javafx.controls -jar simulation.jar
```

To rebuild it after changing sources:

```bash
javac --module-path "$JAVAFX" --add-modules javafx.controls *.java
jar --create --file simulation.jar --main-class SimulatorView *.class
```

## Controls

| Button | Effect |
|---|---|
| **Step** | Advance exactly one generation |
| **Run / Pause** | Toggle continuous animation (one generation per 0.5 s) |
| **Reset** | Fresh starting population (deterministic — see below) |

The run stops by itself with a status message once fewer than two animal species remain alive.

## How the simulation works

Each step, every living animal acts once, in list order: age (+1, die past lifespan) →
hunger (−metabolism, starve at zero) → possibly fall sick → disease countdown →
infect same-species neighbours → females with an adjacent male may produce **one**
litter in free neighbouring cells → move onto food if adjacent, else wander, else die
of overcrowding. Deaths and moves leave a plant behind. Newborns join the population
next step.

Species parameters:

| Species | Eats | Food value | Falls sick → dies after | Infects neighbour |
|---|---|---|---|---|
| Wolf | prey | 9 | gene roll → 7 steps | 18% |
| Bear | prey | 9 | gene roll → 6 steps | 18% |
| Coyote | prey | 9 | gene roll → 4 steps | 13% |
| Sheep | plants (45%/plant) | 10 | gene roll → 6 steps | 15% |
| Deer | plants (40%/plant) | 20 | gene roll → 7 steps | 12% |
| Squirrel | plants (50%/plant) | 5 | gene roll → 7 steps | 20% |

The gene is 14 digits: breeding age `00–01` (12–90), lifespan `02–04` (10–120),
breeding probability `05–06` (10–80%), litter size `07–08` (1–12), disease
probability `09–10` (0–50%), metabolism `11–13` (25–100% of a food unit per step).
Mating splits at digit 7 — father gives age/lifespan/breeding-probability, mother
gives litter-size/disease/metabolism — then each digit drifts ±1 with 20% probability
and every field is clamped back into range. Plants carry the sterile all-zero gene
and never evolve.

## Configuration

- **Initial populations** — creation probabilities in `Simulator` (`WOLF 0.03`,
  `BEAR 0.02`, `COYOTE 0.05`, `SHEEP 0.09`, `DEER 0.11`, `SQUIRREL 0.13`; one roll
  per cell against cumulative bands, plants elsewhere)
- **Field size** — `SimulatorView.GRID_WIDTH / GRID_HEIGHT` (default 100×80)
- **Animation speed** — `SimulatorView.STEP_DELAY_MS` (default 500 ms)
- **Determinism** — `Randomizer` uses seed `1111`; `Simulator.reset()` re-seeds, so
  every fresh run (and every Reset) unfolds identically

## Troubleshooting

- `JavaFX runtime components are missing` — the `--module-path`/`--add-modules` flags are absent or `JAVAFX` points at the wrong folder
- `ClassNotFoundException: SimulatorView` — run from the directory containing the compiled classes
- Blank window / instant end — with the fixed seed this should not happen on default settings; shrinking the grid or raising predator probabilities can starve the ecosystem in a few steps

## Author

Arjun Dhir
