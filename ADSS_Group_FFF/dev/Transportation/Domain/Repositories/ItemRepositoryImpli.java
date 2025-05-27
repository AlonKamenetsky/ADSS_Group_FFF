package Transportation.Domain.Repositories;

import Transportation.DTO.ItemDTO;
import Transportation.DataAccess.ItemDAO;
import Transportation.DataAccess.SqliteItemDAO;
import Transportation.Domain.Item;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemRepositoryImpli implements ItemRepository {

    private final ItemDAO itemDAO;
    private ArrayList<Item> tempItemsList;

    public ItemRepositoryImpli() {
        this.itemDAO = new SqliteItemDAO();
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
    public void delete(int itemId) throws SQLException {
        itemDAO.delete(itemId);
    }
}