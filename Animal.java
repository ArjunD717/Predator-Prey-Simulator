import java.util.List;
import java.util.Objects;
import javafx.scene.paint.Color;

/**
 * Shared state and behaviour for every creature in the field.
 *
 * <p>An animal ages, grows hungry, can fall sick, breeds with an adjacent
 * mate and moves each step. Gene-driven traits (breeding age, lifespan,
 * breeding probability, litter size, disease susceptibility and metabolism)
 * are copied from the animal's {@link Gene} at construction and stay fixed
 * for life; only the gene string itself is passed on to offspring (with
 * crossover and mutation).
 *
 * @author Arjun Dhir
 */
public abstract class Animal {

    /** Sex of an animal. Mating needs one male and one female; either side may initiate. */
    public enum Gender {
        MALE, FEMALE
    }

    private final Gene gene;
    private final Gender gender;

    private Field field;
    private Location location;
    private Color color;
    private boolean alive;

    private int age;
    private double foodLevel;
    private boolean sick;
    private int sickStepsRemaining;

    private final int breedingAge;
    private final int lifeSpan;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final double diseaseProbability;
    private final double metabolism;

    /**
     * Create a new animal at the given location in the field.
     *
     * @param field field the animal lives in (must not be null)
     * @param location initial location; the animal is placed there (must not be null)
     * @param color display color (must not be null)
     * @param gene genetic traits; a random gene is rolled when null
     */
    protected Animal(Field field, Location location, Color color, Gene gene) {
        this.field = Objects.requireNonNull(field, "field must not be null");
        this.location = Objects.requireNonNull(location, "location must not be null");
        this.color = Objects.requireNonNull(color, "color must not be null");
        this.gene = gene != null ? gene : new Gene();
        this.gender = Randomizer.getRandom().nextDouble() <= 0.5 ? Gender.MALE : Gender.FEMALE;
        this.alive = true;
        this.breedingAge = this.gene.getBreedingAge();
        this.lifeSpan = this.gene.getLifeSpan();
        this.breedingProbability = this.gene.getBreedingProbability();
        this.maxLitterSize = this.gene.getLitterSize();
        this.diseaseProbability = this.gene.getDiseaseProbability();
        this.metabolism = this.gene.getMetabolism();
        field.place(this, location);
    }

    /**
     * Roll initial age and hunger for animals placed at setup, or start
     * newborns at age zero with a full stomach. Called by subclass constructors.
     * Setup hunger is biased high (half to full) so the first steps cull the
     * old and sick rather than the merely unlucky.
     *
     * @param randomAge true to randomize age and hunger, false for a newborn
     */
    protected final void initializeAgeAndFood(boolean randomAge) {
        if (randomAge) {
            age = Randomizer.getRandom().nextInt(Math.max(1, lifeSpan));
            foodLevel = getFoodCapacity() / 2.0
                    + Randomizer.getRandom().nextDouble() * (getFoodCapacity() / 2.0);
        }
        else {
            age = 0;
            foodLevel = getFoodCapacity();
        }
    }
    /** Maximum food level; newborns start full. */
    protected abstract int getFoodCapacity();

    /** Per-step probability of infecting each adjacent same-species neighbour while sick. */
    protected abstract double getSpreadChance();

    /** Steps a sick animal survives before the disease kills it. */
    protected abstract int getSickDuration();

    /**
     * Look for food among the given neighbouring locations, eating it when
     * found. Subclasses scan the same shuffled list the rest of the step
     * reuses, so one allocation covers feeding, mating and wandering.
     *
     * @param adjacent neighbouring locations in random order
     * @return the location to move to, or null when no food is in reach
     */
    protected abstract Location findFood(List<Location> adjacent);

    /** Create one newborn of the concrete species. */
    protected abstract Animal createYoung(Field field, Location location, Gene gene);

    /**
     * Age, hunger, sicken, breed and move the animal. Animals eaten earlier
     * in a step are skipped by the simulator; animals eaten later in the
     * same step are purged from the population at the end of the step.
     *
     * @param newborns list receiving newly born animals
     */
    public void act(List<Animal> newborns) {
        incrementAge();
        incrementHunger();
        becomeSick();
        updateIllness();
        if (!alive) {
            return;
        }
        List<Location> adjacent = field.adjacentLocations(location);
        spreadIllness(adjacent);
        giveBirth(newborns, adjacent);
        Location newLocation = findFood(adjacent);
        if (newLocation == null) {
            // No food found - try to move to a free location.
            newLocation = field.firstFreeAdjacentLocation(adjacent);
        }
        if (newLocation != null) {
            setLocation(newLocation);
        }
        else {
            // Overcrowding.
            setDead();
        }
    }

