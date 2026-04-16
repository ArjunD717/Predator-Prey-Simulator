import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.scene.paint.Color; 

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing 6 different animal species.
 * 
 * @author Arjun Dhir
 * @version 2025.02.10
 */

public class Simulator {

    private static final double WOLF_CREATION_PROBABILITY = 0.03;
    private static final double BEAR_CREATION_PROBABILITY = 0.02;
    private static final double COYOTE_CREATION_PROBABILITY = 0.05;
    private static final double SHEEP_CREATION_PROBABILITY = 0.09;
    private static final double DEER_CREATION_PROBABILITY = 0.11;
    private static final double SQUIRREL_CREATION_PROBABILITY = 0.13; 

    private List<Animal> animals;
    private Field field;
    private int step;
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        
        animals = new ArrayList<>();
        field = new Field(depth, width);

        reset();
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal.
     */
    public void simulateOneStep() {
        step++;
        List<Animal> newAnimals = new ArrayList<>();        

        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        animals.addAll(newAnimals);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        animals.clear();
        populate();
    }
    
    /**
     * Randomly populate the field with the animals.
     */
    private void populate() {
        
        Random rand = Randomizer.getRandom();
        field.clear();
        
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Wolf wolf = new Wolf(true, field, location, Color.BLUE,new Gene());
                    animals.add(wolf);
                }
                else if(rand.nextDouble() <= COYOTE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Coyote coyote = new Coyote(true, field, location, Color.RED,new Gene());
                    animals.add(coyote);
                }
                else if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location, Color.YELLOW,new Gene());
                    animals.add(bear);
                }
                else if(rand.nextDouble() <= SHEEP_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Sheep sheep = new Sheep(true, field, location, Color.PURPLE,new Gene());
                    animals.add(sheep);
                }
                else if(rand.nextDouble() <= DEER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Deer deer = new Deer(true, field, location, Color.BROWN,new Gene());
                    animals.add(deer);
                }
                else if(rand.nextDouble() <= SQUIRREL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Squirrel squirrel = new Squirrel(true, field, location, Color.PINK,new Gene());
                    animals.add(squirrel);
                }
                else{
                    Location location = new Location(row,col);
                    new Plant(field,location);
                }
                // else put a plant in the free space
            }
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    public void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
    

    public Field getField() {
        return field;
    }

    public int getStep() {
        return step;
    }
}
