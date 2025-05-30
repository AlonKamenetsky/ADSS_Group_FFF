package Transportation.Domain;

import java.util.HashMap;
import java.util.List;

import Transportation.DTO.ItemsListDTO;
import Transportation.Domain.Repositories.ItemRepository;
import Transportation.DTO.ItemDTO;
import Transportation.Domain.Repositories.ItemRepositoryImpli;

import java.sql.SQLException;

public class ItemManager {
    private final ItemRepository itemRepository;

    public ItemManager() {
        this.itemRepository = new ItemRepositoryImpli();
    }

    public void addItem(String itemName, float itemWeight) throws SQLException {
        itemRepository.addItem(itemName, itemWeight);
    }

    public Integer getItemIdByName(int itemId) throws SQLException {
        return itemRepository.findItem(itemId)
                .map(ItemDTO::itemId)
                .orElse(null);
    }


    public ItemDTO getItemByName(String itemName) throws SQLException {
        return itemRepository.getAllItems().stream().filter(i -> i.itemName().equalsIgnoreCase(itemName)).findFirst().orElse(null);
    }

    public ItemDTO getItemById(int itemId) throws SQLException {
        return itemRepository.findItem(itemId).orElse(null);
    }


    public List<ItemDTO> getAllItems() throws SQLException {
        return itemRepository.getAllItems();
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

    public int makeList(HashMap<String, Integer> itemsInList) throws SQLException {
        return itemRepository.makeList(itemsInList);
    }

    public float findWeightList(int itemsListId) throws SQLException {
        return itemRepository.findWeightList(itemsListId);
    }

    public void removeItem(int itemId) {
        try {
            itemRepository.delete(itemId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ItemsListDTO getItemsList(int itemsListId) throws SQLException {
        return itemRepository.getItemsList(itemsListId);
    }

    public boolean doesItemExist(int itemId) throws SQLException {
        return getItemById(itemId) != null;
    }
}