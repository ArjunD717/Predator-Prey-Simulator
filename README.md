# Predator-Prey Simulator

This repository contains a JavaFX predator-prey simulation built around a multi-species ecosystem model.

## Overview

The simulation models a multi-species food chain with predators, prey, plant regeneration, disease spread, and gene-driven natural selection.

Implemented features include:

- predator species: wolf, bear, and coyote
- prey species: sheep, deer, and squirrel
- plants occupying free cells and returning after deaths or movement
- competition between predators for shared prey
- disease state with same-species transmission
- genetic traits controlling breeding age, lifespan, breeding probability, litter size, disease probability, and metabolism
- crossover and mutation for offspring generation

## Files

- Java source files for the simulation
- `simulation.jar`

## Main Class

`SimulatorView`

## Running

This project is easiest to open in BlueJ with JavaFX support.

Command-line example:

```bash
javac --module-path /path/to/javafx/lib --add-modules javafx.controls *.java
java --module-path /path/to/javafx/lib --add-modules javafx.controls SimulatorView
```

## Author

Arjun Dhir
