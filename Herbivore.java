import java.util.List;
import javafx.scene.paint.Color;

/**
 * Shared behaviour for plant-eaters: they graze on adjacent {@link Plant}s
 * and are themselves hunted as {@link Prey}.
 *
 * @author Arjun Dhir
 */
public abstract class Herbivore extends Animal implements Prey {

    protected Herbivore(boolean randomAge, Field field, Location location, Color color, Gene gene) {
        super(field, location, color, gene);
        initializeAgeAndFood(randomAge);
    }

    /** Probability of eating a plant when one is adjacent. */
    protected abstract double getGrazeProbability();

    @Override
    public final int getFood() {
        return getFoodCapacity();
    }

    /**
     * Graze on the first palatable plant among the given neighbours.
     *
     * @param adjacent neighbouring locations in random order
     * @return where food was found, or null if there was none
     */
    @Override
    protected final Location findFood(List<Location> adjacent) {
        Field field = getField();
        for (Location where : adjacent) {
            if (field.getObjectAt(where) instanceof Plant
                    && Randomizer.getRandom().nextDouble() < getGrazeProbability()) {
                restoreFood();
                return where;
            }
        }
        return null;
    }
}
