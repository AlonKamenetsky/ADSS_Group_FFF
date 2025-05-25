package Transportation.Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemManager {
    private final HashMap<Integer, Item> allItems;
    private int nextItemId = 1;

    public ItemManager() {
        allItems = new HashMap<>();
    }

    public Item getItemByName(String itemName) {
        for (Item i : allItems.values()) {
            if(i.getItemName().equalsIgnoreCase(itemName)) {
                return allItems.get(i.getItemId());
            }
        }
        return null;
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(allItems.values());
    }

    public String getAllItemsString() {
        List<Item> allItems = getAllItems();
        if (allItems.isEmpty()) return "No items available.";

        StringBuilder sb = new StringBuilder("All Items:\n");
        for (Item i : allItems) {
            sb.append(i).append("\n----------------------\n");
        }
        return sb.toString();
    }

    public void addItem(String itemName, float itemWeight) {
        int itemId = nextItemId++;
        Item newItem = new Item(itemId, itemName.toLowerCase(), itemWeight);
        allItems.put(itemId, newItem);
    }

    public void removeItem(String itemName) {
        allItems.remove(getItemByName(itemName).getItemId());
    }

    public boolean doesItemExist(String itemName) {
        Item currItem = getItemByName(itemName.toLowerCase());
        if (currItem == null) {
            return false;
        }
        int itemId = currItem.getItemId();
        return allItems.containsKey(itemId);
    }
}