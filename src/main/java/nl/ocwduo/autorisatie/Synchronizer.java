package nl.ocwduo.autorisatie;

import java.util.*;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

/**
 * Compares two collections of items, show the differences between them and update one collection to agree with the other.
 * The synchronization direction is from left to right.
 * @author arjen
 */
public class Synchronizer
{
    /** Initalize the synchronizer by specifying the two collections to be compared.
     *
     * @param left the leading collection
     * @param right the following collection, that should equal the leading collections.
     */
    public Synchronizer(SyncableObjectProvider left, SyncableObjectProvider right) {
        leftCollection = left;
        rightCollection = right;
    }
    private final Differences differences = new Differences();

    private final SyncableObjectProvider leftCollection;
    private final SyncableObjectProvider rightCollection;

    /**
     * Collect the items from the left and right collection and align them according to their keys.
     * This yields the following 4 possibilities:
     * <ol>
     * <li> key present in L, and missing in R.
     * <li> key present in R, and missing in L.
     * <li> key present in both L and R, but items differ on some attributes. The method isEqualTo (NOTE: not equals()) will be called to determine equality of two items.
     * <li> key present in both L and R, and items are equivalent. These items will be excluded from further processing.
     * </ol>
     */
    public void collectTheSets() {
        for (SyncableObject item: leftCollection.getCollection()) {
            differences.add(LREntry.createLeftEntry(item));
        }
        for (SyncableObject rightItem : rightCollection.getCollection()) {
            final String key = rightItem.getKey();
            LREntry entry = differences.find(key);
            if (entry == null) {
                differences.add(LREntry.createRightEntry(rightItem));
            } else if (entry.getLeftItem().isEqualTo(rightItem)) {
                differences.remove(key);
            } else {
                entry.addRightItem(rightItem);
            }
        }
    }

    /**
     * Synchronize the R collection to agree with the L collection.
     * <ol>
     * <li> key present in L, and missing in R: create an entry in the R collection using the corresponding item from the L collection as the template.
     * <li> key present in R, and missing in L: delete the entry from the R collection, using its key as the index.
     * <li> key present in both L and R, but items differ on some attributes: update the items in the R collection using the corresponding items from the L collection as the template.
     * </ol>
     */
    public void synchronizeLeftToRight() {
        for (LREntry entry: differences) {
            if (entry.getLeftItem() == null) {
                rightCollection.deleteItem(entry.getRightItem());
            } else if (entry.getRightItem() == null) {
                rightCollection.createItemFrom(entry.getLeftItem());
            } else {
                rightCollection.updateItemFrom(entry.getLeftItem(), entry.getRightItem());
            }
        }
    }

    /**
     * List the items whose keys are present in the R collection but missing in the L collection.
     * @return An iterator that lists the items.
     */
    public Iterator<LREntry> leftMissingItems() {
        return new FilterIterator(differences.iterator(), new Predicate()
        {
            @Override
            public boolean evaluate(Object o) {
                return ((LREntry)o).getLeftItem() == null;
            }
        });
    }

    /**
     * List the items whose keys are present in the L collection but missing in the R collection.
     * @return An iterator that lists the items.
     */
    public Iterator<LREntry> rightMissingItems() {
        return new FilterIterator(differences.iterator(), new Predicate()
        {
            @Override
            public boolean evaluate(Object t) {
                return ((LREntry)t).getRightItem() == null;
            }
        });
    }

    /**
     * List the items whose keys are present both L and R, but that differ according to isEqualTo().
     * @return
     */
    public Iterator<LREntry> differingItems() {
        return new FilterIterator(differences.iterator(), new Predicate()
        {
            @Override
            public boolean evaluate(Object o) {
                LREntry t = (LREntry)o;
                return t.getLeftItem() != null && t.getRightItem() != null;
            }
        });
    }
}

class Differences implements Iterable<LREntry> {
    private final NavigableMap<String, LREntry> differenceSet = new TreeMap<>();
    void add(LREntry entry) {
        differenceSet.put(entry.getKey(), entry);
    }
    LREntry find(String key) {
        return differenceSet.get(key);
    }
    void remove(String key) {
        differenceSet.remove(key);
    }

    @Override
    public Iterator<LREntry> iterator() {
        return new AllEntries();
    }

    private class AllEntries implements Iterator<LREntry> {

        Map.Entry<String, LREntry> current = differenceSet.firstEntry();

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public LREntry next() {
            LREntry retval = current.getValue();
            current = differenceSet.higherEntry(current.getKey());
            return retval;
        }

    }
}

