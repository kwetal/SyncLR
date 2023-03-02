package nl.ocwduo.autorisatie;

/**
 *
 * @author arjen
 */
public interface SyncableObject
{
    /** Retrieve the key for the object.
     * Objects from the left and the right set will be matched with this key.
     * @return The unique key for the object in the Left or Right set. Assumed to be non-null.
     */
    String getKey();
    /** Determine whether two objects are equal.
     * Objects will get compared by the algorithm when they have equal keys.
     * @param that The other object.
     * @return true if all the "essential" fields are equal and the objects don't need to be synchronised.
     */
    boolean isEqualTo(SyncableObject that);
}
