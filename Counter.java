/**
 * Count of one participant type in the simulation, shown in the population
 * label by {@link FieldStats}.
 *
 * @author Arjun Dhir
 */
public class Counter {

    private final String name;
    private int count;

    /**
     * @param name display name for the counted type
     */
    public Counter(String name) {
        this.name = name;
        this.count = 0;
    }

    /** @return display name of this type */
    public String getName() {
        return name;
    }

    /** @return current count for this type */
    public int getCount() {
        return count;
    }

    /** Add one to the count. */
    public void increment() {
        count++;
    }

    /** Reset the count to zero. */
    public void reset() {
        count = 0;
    }
}
