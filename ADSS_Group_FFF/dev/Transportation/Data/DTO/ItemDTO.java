package Transportation.Data.DTO;

import Transportation.Domain.Item;

public class ItemDTO {
    private final int itemId;
    private final String itemName;
    private final float weight;

    //Constructor
    public ItemDTO(int _itemId, String _itemName, float _weight) {
        this.itemId = _itemId;
        this.itemName = _itemName;
        this.weight = _weight;
    }

    //Convert Item to ItemDTO
    public static ItemDTO fromEntity(Item item) {
        return new ItemDTO(item.getItemId(), item.getItemName(), item.getWeight());
    }

    //Getters
    public int getItemId() {
        return itemId;
    }
    public String getItemName() {
        return itemName;
    }
    public float getWeight() {
        return weight;
    }
}
