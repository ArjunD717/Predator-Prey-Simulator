import java.util.List;
import javafx.scene.paint.Color;

/**
 * Shared behaviour for meat-eaters: they hunt adjacent {@link Prey} and
 * move onto the kill, otherwise they wander to a free cell.
 *
 * @author Arjun Dhir
 */
public abstract class Predator extends Animal {

    private static final int FOOD_CAPACITY = 9;

    protected Predator(boolean randomAge, Field field, Location location, Color color, Gene gene) {
        super(field, location, color, gene);
        initializeAgeAndFood(randomAge);
    }

    @Override
    protected final int getFoodCapacity() {
        return FOOD_CAPACITY;
    }

    /**
     * Eat the first live prey among the given neighbours.
     *
     * @param adjacent neighbouring locations in random order
     * @return where food was found, or null if there was none
     */
    @Override
    protected final Location findFood(List<Location> adjacent) {
        Field field = getField();
        for (Location where : adjacent) {
            Animal target = field.getObjectAt(where);
            if (target instanceof Prey && target.isAlive()) {
                addFood(((Prey) target).getFood());
                target.setDead();
                return where;
            }
        }
        return null;
    }
}
