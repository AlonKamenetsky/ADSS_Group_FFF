package Transportation.Domain.Repositories;

import Transportation.DTO.ItemDTO;
import Transportation.DataAccess.ItemDAO;
import Transportation.DataAccess.SqliteItemDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ItemRepositoryImpli implements ItemRepository {

    private final ItemDAO itemDAO;

    public ItemRepositoryImpli() {
        this.itemDAO = new SqliteItemDAO();
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
    public void delete(int itemId) throws SQLException {
        itemDAO.delete(itemId);
    }
}