package Transportation.Domain.Repositories;

import Transportation.DTO.ItemDTO;
import Transportation.DTO.ItemsListDTO;
import Transportation.DataAccess.ItemDAO;
import Transportation.DataAccess.ItemsListDAO;
import Transportation.DataAccess.SqliteItemDAO;
import Transportation.DataAccess.SqliteItemsListDAO;
import Transportation.Domain.Item;

import java.sql.SQLException;
import java.util.*;

public class ItemRepositoryImpli implements ItemRepository {

    private final ItemDAO itemDAO;
    private final ItemsListDAO listDAO;

    public ItemRepositoryImpli() {
        this.itemDAO = new SqliteItemDAO();
        this.listDAO = new SqliteItemsListDAO();
        }

    @Override
    public ItemDTO addItem(String name,float weight) throws SQLException {
        return itemDAO.insert(new ItemDTO(null,name,weight));
    }

    @Override
    public List<ItemDTO> getAllItems() throws SQLException {
        return itemDAO.findAll();
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
        return listDAO.findItemListByID(itemsListId);
    }




    @Override
    public void delete(int itemId) throws SQLException {
        itemDAO.delete(itemId);
    }

    //Helping methods
    private ItemDTO toDTO(Item item) {
        return new ItemDTO(item.getItemId(),item.getItemName(),item.getWeight());
    }
}