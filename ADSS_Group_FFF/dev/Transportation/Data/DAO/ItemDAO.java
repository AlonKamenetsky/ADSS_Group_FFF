package Transportation.Data.DAO;
import Transportation.Domain.Item;

import java.util.ArrayList;

public interface ItemDAO {
    ArrayList<Item> findAll();
    Item getByName(String name);
     void insert(Item item);
     void delete(Item item);
     boolean exists(String name);
     float getWeightByName(String name);

}
