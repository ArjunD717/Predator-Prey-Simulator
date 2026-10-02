import javafx.scene.paint.Color;

/**
 * A deer grazes on adjacent plants, breeds with an adjacent opposite-sex
 * deer, and is hunted as prey.
 *
 * @author Arjun Dhir
 */
public class Deer extends Herbivore {

    private static final int FOOD_VALUE = 20;
    private static final double SPREAD_CHANCE = 0.12;
    private static final int SICK_DURATION = 7;
    private static final double GRAZE_PROBABILITY = 0.40;

    /**
     * @param randomAge true for a random age and hunger level, false for a newborn
     * @param field field currently occupied
     * @param location location within the field
     * @param color display color
     * @param gene genetic traits; a random gene is rolled when null
     */
    public Deer(boolean randomAge, Field field, Location location, Color color, Gene gene) {
        super(randomAge, field, location, color, gene);
    }

    @Override
    protected int getFoodCapacity() {
        return FOOD_VALUE;
    }

    @Override
    protected double getGrazeProbability() {
        return GRAZE_PROBABILITY;
    }

    @Override
    protected double getSpreadChance() {
        return SPREAD_CHANCE;
    }

    @Override
    protected int getSickDuration() {
        return SICK_DURATION;
    }

    @Override
    protected Animal createYoung(Field field, Location location, Gene gene) {
        return new Deer(false, field, location, getColor(), gene);
    }
}
