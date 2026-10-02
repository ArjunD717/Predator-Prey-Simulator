import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Counts live animals per species for the population label and decides
 * whether the ecosystem is still worth running.
 *
 * @author Arjun Dhir
 */
public class FieldStats {

    private final Map<Class<?>, Counter> counters = new HashMap<>();
    private boolean countsValid = false;

    /** Build an empty stats collector. */
    public FieldStats() {
    }

    /**
     * @param field field to describe
     * @return population summary such as {@code "Bear: 12 Deer: 40 "}
     */
    public String getPopulationDetails(Field field) {
        if (!countsValid) {
            generateCounts(field);
        }
        Map<String, Counter> ordered = new TreeMap<>();
        for (Counter counter : counters.values()) {
            ordered.put(counter.getName(), counter);
        }
        StringBuilder buffer = new StringBuilder();
        for (Counter info : ordered.values()) {
            buffer.append(info.getName()).append(": ").append(info.getCount()).append(' ');
        }
        return buffer.toString();
    }

    /** Drop all counts; they are rebuilt on the next read. */
    public void reset() {
        countsValid = false;
        counters.clear();
    }

    /**
     * Count one animal of the given class.
     *
     * @param animalClass class of animal to count
     */
    public void incrementCount(Class<?> animalClass) {
        Counter counter = counters.get(animalClass);
        if (counter == null) {
            counter = new Counter(animalClass.getSimpleName());
            counters.put(animalClass, counter);
        }
        counter.increment();
    }

    /** Mark the current counts as complete. */
    public void countFinished() {
        countsValid = true;
    }

    /**
     * The run stays viable while at least two non-plant species are alive;
     * a single surviving species (or an empty field) has nothing left to
     * simulate. Plants are ground cover, not a species in this check.
     * Short-circuits on the second live species instead of counting the
     * whole field, since the animation loop calls this every step.
     *
     * @param field field to check
     * @return true if the simulation should continue
     */
    public boolean isViable(Field field) {
        Set<Class<?>> species = new HashSet<>(8);
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Animal animal = field.getObjectAt(row, col);
                if (animal != null && animal.isAlive() && !(animal instanceof Plant)
                        && species.add(animal.getClass()) && species.size() >= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Recount every cell. Not kept up to date; callers go through
     * {@link #getPopulationDetails(Field)}.
     *
     * @param field field to count
     */
    private void generateCounts(Field field) {
        reset();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Animal animal = field.getObjectAt(row, col);
                if (animal != null) {
                    incrementCount(animal.getClass());
                }
            }
        }
        countsValid = true;
    }
}
