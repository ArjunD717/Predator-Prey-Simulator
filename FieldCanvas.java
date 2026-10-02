import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Objects;

/**
 * Canvas that paints one small rectangle per field cell.
 *
 * @author Arjun Dhir
 */
public class FieldCanvas extends Canvas {

    private static final int MIN_SCALE = 6;

    private final int pixelWidth;
    private final int pixelHeight;
    private int xScale = MIN_SCALE;
    private int yScale = MIN_SCALE;
    private final GraphicsContext gc;

    /**
     * @param width canvas width in pixels
     * @param height canvas height in pixels
     */
    public FieldCanvas(int width, int height) {
        super(width, height);
        this.pixelWidth = width;
        this.pixelHeight = height;
        this.gc = getGraphicsContext2D();
    }

    /**
     * Derive cell size from the grid dimensions. Falls back to a fixed
     * minimum scale when the grid is larger than the canvas.
     *
     * @param gridHeight rows in the field
     * @param gridWidth columns in the field
     */
    public void setScale(int gridHeight, int gridWidth) {
        if (gridHeight <= 0 || gridWidth <= 0) {
            throw new IllegalArgumentException("grid dimensions must be positive");
        }
        xScale = pixelWidth / gridWidth;
        yScale = pixelHeight / gridHeight;
        if (xScale < 1) {
            xScale = MIN_SCALE;
        }
        if (yScale < 1) {
            yScale = MIN_SCALE;
        }
    }

    /**
     * Paint one cell.
     *
     * <p>Called once per cell per frame; kept allocation-free (no boxing,
     * no objects) so the 8k-cell repaint stays cheap.
     *
     * @param x column
     * @param y row
     * @param color fill (must not be null)
     */
    public void drawMark(int x, int y, Color color) {
        gc.setFill(Objects.requireNonNull(color, "color must not be null"));
        gc.fillRect(x * xScale, y * yScale, Math.max(1, xScale - 1), Math.max(1, yScale - 1));
    }
}
