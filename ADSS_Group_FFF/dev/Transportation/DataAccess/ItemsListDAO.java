package Transportation.DataAccess;

import java.sql.SQLException;
import java.util.HashMap;

public interface ItemsListDAO {
    int createEmptyList() throws SQLException;
    void addItemToList(int listId, int itemId, int quantity) throws SQLException;
    void removeItemFromList(int listId, int itemId) throws SQLException;
    float findWeight(int listId) throws SQLException;
    HashMap<Integer, Integer> getItemsInList(int listId) throws SQLException;
    void delete(int listId) throws SQLException;
}