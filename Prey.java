/**
 * Something a {@link Predator} can hunt. The food value is added to the
 * hunter's reserves when the prey is killed.
 *
 * @author Arjun Dhir
 */
public interface Prey {

    /**
     * @return food units the hunter gains from this kill
     */
    int getFood();
}
