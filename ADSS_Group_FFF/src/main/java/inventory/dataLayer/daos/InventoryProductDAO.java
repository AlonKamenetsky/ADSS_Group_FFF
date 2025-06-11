package inventory.dataLayer.daos;

import inventory.domainLayer.InventoryProduct;

import java.util.List;

public interface InventoryProductDAO {
    void save(InventoryProduct product);
    InventoryProduct findById(int id);
    List<InventoryProduct> findAll();
    void delete(int id);
    void update(InventoryProduct product);
}

