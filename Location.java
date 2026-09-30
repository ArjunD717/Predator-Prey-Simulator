import java.util.Objects;

/**
 * Immutable row/column position in the field grid.
 *
 * @author Arjun Dhir
 */
public final class Location {

    private final int row;
    private final int col;

    /**
     * @param row row index
     * @param col column index
     */
    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Location other)) {
            return false;
        }
        return row == other.row && col == other.col;
    }

    @Override
    public String toString() {
        return row + "," + col;
    }

    /**
     * Packs the row into the top 16 bits and the column into the bottom,
     * giving a unique hash for all but very large grids.
     *
     * @return hash code for the (row, col) pair
     */
    @Override
    public int hashCode() {
        return (row << 16) + col;
    }

    /** @return the row */
    public int getRow() {
        return row;
    }

    /** @return the column */
    public int getCol() {
        return col;
    }

    /** Fail fast when a location must lie inside the field. */
    void requireInside(int depth, int width) {
        Objects.checkIndex(row, depth);
        Objects.checkIndex(col, width);
    }
}
