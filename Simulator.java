import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;

/**
 * Owns the population and the field, and advances the ecosystem one step
 * at a time. Each simulation step every living animal acts once; the dead
 * are removed and the newborns join the population.
 *
 * <p>Setup rolls one random draw per cell and maps it onto cumulative
 * species probabilities, so the creation chances are exactly the constants
 * below. All other cells grow plants.
 *
 * @author Arjun Dhir
 */
public class Simulator {

    /** Default field size used by the graphical view. */
    public static final int DEFAULT_DEPTH = 80;
    /** Default field size used by the graphical view. */
    public static final int DEFAULT_WIDTH = 100;

    private static final double WOLF_CREATION_PROBABILITY = 0.03;
    private static final double BEAR_CREATION_PROBABILITY = 0.02;
    private static final double COYOTE_CREATION_PROBABILITY = 0.05;
    private static final double SHEEP_CREATION_PROBABILITY = 0.09;
    private static final double DEER_CREATION_PROBABILITY = 0.11;
    private static final double SQUIRREL_CREATION_PROBABILITY = 0.13;

    private static final double BEAR_UPPER = WOLF_CREATION_PROBABILITY + BEAR_CREATION_PROBABILITY;
    private static final double COYOTE_UPPER = BEAR_UPPER + COYOTE_CREATION_PROBABILITY;
    private static final double SHEEP_UPPER = COYOTE_UPPER + SHEEP_CREATION_PROBABILITY;
    private static final double DEER_UPPER = SHEEP_UPPER + DEER_CREATION_PROBABILITY;
    private static final double SQUIRREL_UPPER = DEER_UPPER + SQUIRREL_CREATION_PROBABILITY;

    private final List<Animal> animals = new ArrayList<>();
    private final Field field;
    private int step;

    /**
     * @param depth rows, must be greater than zero
     * @param width columns, must be greater than zero
     */
    public Simulator(int depth, int width) {
        field = new Field(depth, width);
        reset();
    }

    /**
     * Advance every living animal once: the dead leave the population and
     * the newborns join it.
     */
    public void simulateOneStep() {
        step++;
        List<Animal> newborns = new ArrayList<>(animals.size() / 4 + 16);
        for (Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            if (!animal.isAlive()) {
                // Eaten by an earlier animal this step; never gets to act.
                it.remove();
                continue;
            }
            animal.act(newborns);
            if (!animal.isAlive()) {
                it.remove();
            }
        }
        animals.addAll(newborns);
        // Animals eaten after they acted die mid-step; purge so the
        // population only ever holds the living.
        animals.removeIf(animal -> !animal.isAlive());
    }

    /** Return the simulation to a fresh starting position. */
    public void reset() {
        step = 0;
        Randomizer.reset();
        animals.clear();
        populate();
    }

    /**
     * Fill the field: one random draw per cell, cumulative species chances,
     * plants everywhere else.
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                double roll = rand.nextDouble();
                if (roll < WOLF_CREATION_PROBABILITY) {
                    animals.add(new Wolf(true, field, location, Color.BLUE, new Gene()));
                }
                else if (roll < BEAR_UPPER) {
                    animals.add(new Bear(true, field, location, Color.YELLOW, new Gene()));
                }
                else if (roll < COYOTE_UPPER) {
                    animals.add(new Coyote(true, field, location, Color.RED, new Gene()));
                }
                else if (roll < SHEEP_UPPER) {
                    animals.add(new Sheep(true, field, location, Color.PURPLE, new Gene()));
                }
                else if (roll < DEER_UPPER) {
                    animals.add(new Deer(true, field, location, Color.BROWN, new Gene()));
                }
                else if (roll < SQUIRREL_UPPER) {
                    animals.add(new Squirrel(true, field, location, Color.PINK, new Gene()));
                }
                else {
                    new Plant(field, location);
                }
            }
        }
    }

    /**
     * Pause the calling thread. Interrupts are swallowed so animation loops
     * keep running; callers that care should watch the interrupt flag.
     *
     * @param millisec time to pause, in milliseconds
     */
    public void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Field getField() {
        return field;
    }

    public int getStep() {
        return step;
    }

    /** Live population; read-only. */
    public List<Animal> getAnimals() {
        return Collections.unmodifiableList(animals);
    }
}
