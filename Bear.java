import javafx.scene.paint.Color;

/**
 * A bear hunts adjacent prey, breeds with a nearby male (females only),
 * and dies of hunger, disease, old age or overcrowding.
 *
 * @author Arjun Dhir
 */
public class Bear extends Predator {

    private static final double SPREAD_CHANCE = 0.18;
    private static final int SICK_DURATION = 6;

    /**
     * @param randomAge true for a random age and hunger level, false for a newborn
     * @param field field currently occupied
     * @param location location within the field
     * @param color display color
     * @param gene genetic traits; a random gene is rolled when null
     */
    public Bear(boolean randomAge, Field field, Location location, Color color, Gene gene) {
        super(randomAge, field, location, color, gene);
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
        return new Bear(false, field, location, getColor(), gene);
    }
}
