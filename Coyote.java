import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.LinkedList;
import javafx.scene.paint.Color; 

/**
 * A simple model of a Coyote.
 * Coyotes age, move, eat prey, and die.
 * 
 * @author Arjun Dhir
 * @version 2025.02.10
 */
public class Coyote extends Animal {
    private static final int BASE_FOOD_VALUE = 9;
    private static final double SPREAD_CHANCE = 0.13;
    private static final int SICK_DURATION = 4;
    private static final Random rand = Randomizer.getRandom();
    
    private int age;
    private double foodLevel;
    private boolean sick;
    private int illSteps;
    /**
     * Create a Coyote. A Coyote can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Coyote will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Coyote(boolean randomAge, Field field, Location location, Color col, Gene gene) {
        super(field, location, col,gene);
        sick = false;
        
        if(randomAge) {
            age = rand.nextInt(LIFE_SPAN);
            foodLevel = rand.nextInt(BASE_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = BASE_FOOD_VALUE;
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
            if (animal instanceof Coyote){
                
                Coyote a = (Coyote) animal;
                if (a!=null && a.isAlive() && !a.isSick() && Randomizer.getRandom().nextDouble() < SPREAD_CHANCE){
                    a.setSick();
                    a.setIllSteps();
                }
            }
        
        }
    }
    
    
    /**
     * This is what the Coyote does most of the time: it hunts for
     * prey. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newCoyotes A list to return newly born Coyotes.
     */
    public void act(List<Animal> newCoyotes) {
        incrementAge();
        incrementHunger();
        becomeSick();
        updateIllness();
        if (!isAlive()){
            return;
        }
        spreadIllness();
        
        if(isAlive()) {
            giveBirth(newCoyotes);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().getFreeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
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
     * Increase the age. This could result in the Coyote's death.
     */
    private void incrementAge() {
        age++;
        if(age > LIFE_SPAN) {
            setDead();
        }
    }
    
    /**
     * Make this Coyote more hungry. This could result in the Coyote's death.
     */
    private void incrementHunger() {
        foodLevel = foodLevel - METABOLISM;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Animal animal = field.getObjectAt(where);
            if(animal instanceof Prey) {
                Prey prey = (Prey) animal;
                if(animal.isAlive()) { 
                    foodLevel += prey.getFood();
                    animal.setDead();
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this Coyote is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCoyotes A list to return newly born Coyotes.
     */
    private void giveBirth(List<Animal> newCoyote) {
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
                    Coyote young = new Coyote(false, field, loc, getColor(), gene);
                    newCoyote.add(young);
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
     * A Coyote can breed if it has reached the breeding age.
     */
    private boolean canBreed() {
        return age >= BREEDING_AGE;
    }
}
