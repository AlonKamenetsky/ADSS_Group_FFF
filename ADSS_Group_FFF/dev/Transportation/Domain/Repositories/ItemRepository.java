package Transportation.Domain.Repositories;

import Transportation.DTO.ItemDTO;
import Transportation.DTO.ItemsListDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    ItemDTO addItem(String name, float weight)  throws SQLException;
    List<ItemDTO> getAllItems() throws SQLException;
    Optional<ItemDTO> findItem(int id) throws SQLException;
    int makeList(HashMap<String, Integer> itemsInList) throws SQLException;
    void delete(int itemId) throws SQLException;
    float findWeightList(int itemsListId) throws SQLException;
    ItemsListDTO getItemsList(int itemsListId) throws SQLException;
}