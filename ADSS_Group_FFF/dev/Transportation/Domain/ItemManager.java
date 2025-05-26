package Transportation.Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import Transportation.Domain.Repositories.ItemRepository;
import Transportation.DTO.ItemDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ItemManager {
    //private final HashMap<Integer, Item> allItems;
    //private int nextItemId = 1;
    private final ItemRepository itemRepository;

    public ItemManager(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void addItem(String itemName, float itemWeight) {
        try {
            itemRepository.addItem(itemName.toLowerCase(),itemWeight);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemDTO getItemByName(String itemName) {
        try {
            return itemRepository.getAllItems().stream().filter(i -> i.itemName().equalsIgnoreCase(itemName)).findFirst().orElse(null);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ItemDTO> getAllItems() throws SQLException {
        try {
            return itemRepository.getAllItems();
        }
        catch (SQLException e) {
            e.printStackTrace();
            // create new list
            return new ArrayList<>();
        }
    }

    public String getAllItemsString() throws SQLException {
        List<ItemDTO> allItems = getAllItems();
        if (allItems.isEmpty()) return "No items available.";

        StringBuilder sb = new StringBuilder("All Items:\n");
        for (ItemDTO i : allItems) {
            sb.append(i.itemName()).append(" (Weight: ").append(i.itemWeight()).append(")\n----------------------\n");
        }
        return sb.toString();
    }



    public void removeItem(String itemName) {
        try {
            itemRepository.delete(getItemByName(itemName));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean doesItemExist(String itemName) {
       return getItemByName(itemName) != null;
    }
}