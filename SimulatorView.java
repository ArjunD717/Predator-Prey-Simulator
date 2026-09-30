import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Graphical view and controls for the predator-prey simulation.
 *
 * <p>Shows one rectangle per field cell, the current generation, and live
 * population counts. Step advances a single generation, Run/Pause toggles
 * continuous animation, and Reset returns to a fresh (deterministically
 * re-seeded) starting position.
 *
 * @author Arjun Dhir
 */
public class SimulatorView extends Application {

    public static final int GRID_WIDTH = 100;
    public static final int GRID_HEIGHT = 80;
    public static final int WIN_WIDTH = 650;
    public static final int WIN_HEIGHT = 650;

    private static final Color EMPTY_COLOR = Color.WHITE;
    private static final int STEP_DELAY_MS = 500;

    private static final String GENERATION_PREFIX = "Generation: ";
    private static final String POPULATION_PREFIX = "Population: ";

    private Label genLabel;
    private Label populationLabel;
    private Label infoLabel;
    private Button runButton;

    private FieldCanvas fieldCanvas;
    private FieldStats stats;
    private Simulator simulator;
    private Timeline timeline;

    @Override
    public void start(Stage stage) {
        stats = new FieldStats();
        fieldCanvas = new FieldCanvas(WIN_WIDTH - 50, WIN_HEIGHT - 50);
        fieldCanvas.setScale(GRID_HEIGHT, GRID_WIDTH);
        simulator = new Simulator(GRID_HEIGHT, GRID_WIDTH);

        genLabel = new Label(GENERATION_PREFIX + "0");
        infoLabel = new Label(" ");
        populationLabel = new Label(POPULATION_PREFIX);

        Button stepButton = new Button("Step");
        stepButton.setOnAction(event -> stepOnce());
        runButton = new Button("Run");
        runButton.setOnAction(event -> toggleAutoRun());
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(event -> reset());

        HBox toolbar = new HBox(10, stepButton, runButton, resetButton, genLabel, infoLabel);
        toolbar.setPadding(new Insets(8));
        HBox statusBar = new HBox(populationLabel);
        statusBar.setPadding(new Insets(8));

        timeline = new Timeline(new KeyFrame(Duration.millis(STEP_DELAY_MS), event -> stepOnce()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        BorderPane root = new BorderPane(fieldCanvas, toolbar, null, statusBar, null);
        stage.setScene(new Scene(root, WIN_WIDTH, WIN_HEIGHT));
        stage.setTitle("Predator/Prey Simulation");
        stage.setOnCloseRequest(event -> stopAutoRun());
        updateCanvas(simulator.getStep(), simulator.getField());
        stage.show();
    }

    /** Display a short status message in the toolbar. */
    public void setInfoText(String text) {
        infoLabel.setText(text);
    }

    /**
     * Show the current status of the field.
     *
     * @param generation current generation
     * @param field field whose status is displayed
     */
    public void updateCanvas(int generation, Field field) {
        genLabel.setText(GENERATION_PREFIX + generation);
        stats.reset();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Animal animal = field.getObjectAt(row, col);
                if (animal != null && animal.isAlive()) {
                    if (!(animal instanceof Plant)) {
                        stats.incrementCount(animal.getClass());
                    }
                    fieldCanvas.drawMark(col, row, animal.getColor());
                }
                else {
                    fieldCanvas.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();
        populationLabel.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
    }

    /**
     * @return true while at least two non-plant species are alive
     */
    public boolean isViable(Field field) {
        return stats.isViable(field);
    }

    /**
     * Advance one generation, stopping the animation when the ecosystem is
     * no longer viable.
     */
    public void stepOnce() {
        synchronized (simulator) {
            simulator.simulateOneStep();
            updateCanvas(simulator.getStep(), simulator.getField());
        }
        if (!isViable(simulator.getField())) {
            stopAutoRun();
            setInfoText("Simulation ended: fewer than two species remain.");
        }
    }

    /** Start or pause continuous animation. */
    public void toggleAutoRun() {
        if (timeline.getStatus() == Timeline.Status.RUNNING) {
            stopAutoRun();
        }
        else if (!isViable(simulator.getField())) {
            setInfoText("Simulation ended: fewer than two species remain.");
        }
        else {
            setInfoText(" ");
            timeline.play();
            runButton.setText("Pause");
        }
    }

    /** Stop continuous animation, if running. */
    public void stopAutoRun() {
        timeline.stop();
        runButton.setText("Run");
    }

    /**
     * Run the simulation from its current state for the given number of
     * generations on a background thread, stopping early when it ceases to
     * be viable. Any running animation is stopped first so the two loops
     * cannot step the simulator concurrently.
     *
     * @param numSteps generations to run
     */
    public void simulate(int numSteps) {
        stopAutoRun();
        Thread worker = new Thread(() -> {
            for (int step = 0; step < numSteps && isViable(simulator.getField()); step++) {
                int generation;
                synchronized (simulator) {
                    simulator.simulateOneStep();
                    generation = simulator.getStep();
                }
                simulator.delay(STEP_DELAY_MS);
                final int shown = generation;
                Platform.runLater(() -> {
                    synchronized (simulator) {
                        updateCanvas(shown, simulator.getField());
                    }
                });
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    /** Return the simulation to a fresh starting position. */
    public void reset() {
        stopAutoRun();
        synchronized (simulator) {
            simulator.reset();
        }
        setInfoText(" ");
        updateCanvas(simulator.getStep(), simulator.getField());
    }

    @Override
    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Launch the application on the JavaFX application thread.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
