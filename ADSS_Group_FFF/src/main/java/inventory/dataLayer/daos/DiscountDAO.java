package inventory.dataLayer.daos;

import inventory.domainLayer.Discount;
import java.util.List;

public interface DiscountDAO {
    void save(Discount discount);
    Discount findById(String id);
    List<Discount> findAll();
    void delete(String id);
    void update(Discount discount);

    /**
     * Return the Discount that applies specifically to the given product ID,
     * or null if no product-level discount exists.
     */
    Discount findByItemId(int itemId);

    /**
     * Return the Discount that applies to the given category name,
     * or null if no category-level discount exists.
     */
    Discount findByCategoryName(String categoryName);
}
