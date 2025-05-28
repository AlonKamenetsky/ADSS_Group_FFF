package Transportation.Service;
import Transportation.DTO.ItemDTO;
import Transportation.Domain.Item;
import Transportation.Domain.ItemManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        ItemDTO itemDTO = itemManager.getItemByName(itemName);
        if (itemDTO != null) {
            itemManager.removeItem(itemDTO.itemId());
        }
    }

//    public String viewAllItems() {
//        return itemManager.getAllItemsString();
//    }

    public List<ItemDTO> viewAllItems() {
        try {
            return itemManager.getAllItems();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public boolean doesItemExist(String itemName) {
        if (itemName == null) {
            return false;
        }
        else {
            ItemDTO itemDTO = itemManager.getItemByName(itemName);
            return itemManager.doesItemExist(itemDTO.itemId());
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