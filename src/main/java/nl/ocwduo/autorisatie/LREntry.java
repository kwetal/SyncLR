package nl.ocwduo.autorisatie;


public class LREntry {
    public static LREntry createLeftEntry(SyncableObject item) {
        LREntry entry = new LREntry(item.getKey(), item);
        return entry;
    }

    public static LREntry createRightEntry(SyncableObject item) {
        LREntry entry = new LREntry(item.getKey(), null);
        entry.rightItem = item;
        return entry;
    }

    public void addRightItem(SyncableObject item) {
        if (rightItem != null) {
            throw new IllegalStateException("right item can be set only once");
        }
        rightItem = item;
    }

    public boolean equalItems() {
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
