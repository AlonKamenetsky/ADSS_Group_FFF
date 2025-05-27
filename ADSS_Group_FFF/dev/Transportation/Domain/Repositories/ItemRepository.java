package Transportation.Domain.Repositories;

import Transportation.DTO.ItemDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    ItemDTO addItem(String name, float weight)  throws SQLException;
    List<ItemDTO> getAllItems() throws SQLException;
    Optional<ItemDTO> findItem(int id) throws SQLException;
    void delete(ItemDTO itemDTO) throws SQLException;
}
