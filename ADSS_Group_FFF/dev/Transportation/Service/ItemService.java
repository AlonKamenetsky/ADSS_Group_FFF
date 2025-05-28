package Transportation.Service;

import Transportation.DTO.ItemDTO;
import Transportation.Domain.ItemManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemService {
    private final ItemManager itemManager;

    public ItemService(ItemManager itemManager1) {
        itemManager = itemManager1;
    }

    public void addItem(String itemName, float weight) {
        if (itemName == null || weight < 0) {
            return;
        }
        try {
            itemManager.addItem(itemName, weight);
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public void deleteItem(String itemName) {
        if (itemName == null) {
            return;
        }
        try {
            ItemDTO itemDTO = itemManager.getItemByName(itemName);
            if (itemDTO != null) {
                itemManager.removeItem(itemDTO.itemId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public List<ItemDTO> viewAllItems() {
        try {
            return itemManager.getAllItems();
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }


    public boolean doesItemExist(String itemName) {
        if (itemName == null) {
            return false;
        }
        try {
            ItemDTO itemDTO = itemManager.getItemByName(itemName);
            return itemManager.doesItemExist(itemDTO.itemId());
        } catch (SQLException e) {
            throw new RuntimeException("Database access error");
        }
    }
//
//    public List<ItemDTO> getAllItemDTOs() {
//        return itemManager.getAllItems()
//                .stream()
//                .map(ItemDTO::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    public ItemDTO getItemDTOByName(String itemName) {
//        Item item = itemManager.getItemByName(itemName);
//        return item != null ? ItemDTO.fromEntity(item) : null;
//    }

}