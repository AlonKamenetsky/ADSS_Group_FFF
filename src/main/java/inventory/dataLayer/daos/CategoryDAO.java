package inventory.dataLayer.daos;

import inventory.domainLayer.Category;
import java.util.List;

public interface CategoryDAO {
    void save(Category category);
    Category findByName(String name);
    List<Category> findAll();
    void delete(String name);
    void update(Category category);
}
