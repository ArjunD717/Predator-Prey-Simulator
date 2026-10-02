import java.util.List;
import javafx.scene.paint.Color;

/**
 * A plant regrows in every vacated or death cell, so prey always have
 * something to graze on. Plants never move, age or breed.
 *
 * @author Arjun Dhir
 */
public class Plant extends Animal {

    /**
     * Place a plant at the given location.
     *
     * @param field field currently occupied
     * @param location location within the field
     */
    public Plant(Field field, Location location) {
        super(field, location, Color.GREEN, Gene.sterile());
    }

    @Override
    protected int getFoodCapacity() {
        return 0;
    }

    @Override
    protected double getSpreadChance() {
        return 0;
    }

    @Override
    protected int getSickDuration() {
        return 0;
    }

    @Override
    protected Location findFood(List<Location> adjacent) {
        return null;
    }

    @Override
    protected Animal createYoung(Field field, Location location, Gene gene) {
        return new Plant(field, location);
    }

    @Override
    public void act(List<Animal> newborns) {
        // Plants do nothing.
    }
}
