import java.util.List;
import javafx.scene.paint.Color;
/**
 * Write a description of class Plant here.
 *
 * @author Arjun Dhir
 * @18/02/26
 */
public class Plant extends Animal
{
    /**
     * Constructor for objects of class Plant
     */
    public Plant(Field field, Location location)
    {
        
        super(field,location,Color.GREEN,new Gene("00000000000000"));
        //Plants don't have a gene in this scenario, therefore forced to 0.
    }
    
    @Override
    public void act(List<Animal> newAnimals){
        //Plants can't move
    }
    }
