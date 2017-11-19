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

    /** This method will be called to deleteItem an item from the collection.
     *
     * @param item the item to be removed.
     */
    public void deleteItem(SyncableObject item);

    /**
     * This method will be called to update the differing attributes.
     * @param leftItem The item to take the attributes from.
     * @param rightItem The item to transfer the attributes to.
     */
    public void updateItemFrom(SyncableObject leftItem, SyncableObject rightItem);

}
