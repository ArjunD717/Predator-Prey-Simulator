import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color; 

/**
 * Provide a graphical view of the field. This is a custom node for the user interface. 
 *
 * @author Arjun Dhir
 * @version 2024.02.03
 */

public class FieldCanvas extends Canvas {

    private static final int GRID_VIEW_SCALING_FACTOR = 6;
    private int width, height;
    private int xScale, yScale;
    GraphicsContext gc;
    
    /**
    * Create a new FieldView component.
    */
    public FieldCanvas(int height, int width) {
        super(height, width);
        gc = getGraphicsContext2D();
        this.height = height;
        this.width = width;
    }
    
    /**
     * The scale determines the actual size of the rectangles that are drawn
     */
    public void setScale(int gridHeight, int gridWidth) {
        xScale = width / gridWidth;
        yScale = height / gridHeight;
    
        if (xScale < 1)
            xScale = GRID_VIEW_SCALING_FACTOR;
    
        if (yScale < 1)
            yScale = GRID_VIEW_SCALING_FACTOR;
    }
  
    /**
    * Paint a rectangle of the given color on the canvas
    */
    public void drawMark(int x, int y, Color color) {
        gc.setFill(color);
        gc.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
    }
}
