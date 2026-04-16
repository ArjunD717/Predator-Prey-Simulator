import java.util.Random;

/**
 * Write a description of class Gene here.
 *
 * @author Arjun Dhir
 * @version (a version number or a date)
 */
public class Gene
{
    private final String STRING_GENE;
    private final int BREEDING_AGE;
    private final int LIFE_SPAN;
    private final double BREEDING_PROBABILITY;
    private final int LITTER_SIZE;
    private final double DISEASE_PROBABILITY;
    private final double METABOLISM;
    
    public Gene(){
        Random rand = new Random();
        
        int breedingAge= rand.nextInt(79) + 12;
        int lifeSpan = rand.nextInt(111) + 10;
        int breedingProbability = rand.nextInt(51) ;
        int litterSize = rand.nextInt(12) + 1;
        int diseaseProbability = rand.nextInt(51) ;
        int metabolism = (rand.nextInt(76) + 25) ;
        
        STRING_GENE = String.format( "%02d%03d%02d%02d%02d%03d", breedingAge, lifeSpan, breedingProbability,litterSize,diseaseProbability,metabolism);
        
        BREEDING_AGE = breedingAge;
        LIFE_SPAN = lifeSpan;
        BREEDING_PROBABILITY = breedingProbability / 100.0;
        LITTER_SIZE = litterSize;
        DISEASE_PROBABILITY = diseaseProbability / 100.0;
        METABOLISM = metabolism / 100.0 ;
         
    }
    
    public Gene (String gene){
        if ( gene.equals("00000000000000")){
            STRING_GENE = gene;
            BREEDING_AGE = Integer.parseInt(gene.substring(0,2));
            LIFE_SPAN = Integer.parseInt(gene.substring(2,5));
            BREEDING_PROBABILITY = Integer.parseInt(gene.substring(5,7)) / 100.0;
            LITTER_SIZE = Integer.parseInt(gene.substring(7,9));
            DISEASE_PROBABILITY = Integer.parseInt(gene.substring(9,11)) / 100.0;
            METABOLISM = Integer.parseInt(gene.substring(11,14)) / 100.0;
        }
        else{
            STRING_GENE = mutate(gene);
            BREEDING_AGE = Integer.parseInt(STRING_GENE.substring(0,2));
            LIFE_SPAN = Integer.parseInt(STRING_GENE.substring(2,5));
            BREEDING_PROBABILITY = Integer.parseInt(STRING_GENE.substring(5,7)) / 100.0;
            LITTER_SIZE = Integer.parseInt(STRING_GENE.substring(7,9));
            DISEASE_PROBABILITY = Integer.parseInt(STRING_GENE.substring(9,11)) / 100.0;
            METABOLISM = Integer.parseInt(STRING_GENE.substring(11,14)) / 100.0;
    }
    }
    public int getBreedingAge(){
        return BREEDING_AGE;
    }
    
    public int getLifeSpan(){
        return LIFE_SPAN;
    }
    
    public double getBreedingProbability(){
        return BREEDING_PROBABILITY;
    }
    
    public int getLitterSize(){
        return LITTER_SIZE;
    }
    
    public double getDiseaseProbability(){
        return DISEASE_PROBABILITY;
    }
    
    public double getMetabolism(){
        return METABOLISM;
    }
    
    public String getStringGene(){
        return STRING_GENE;
    }
    
    /**
     * Ensures all gene values are within the limits after mutating. 
     */
    public int limit(int attribute, int lower, int upper){
        if (attribute > upper){
            return upper;
        }
        if (attribute < lower){
            return lower;
        }
        return attribute;
    }
    
    public String mutate(String gene){
        Random rand = Randomizer.getRandom();
        
        String mutated_gene = "";
        
        for (int i = 0; i < gene.length(); i++){
            int current = Integer.parseInt(gene.substring(i,i+1));
            
            if (rand.nextDouble() < 0.2){
                double chance = rand.nextDouble();
                if (chance < 0.5){
                    if (current != 0){
                        current -= 1;
                    }
                }
                
                if (chance > 0.5){
                    if (current !=9){
                        current += 1;
                    }
                }
                
            }
            mutated_gene+=current;
        }
        
        
        int breedingAgeTemp = limit(Integer.parseInt(mutated_gene.substring(0, 2)), 12, 90);
        int lifeSpanTemp = limit(Integer.parseInt(mutated_gene.substring(2, 5)), 10, 120);
        int breedingProbabilityTemp = limit(Integer.parseInt(mutated_gene.substring(5, 7)), 0, 50);
        int litterSizeTemp = limit(Integer.parseInt(mutated_gene.substring(7, 9)), 1, 12);
        int diseaseProbabilityTemp = limit(Integer.parseInt(mutated_gene.substring(9, 11)), 0, 50);
        int metabolismTemp = limit(Integer.parseInt(mutated_gene.substring(11, 14)), 25, 100);
        
        
        
        
        return String.format("%02d%03d%02d%02d%02d%03d",breedingAgeTemp,lifeSpanTemp,breedingProbabilityTemp,litterSizeTemp,diseaseProbabilityTemp,metabolismTemp);
    }
}
