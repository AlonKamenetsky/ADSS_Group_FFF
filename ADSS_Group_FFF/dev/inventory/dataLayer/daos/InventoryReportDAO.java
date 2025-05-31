package inventory.dataLayer.daos;

import inventory.domainLayer.InventoryReport;
import java.util.List;

public interface InventoryReportDAO {
    void save(InventoryReport report);
    InventoryReport findById(String id);
    List<InventoryReport> findAll();
    void delete(String id);
    void update(InventoryReport report);
}

