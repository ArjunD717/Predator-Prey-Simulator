import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color; 

/**
 * A simple model of a Squirrel .
 * Squirrel s age, move, breed, and die.
 * 
 * @author Arjun Dhir
 * @version 2025.02.10
 */
public class Squirrel  extends Animal implements Prey {
    private static final int FOOD_VALUE = 5;
    private static final double SPREAD_CHANCE = 0.2;
    private static final int SICK_DURATION = 7;
    private static final Random rand = Randomizer.getRandom();
    
    private int age;
    private double foodLevel;
    private boolean sick;
    private int illSteps;
    /**
     * Create a new Squirrel . A Squirrel  may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Squirrel  will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Squirrel (boolean randomAge, Field field, Location location, Color col, Gene gene) {
        super(field, location, col, gene);
        sick = false;
        
        if(randomAge) {
            age = rand.nextInt(LIFE_SPAN);
            foodLevel = rand.nextInt(FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = FOOD_VALUE;
        }
    }
    
    /**
     * This is what makes the animal sick
     */
    private void becomeSick(){
        if ((!sick) && Randomizer.getRandom().nextDouble() < DISEASE_PROBABILITY){
            sick = true;
            illSteps = SICK_DURATION;
        }
    }
    
    /** 
     * This keeps a track of the steps that you have taken being ill
     */
    private void updateIllness(){
        if (sick){
            illSteps -= 1;
            if (illSteps == 0){
                setDead();
            }
        }
    }
    
    private boolean isSick(){
        return sick;
    }
    
    private void setSick(){
        sick = true;
    }
    
    private void setIllSteps(){
        illSteps = SICK_DURATION;
    }
    
    /** 
     * Disease only spreads to its own species,
     */
    private void spreadIllness(){
        if (!sick || !isAlive()){
            return; 
        }
        
        Field field = getField();
        
        for (Location location : field.adjacentLocations(getLocation())){
            Animal animal = field.getObjectAt(location);
            if (animal instanceof Squirrel){
                
                Squirrel a = (Squirrel) animal;
                if (a!=null && a.isAlive() && !a.isSick() && Randomizer.getRandom().nextDouble() < SPREAD_CHANCE){
                    a.setSick();
                    a.setIllSteps();
                }
            }
        }
    }
    
    /**
     * Make this sheep more hungry. This could result in the sheep's death.
     */
    private void incrementHunger() {
        foodLevel = foodLevel - METABOLISM;
        if(foodLevel <= 0) {
            setDead();
        }
    }    
    
    public int getFood(){
        return FOOD_VALUE;
    }
    
    /**
     * Look for food adjacent to the current location.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location loc : adjacent){
            if (field.getObjectAt(loc) instanceof Plant&& (Randomizer.getRandom().nextDouble() < 0.50)){
                foodLevel = FOOD_VALUE;
                return loc;
            }
        }
        return getField().getFreeAdjacentLocation(getLocation());
    }
    
    /**
     * This is what the Squirrel  does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newSquirrel  A list to return newly born Squirrel .
     */
    public void act(List<Animal> newSquirrel ) {
        incrementAge();
        incrementHunger();
        becomeSick();
        updateIllness();
        if (!isAlive()){
            return;
        }
        spreadIllness();
        if(isAlive()) {
            giveBirth(newSquirrel);            
            // Try to move into a free location.
            Location newLocation = findFood();
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the Squirrel 's death.
     */
    private void incrementAge() {
        age++;
        if(age > LIFE_SPAN) {
            setDead();
        }
    }
    
    
    /**
     * Check whether or not this Squirrel  is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSquirrel  A list to return newly born Squirrel .
     */
    private void giveBirth(List<Animal> newSquirrel) {
        if (isMale() || !canBreed()){
            return;
        }
        
        Field field = getField();
        List<Animal> neighbours = field.getLivingNeighbours(getLocation());
        for (Animal animal : neighbours){
            if (animal.getClass() == getClass() && animal.isMale()){
                if (rand.nextDouble() > gene.getBreedingProbability()){
                    continue;
                }
                List<Location> free = field.getFreeAdjacentLocations(getLocation());
                int births = breed();
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Gene parent1 = animal.getGene();
                    Gene parent2 = this.getGene();
                Gene gene = new Gene( parent1.getStringGene().substring(0,7) + parent2.getStringGene().substring(7,14));
                    Squirrel young = new Squirrel(false, field, loc, getColor(), gene);
                    newSquirrel.add(young);
                }   
            }
        }
    }      
     
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed() {
        int births = 0;
        if(canBreed()) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }
    
    /**
     * A Squirrel  can breed if it has reached the breeding age.
     * @return true if the Squirrel  can breed, false otherwise.
     */
    private boolean canBreed() {
        return age >= BREEDING_AGE;
    }
}
