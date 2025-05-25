package Transportation.Domain;

import java.util.Objects;

public class Item {
    private final int itemId;
    private final String itemName;
    private final float weight;

    public Item(int _itemId, String _itemName, float _weight) {
        itemId = _itemId;
        itemName = _itemName;
        weight = _weight;
    }

    public float getWeight() {
        return weight;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item newItem)) return false;
        return itemId == newItem.itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    @Override
    public String toString() {
        return itemName + " (Weight: " + weight + ")";
    }
}
