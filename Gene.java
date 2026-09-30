import java.util.Objects;
import java.util.Random;

/**
 * Immutable genetic traits for one animal, encoded as a 14-digit string:
 *
 * <pre>
 * positions  traits               range
 * 0-1        breeding age         12-90
 * 2-4        lifespan             10-120
 * 5-6        breeding probability 10-80 (%)
 * 7-8        litter size          1-12
 * 9-10       disease probability  0-50 (%)
 * 11-13      metabolism           25-100 (% of one food unit per step)
 * </pre>
 *
 * <p>Offspring inherit the first half (age, lifespan, breeding probability)
 * from their father and the second half (litter size, disease probability,
 * metabolism) from their mother; each digit then mutates up or down by one
 * with a 20% probability and the fields are clamped back into range.
 *
 * @author Arjun Dhir
 */
public final class Gene {

    /** Length of every gene string. */
    public static final int GENE_LENGTH = 14;
    /**
     * Crossover point between the paternal half (breeding age, lifespan,
     * breeding probability) and the maternal half (litter size, disease
     * probability, metabolism).
     */
    public static final int CROSSOVER_INDEX = 7;

    public static final int MIN_BREEDING_AGE = 12;
    public static final int MAX_BREEDING_AGE = 90;
    public static final int MIN_LIFE_SPAN = 10;
    public static final int MAX_LIFE_SPAN = 120;
    public static final int MIN_BREEDING_PROBABILITY = 10;
    public static final int MAX_BREEDING_PROBABILITY = 80;
    public static final int MIN_LITTER_SIZE = 1;
    public static final int MAX_LITTER_SIZE = 12;
    public static final int MIN_DISEASE_PROBABILITY = 0;
    public static final int MAX_DISEASE_PROBABILITY = 50;
    public static final int MIN_METABOLISM = 25;
    public static final int MAX_METABOLISM = 100;
    private static final double MUTATION_RATE = 0.2;
    private static final String STERILE_CODE = "00000000000000";

    private final String code;
    private final int breedingAge;
    private final int lifeSpan;
    private final double breedingProbability;
    private final int litterSize;
    private final double diseaseProbability;
    private final double metabolism;

    /** Roll a completely random gene. */
    public Gene() {
        this(randomCode(Randomizer.getRandom()));
    }

    /**
     * Decode an exact gene string.
     *
     * @param code 14 digits as described above
     * @throws IllegalArgumentException if the code is not 14 digits
     */
    public Gene(String code) {
        this.code = validate(code);
        breedingAge = parse(code, 0, 2);
        lifeSpan = parse(code, 2, 5);
        breedingProbability = parse(code, 5, 7) / 100.0;
        litterSize = parse(code, 7, 9);
        diseaseProbability = parse(code, 9, 11) / 100.0;
        metabolism = parse(code, 11, 14) / 100.0;
    }

    /**
     * Breed two genes: crossover at the paternal/maternal boundary, then mutate.
     *
     * @param father source of the first half (must not be null)
     * @param mother source of the second half (must not be null)
     * @return the offspring gene
     */
    public static Gene combine(Gene father, Gene mother) {
        Objects.requireNonNull(father, "father gene must not be null");
        Objects.requireNonNull(mother, "mother gene must not be null");
        String crossed = father.code.substring(0, CROSSOVER_INDEX)
                + mother.code.substring(CROSSOVER_INDEX);
        return new Gene(mutate(crossed, Randomizer.getRandom()));
    }

    /** Gene for plants, which do not evolve. */
    public static Gene sterile() {
        return new Gene(STERILE_CODE);
    }

    public int getBreedingAge() {
        return breedingAge;
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    public double getBreedingProbability() {
        return breedingProbability;
    }

    public int getLitterSize() {
        return litterSize;
    }

    public double getDiseaseProbability() {
        return diseaseProbability;
    }

    public double getMetabolism() {
        return metabolism;
    }

    public String getStringGene() {
        return code;
    }

    private static String randomCode(Random rand) {
        return String.format("%02d%03d%02d%02d%02d%03d",
                MIN_BREEDING_AGE + rand.nextInt(MAX_BREEDING_AGE - MIN_BREEDING_AGE + 1),
                MIN_LIFE_SPAN + rand.nextInt(MAX_LIFE_SPAN - MIN_LIFE_SPAN + 1),
                MIN_BREEDING_PROBABILITY + rand.nextInt(MAX_BREEDING_PROBABILITY - MIN_BREEDING_PROBABILITY + 1),
                MIN_LITTER_SIZE + rand.nextInt(MAX_LITTER_SIZE - MIN_LITTER_SIZE + 1),
                MIN_DISEASE_PROBABILITY + rand.nextInt(MAX_DISEASE_PROBABILITY - MIN_DISEASE_PROBABILITY + 1),
                MIN_METABOLISM + rand.nextInt(MAX_METABOLISM - MIN_METABOLISM + 1));
    }

    /**
     * Nudge each digit up or down by one with a 20% probability, then clamp
     * every field back into range.
     */
    private static String mutate(String code, Random rand) {
        char[] digits = code.toCharArray();
        for (int i = 0; i < digits.length; i++) {
            if (rand.nextDouble() < MUTATION_RATE) {
                int digit = digits[i] - '0';
                if (rand.nextDouble() < 0.5) {
                    digit = Math.max(0, digit - 1);
                }
                else {
                    digit = Math.min(9, digit + 1);
                }
                digits[i] = (char) ('0' + digit);
            }
        }
        String mutated = new String(digits);
        return String.format("%02d%03d%02d%02d%02d%03d",
                clamp(parse(mutated, 0, 2), MIN_BREEDING_AGE, MAX_BREEDING_AGE),
                clamp(parse(mutated, 2, 5), MIN_LIFE_SPAN, MAX_LIFE_SPAN),
                clamp(parse(mutated, 5, 7), MIN_BREEDING_PROBABILITY, MAX_BREEDING_PROBABILITY),
                clamp(parse(mutated, 7, 9), MIN_LITTER_SIZE, MAX_LITTER_SIZE),
                clamp(parse(mutated, 9, 11), MIN_DISEASE_PROBABILITY, MAX_DISEASE_PROBABILITY),
                clamp(parse(mutated, 11, 14), MIN_METABOLISM, MAX_METABOLISM));
    }

    private static int clamp(int value, int lower, int upper) {
        return Math.min(upper, Math.max(lower, value));
    }

    private static int parse(String code, int begin, int end) {
        return Integer.parseInt(code.substring(begin, end));
    }

    private static String validate(String code) {
        Objects.requireNonNull(code, "gene code must not be null");
        if (code.length() != GENE_LENGTH || !code.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("gene code must be " + GENE_LENGTH + " digits: " + code);
        }
        return code;
    }
}
