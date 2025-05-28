package Transportation.Domain.Repositories;

import Transportation.DTO.ItemDTO;
import Transportation.DTO.ItemsListDTO;
import Transportation.DataAccess.ItemDAO;
import Transportation.DataAccess.ItemsListDAO;
import Transportation.DataAccess.SqliteItemDAO;
import Transportation.DataAccess.SqliteItemsListDAO;
import Transportation.Domain.Item;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ItemRepositoryImpli implements ItemRepository {

    private final ItemDAO itemDAO;
    private final ItemsListDAO listDAO;
    private ArrayList<Item> tempItemsList;

    public ItemRepositoryImpli() {
        this.itemDAO = new SqliteItemDAO();
        this.listDAO = new SqliteItemsListDAO();
        }

    @Override
    public ItemDTO addItem(String name,float weight) throws SQLException {
        ItemDTO insertedItemDTO = itemDAO.insert(new ItemDTO(null,name,weight));
        tempItemsList.add(new Item(insertedItemDTO.itemId(),name, weight));
        return insertedItemDTO;
    }

    @Override
    public List<ItemDTO> getAllItems() throws SQLException {
        ArrayList<ItemDTO> returnedList = new ArrayList<>();
        if (tempItemsList == null) {
            returnedList = (ArrayList<ItemDTO>) itemDAO.findAll();
        }
        else {
            for (Item i : tempItemsList) {
                returnedList.add(new ItemDTO(i.getItemId(), i.getItemName(), i.getWeight()));
            }
        }
        return returnedList;
    }

    @Override
    public Optional<ItemDTO> findItem(int itemId) throws SQLException {
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
        return listDAO.findWeight(itemsListId);
    }

    @Override
    public ItemsListDTO getItemsList(int itemsListId) throws SQLException {
        return new ItemsListDTO(itemsListId, listDAO.getItemsInList(itemsListId));
    }

    @Override
    public void delete(int itemId) throws SQLException {
        itemDAO.delete(itemId);
    }
}