    /** Increase the age. This can kill the animal. */
    private void incrementAge() {
        age++;
        if (age > lifeSpan) {
            setDead();
        }
    }

    /** Burn food. This can starve the animal. */
    private void incrementHunger() {
        foodLevel -= metabolism;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /** Add food from a kill, clamping at the maximum so reserves stay bounded. */
    protected final void addFood(int amount) {
        foodLevel = Math.min(foodLevel + amount, getFoodCapacity());
    }

    /** Refill to full after grazing. */
    protected final void restoreFood() {
        foodLevel = getFoodCapacity();
    }

    /** Spontaneously fall sick according to the gene's disease probability. */
    private void becomeSick() {
        if (!sick && Randomizer.getRandom().nextDouble() < diseaseProbability) {
            sick = true;
            sickStepsRemaining = getSickDuration();
        }
    }

    /** Count down the illness; death follows when time runs out. */
    private void updateIllness() {
        if (sick) {
            sickStepsRemaining--;
            if (sickStepsRemaining <= 0) {
                setDead();
            }
        }
    }

    /** Spread disease to same-species neighbours from the shared scan. */
    private void spreadIllness(List<Location> adjacent) {
        if (!sick) {
            return;
        }
        for (Location neighbour : adjacent) {
            Animal animal = field.getObjectAt(neighbour);
            if (animal != null && animal.getClass() == getClass()
                    && animal.isAlive() && !animal.sick
                    && Randomizer.getRandom().nextDouble() < getSpreadChance()) {
                animal.sick = true;
                animal.sickStepsRemaining = animal.getSickDuration();
            }
        }
    }

    /**
     * Produce one litter when old enough and a same-species animal of the
     * opposite sex is adjacent: mating needs one male and one female, but
     * either side may initiate, so a lone eligible animal beside any
     * opposite-sex mate can breed. At most one litter per step.
     */
    private void giveBirth(List<Animal> newborns, List<Location> adjacent) {
        if (age < breedingAge) {
            return;
        }
        for (Location neighbour : adjacent) {
            Animal animal = field.getObjectAt(neighbour);
            if (animal == null || animal.getClass() != getClass()
                    || !animal.isAlive() || animal.isMale() == isMale()) {
                continue;
            }
            if (Randomizer.getRandom().nextDouble() > breedingProbability) {
                continue;
            }
            Animal mate = animal;
            List<Location> free = field.freeAdjacentLocations(adjacent);
            int births = Math.min(Randomizer.getRandom().nextInt(Math.max(1, maxLitterSize)) + 1, free.size());
            Gene father = isMale() ? gene : mate.getGene();
            Gene mother = isMale() ? mate.getGene() : gene;
            for (int b = 0; b < births; b++) {
                newborns.add(createYoung(field, free.get(b), Gene.combine(father, mother)));
            }
            return;
        }
    }

    /** Check whether the animal is alive. */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Kill the animal and let a plant regrow in its place.
     */
    protected void setDead() {
        alive = false;
        if (location != null) {
            Location deathSite = location;
            Field home = field;
            location = null;
            field = null;
            // The Plant constructor places itself at the death site.
            new Plant(home, deathSite);
        }
    }

    /** Return the animal's location. */
    public Location getLocation() {
        return location;
    }

    public boolean isMale() {
        return gender == Gender.MALE;
    }

    public Gender getGender() {
        return gender;
    }

    /**
     * Move to a new location, leaving a plant behind in the vacated cell.
     *
     * @param newLocation the animal's new location (must not be null)
     */
    protected void setLocation(Location newLocation) {
        Objects.requireNonNull(newLocation, "new location must not be null");
        if (location != null) {
            // The Plant constructor places itself in the vacated cell.
            new Plant(field, location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /** Return the animal's field. */
    public Field getField() {
        return field;
    }

    public Gene getGene() {
        return gene;
    }

    /** Change the display color. */
    public void setColor(Color color) {
        this.color = Objects.requireNonNull(color, "color must not be null");
    }

    /** Return the display color. */
    public Color getColor() {
        return color;
    }
}
