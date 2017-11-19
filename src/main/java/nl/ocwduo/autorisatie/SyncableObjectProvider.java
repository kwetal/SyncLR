package nl.ocwduo.autorisatie;

/**
 *
 * @author arjen
 */
public interface SyncableObjectProvider
{
    /** Iterator to retrieve te complete set of objects, one by one.
     *
     * @return
     */
    Iterable<? extends SyncableObject> getCollection();

    /** This method will be called to create a new item in this collection.
     *
     * @param item Example item from which the data must be taken.
     */
    public void createItemFrom(SyncableObject item);

    public void delete(SyncableObject rightItem);

    public void updateFrom(SyncableObject leftItem, SyncableObject rightItem);

}
