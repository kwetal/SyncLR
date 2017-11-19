package nl.ocwduo.autorisatie;

import java.util.*;

/**
 *
 * @author arjen
 */
public class Synchronizer
{
    public Synchronizer(SyncableObjectProvider left, SyncableObjectProvider right) {
        leftCollection = left;
        rightCollection = right;
    }
    private final Differences differences = new Differences();

    private final SyncableObjectProvider leftCollection;
    private final SyncableObjectProvider rightCollection;

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
    
    public void synchronizeLeftToRight() {
        for (LREntry entry: differences) {
            if (entry.getLeftItem() == null) {
                rightCollection.delete(entry.getRightItem());
            } else if (entry.getRightItem() == null) {
                rightCollection.createItemFrom(entry.getLeftItem());
            } else {
                rightCollection.updateFrom(entry.getLeftItem(), entry.getRightItem());
            }
        }
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

class LREntry {
    static LREntry createLeftEntry(SyncableObject item) {
        LREntry entry = new LREntry(item.getKey(), item);
        return entry;
    }

    static LREntry createRightEntry(SyncableObject item) {
        LREntry entry = new LREntry(item.getKey(), null);
        entry.rightItem = item;
        return entry;
    }

    void addRightItem(SyncableObject item) {
        if (rightItem != null) {
            throw new IllegalStateException("right item can be set only once");
        }
        rightItem = item;
    }

    boolean equalItems() {
        return leftItem.isEqualTo(rightItem);
    }

    private LREntry(String key, SyncableObject item) {
        this.key = key;
        leftItem = item;
    }

    private final String key;
    private final SyncableObject leftItem;
    private SyncableObject rightItem;

    String getKey() { return key; }

    SyncableObject getLeftItem() { return leftItem; }

    SyncableObject getRightItem() { return rightItem; }

}