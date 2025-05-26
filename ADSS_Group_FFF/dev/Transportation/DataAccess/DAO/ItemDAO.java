package Transportation.DataAccess.DAO;

import Transportation.DTO.ItemDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ItemDAO {
    List<ItemDTO> findAll() throws SQLException;
    Optional<ItemDTO> findById(int id) throws SQLException;
    Optional<ItemDTO> findByName(String name) throws SQLException;
    ItemDTO insert(ItemDTO item) throws SQLException;
    void delete(ItemDTO item) throws SQLException;
}