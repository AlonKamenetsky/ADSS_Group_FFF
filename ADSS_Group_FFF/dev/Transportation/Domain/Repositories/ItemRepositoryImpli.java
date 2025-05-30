package Transportation.Domain.Repositories;

import Transportation.DTO.ItemDTO;
import Transportation.DTO.ItemsListDTO;
import Transportation.DTO.TruckDTO;
import Transportation.DataAccess.ItemDAO;
import Transportation.DataAccess.ItemsListDAO;
import Transportation.DataAccess.SqliteItemDAO;
import Transportation.DataAccess.SqliteItemsListDAO;
import Transportation.Domain.Item;
import Transportation.Domain.ItemsList;
import Transportation.Domain.Truck;

import java.sql.SQLException;
import java.util.*;

public class ItemRepositoryImpli implements ItemRepository {

    private final ItemDAO itemDAO;
    private final ItemsListDAO listDAO;
    private final ArrayList<Item> tempItemList;
    private final ArrayList<ItemsList> tempItemsList;

    public ItemRepositoryImpli() {
        this.itemDAO = new SqliteItemDAO();
        this.listDAO = new SqliteItemsListDAO();
        this.tempItemList = new ArrayList<>();
        this.tempItemsList = new ArrayList<>();
        }

    @Override
    public ItemDTO addItem(String name,float weight) throws SQLException {
        ItemDTO insertedItemDTO = itemDAO.insert(new ItemDTO(null,name,weight));
        tempItemList.add(new Item(insertedItemDTO.itemId(),name, weight));
        return insertedItemDTO;
    }

    @Override
    public List<ItemDTO> getAllItems() throws SQLException {
        if (!tempItemList.isEmpty()) {
            List<ItemDTO> returnedList = new ArrayList<>();
            for (Item currItem : tempItemList) {
                returnedList.add(toDTO(currItem));
            }
            return returnedList;
        } else {
            return itemDAO.findAll();
        }
    }

    @Override
    public Optional<ItemDTO> findItem(int itemId) throws SQLException {
        for (Item currItem : tempItemList) {
            if (currItem.getItemId() == itemId) {
                return Optional.of(toDTO(currItem));
            }
        }
        return itemDAO.findById(itemId);
    }

    @Override
    public int makeList(HashMap<String, Integer> itemsInList) throws SQLException {
        int listId = listDAO.createEmptyList();
        for (HashMap.Entry<String, Integer> entry : itemsInList.entrySet()) {
            Optional<ItemDTO> maybeItem = itemDAO.findByName(entry.getKey());
            if (maybeItem.isPresent()) {
                int currItemId = maybeItem.get().itemId();
                listDAO.addItemToList(listId, currItemId, entry.getValue());
            }
        }
        return listId;
    }

    public float findWeightList(int itemsListId) throws SQLException {
        for (ItemsList currItemsList : tempItemsList) {
            if (currItemsList.getListId() == itemsListId) {
                currItemsList.getListWeight();
            }
        }
        return listDAO.findWeight(itemsListId);
    }

    @Override
    public ItemsListDTO getItemsList(int itemsListId) throws SQLException {
        for (ItemsList currItemsList : tempItemsList) {
            if (currItemsList.getListId() == itemsListId) {
                Map<Integer, Integer> result = new HashMap<>();
                for (Map.Entry<Item, Integer> entry : currItemsList.getItemsMap().entrySet()) {
                    Item item = entry.getKey();
                    int quantity = entry.getValue();
                    result.put(item.getItemId(), quantity);
                }
                return new ItemsListDTO(itemsListId, result);
            }
        }
        return listDAO.findItemListByID(itemsListId);
    }




    @Override
    public void delete(int itemId) throws SQLException {
        for (Item i : tempItemList) {
            if (i.getItemId() == itemId) {
                tempItemList.remove(i);
            }
        }
        itemDAO.delete(itemId);
    }

    //Helping methods
    private ItemDTO toDTO(Item item) {
        return new ItemDTO(item.getItemId(),item.getItemName(),item.getWeight());
    }

    private Item findItemInList(int itemId) {
        for (Item currItem: tempItemList) {
            if (currItem.getItemId() == itemId) {
                return currItem;
            }
        }
        return null;
    }

    public void clearCacheI(){
        tempItemList.clear();
    }
    public void clearCacheIL() {
        tempItemsList.clear();
    }
}