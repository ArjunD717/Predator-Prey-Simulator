import javafx.scene.paint.Color;

/**
 * A coyote hunts adjacent prey, breeds with an adjacent opposite-sex coyote,
 * and dies of hunger, disease, old age or overcrowding.
 *
 * @author Arjun Dhir
 */
public class Coyote extends Predator {

    private static final double SPREAD_CHANCE = 0.13;
    private static final int SICK_DURATION = 4;

    /**
     * @param randomAge true for a random age and hunger level, false for a newborn
     * @param field field currently occupied
     * @param location location within the field
     * @param color display color
     * @param gene genetic traits; a random gene is rolled when null
     */
    public Coyote(boolean randomAge, Field field, Location location, Color color, Gene gene) {
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
        return new Coyote(false, field, location, getColor(), gene);
    }
}
