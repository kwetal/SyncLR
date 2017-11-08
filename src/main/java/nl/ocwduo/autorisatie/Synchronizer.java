package nl.ocwduo.autorisatie;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

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
            } else {
                entry.setRightItem(rightItem);
                if (entry.equalItems()) {
                    differences.remove(key);
                }
            }
        }
    }
    
    public void synchronizeLeftToRight() {
        for (LREntry entry: differences) {
            if (!entry.getSelected()) continue;
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
        return new MyIterator();
    }

    private class MyIterator implements Iterator<LREntry> {

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
        LREntry entry = new LREntry(item.getKey());
        entry.setLeftItem(item);
        return entry;
    }

    static LREntry createRightEntry(SyncableObject item) {
        LREntry entry = new LREntry(item.getKey());
        entry.setRightItem(item);
        return entry;
    }

    private LREntry(String key) {
        this.key = key;
    }

    private final String key;
    private SyncableObject leftItem;
    private SyncableObject rightItem;
    private boolean selected;

    String getKey() { return key; }

    boolean getSelected() { return selected; }

    void setSelected(boolean value) { selected = value; }

    void setLeftItem(SyncableObject item) {
        if (leftItem != null) {
            throw new IllegalStateException("left item can be set only once");
        }
        leftItem = item;
    }

    SyncableObject getLeftItem() { return leftItem; }

    void setRightItem(SyncableObject item) {
        if (rightItem != null) {
            throw new IllegalStateException("right item can be set only once");
        }
        rightItem = item;
    }

    SyncableObject getRightItem() { return rightItem; }

    boolean equalItems() {
        return leftItem.isEqualTo(rightItem);
    }
}