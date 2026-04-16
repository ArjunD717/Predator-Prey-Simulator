import java.util.List;
import javafx.scene.paint.Color;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author Arjun Dhir
 * @version 2025.02.10
 */

public abstract class Animal {
    
    private boolean alive;
    private Field field;
    private Location location;
    private Color color = Color.BLACK;
    protected String gender;
    protected Gene gene;
    
    protected int BREEDING_AGE;
    protected int LIFE_SPAN;
    protected double BREEDING_PROBABILITY;
    protected int MAX_LITTER_SIZE;
    protected double DISEASE_PROBABILITY;
    protected double METABOLISM;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    
    public Animal(Field field, Location location, Color col, Gene gene){
        alive = true;
        this.field = field;
        setLocation(location);
        setColor(col);
        gender = setGender();
        if (gene == null){
            this.gene = new Gene();
        }
        else{
            this.gene = gene;
       }
             
        BREEDING_AGE = this.gene.getBreedingAge();
        LIFE_SPAN = this.gene.getLifeSpan();
        BREEDING_PROBABILITY = this.gene.getBreedingProbability();
        MAX_LITTER_SIZE = this.gene.getLitterSize();
        METABOLISM = this.gene.getMetabolism();
        DISEASE_PROBABILITY = this.gene.getDiseaseProbability();
        
    }
    
    protected Gene getGene(){
        return this.gene;
    }
    /**
     * This function randomly assigns the gender of the animal.
     * 
     */
    private String setGender(){
        Double chance = Randomizer.getRandom().nextDouble();
        if (chance <= 0.5){
            return "Male";
        }
        else{
            return "Female";
        }
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive() {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead() {
        alive = false;
        if(location != null) {
            Plant plant = new Plant(field,location); 
            field.place(plant, location); //Replaces dead animal with the plant
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation() {
        return location;
    }
    
    protected boolean isMale(){
        return gender.equals("Male") ;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation) {
        if(location != null) {
            new Plant(field,location); //When an animal moves from a cell, creates a new plant
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField() {
        return field;
    }
    
    /**
     * Changes the color of the animal
     */
    public void setColor(Color col) {
        color = col;
    }

    /**
     * Returns the animal's color
     */
    public Color getColor() {
        return color;
    }   
}
