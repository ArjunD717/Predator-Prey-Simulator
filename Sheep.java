import javafx.scene.paint.Color;

/**
 * A sheep grazes on adjacent plants, breeds with an adjacent opposite-sex
 * sheep, and is hunted as prey.
 *
 * @author Arjun Dhir
 */
public class Sheep extends Herbivore {

    private static final int FOOD_VALUE = 10;
    private static final double SPREAD_CHANCE = 0.15;
    private static final int SICK_DURATION = 6;
    private static final double GRAZE_PROBABILITY = 0.45;

    /**
     * @param randomAge true for a random age and hunger level, false for a newborn
     * @param field field currently occupied
     * @param location location within the field
     * @param color display color
     * @param gene genetic traits; a random gene is rolled when null
     */
    public Sheep(boolean randomAge, Field field, Location location, Color color, Gene gene) {
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
        return new Sheep(false, field, location, getColor(), gene);
    }
}
