package Transportation.Service;

import Transportation.Domain.ItemManager;

public class ItemService {
    private final ItemManager itemManager;

    public ItemService(ItemManager itemManager1) {
        itemManager = itemManager1;
    }

    public void addItem(String itemName, float weight) {
        if (itemName == null || weight < 0) {
            return;
        }
        itemManager.addItem(itemName, weight);
    }

    public void deleteItem(String itemName) {
        if (itemName == null) {
            return;
        }
        if (itemManager.doesItemExist(itemName)) {
            itemManager.removeItem(itemName);
        }
    }

    public String viewAllItems() {
        return itemManager.getAllItemsString();
    }

    public boolean doesItemExist(String itemName) {
        if (itemName == null) {
            return false;
        }
        else {
            return itemManager.doesItemExist(itemName);
        }
    }
}