import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Rectangular grid of field positions. Each cell holds at most one
 * {@link Animal} (plants occupy cells too), or null when empty.
 *
 * @author Arjun Dhir
 */
public class Field {

    private static final Random RAND = Randomizer.getRandom();

    private final int depth;
    private final int width;
    private final Animal[][] grid;

    /**
     * @param depth rows, must be greater than zero
     * @param width columns, must be greater than zero
     */
    public Field(int depth, int width) {
        if (depth <= 0 || width <= 0) {
            throw new IllegalArgumentException("depth and width must be positive: " + depth + "x" + width);
        }
        this.depth = depth;
        this.width = width;
        this.grid = new Animal[depth][width];
    }

    /** Empty the field. */
    public void clear() {
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                grid[row][col] = null;
            }
        }
    }

    /**
     * Clear the given location.
     *
     * @param location location to clear (must be in bounds)
     */
    public void clear(Location location) {
        location.requireInside(depth, width);
        grid[location.getRow()][location.getCol()] = null;
    }

    /**
     * Place an animal at the given location, overwriting any occupant.
     *
     * @param animal animal to place (must not be null)
     * @param row row coordinate
     * @param col column coordinate
     */
    public void place(Animal animal, int row, int col) {
        place(animal, new Location(row, col));
    }

    /**
     * Place an animal at the given location, overwriting any occupant.
     *
     * @param animal animal to place (must not be null)
     * @param location where to place it
     */
    public void place(Animal animal, Location location) {
        Objects.requireNonNull(animal, "animal must not be null");
        location.requireInside(depth, width);
        grid[location.getRow()][location.getCol()] = animal;
    }

    /**
     * @param location where in the field
     * @return the animal at the location, or null if there is none
     */
    public Animal getObjectAt(Location location) {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * @return the animal at the given coordinates, or null if there is none
     */
    public Animal getObjectAt(int row, int col) {
        Objects.checkIndex(row, depth);
        Objects.checkIndex(col, width);
        return grid[row][col];
    }

    /**
     * Pick a random adjacent location (never the location itself).
     * Locations are all within bounds.
     *
     * @param location origin
     * @return a random neighbour
     * @throws IllegalArgumentException when the location has no neighbours
     */
    public Location randomAdjacentLocation(Location location) {
        List<Location> adjacent = adjacentLocations(location);
        if (adjacent.isEmpty()) {
            throw new IllegalArgumentException("location has no adjacent cells: " + location);
        }
        return adjacent.get(RAND.nextInt(adjacent.size()));
    }

    /**
     * All locations adjacent to the given one, in random order.
     * The list never includes the location itself and never leaves the grid.
     *
     * @param location origin (must not be null)
     * @return shuffled neighbours
     */
    public List<Location> adjacentLocations(Location location) {
        Objects.requireNonNull(location, "location must not be null");
        List<Location> locations = new LinkedList<>();
        int row = location.getRow();
        int col = location.getCol();
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            int nextRow = row + rowOffset;
            if (nextRow < 0 || nextRow >= depth) {
                continue;
            }
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                int nextCol = col + colOffset;
                if (nextCol < 0 || nextCol >= width || (rowOffset == 0 && colOffset == 0)) {
                    continue;
                }
                locations.add(new Location(nextRow, nextCol));
            }
        }
        Collections.shuffle(locations, RAND);
        return locations;
    }

    /**
     * Living neighbours in random order.
     *
     * @param location origin
     * @return shuffled living neighbours
     */
    public List<Animal> getLivingNeighbours(Location location) {
        Objects.requireNonNull(location, "location must not be null");
        List<Animal> neighbours = new ArrayList<>();
        for (Location adjacent : adjacentLocations(location)) {
            Animal animal = grid[adjacent.getRow()][adjacent.getCol()];
            if (animal != null && animal.isAlive()) {
                neighbours.add(animal);
            }
        }
        return neighbours;
    }

    /** @return the depth (rows) of the field */
    public int getDepth() {
        return depth;
    }

    /** @return the width (columns) of the field */
    public int getWidth() {
        return width;
    }

    /**
     * Free adjacent locations (empty cells and plants) in random order.
     *
     * @param location origin
     * @return shuffled free neighbours
     */
    public List<Location> getFreeAdjacentLocations(Location location) {
        List<Location> free = new LinkedList<>();
        for (Location next : adjacentLocations(location)) {
            Animal occupant = getObjectAt(next);
            if (occupant == null || occupant instanceof Plant) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * A random free adjacent location, or null when surrounded.
     *
     * @param location origin
     * @return a free neighbour, or null
     */
    public Location getFreeAdjacentLocation(Location location) {
        List<Location> free = getFreeAdjacentLocations(location);
        return free.isEmpty() ? null : free.get(0);
    }
}
