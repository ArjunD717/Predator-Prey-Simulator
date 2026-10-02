import javafx.scene.paint.Color;

/**
 * A wolf hunts adjacent prey, breeds with an adjacent opposite-sex wolf,
 * and dies of hunger, disease, old age or overcrowding.
 *
 * @author Arjun Dhir
 */
public class Wolf extends Predator {

    private static final double SPREAD_CHANCE = 0.18;
    private static final int SICK_DURATION = 7;

    /**
     * @param randomAge true for a random age and hunger level, false for a newborn
     * @param field field currently occupied
     * @param location location within the field
     * @param color display color
     * @param gene genetic traits; a random gene is rolled when null
     */
    public Wolf(boolean randomAge, Field field, Location location, Color color, Gene gene) {
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
        return new Wolf(false, field, location, getColor(), gene);
    }
}
