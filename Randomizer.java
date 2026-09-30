import java.util.Random;

/**
 * Single source of randomness for the simulation. A shared fixed-seed
 * generator keeps repeated runs identical, which makes behaviour testable.
 * Flip {@code USE_SHARED} to false for different behaviour every run.
 *
 * @author Arjun Dhir
 */
public final class Randomizer {

    private static final int SEED = 1111;
    private static final Random SHARED = new Random(SEED);
    private static final boolean USE_SHARED = true;

    private Randomizer() {
        // Utility class; never instantiated.
    }

    /** @return the simulation random generator */
    public static Random getRandom() {
        if (USE_SHARED) {
            return SHARED;
        }
        return new Random();
    }

    /**
     * Re-seed the shared generator. No effect when randomness is unshared.
     * Called by {@link Simulator#reset()} so a fresh run repeats exactly.
     */
    public static void reset() {
        if (USE_SHARED) {
            SHARED.setSeed(SEED);
        }
    }
}
