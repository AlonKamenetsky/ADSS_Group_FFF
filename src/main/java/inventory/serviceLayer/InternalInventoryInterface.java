package inventory.serviceLayer;

// this is for Stav and Blanga's use - not for other's

import IntegrationInventoryAndSupplier.InventoryInterface;
import IntegrationInventoryAndSupplier.SupplierInterface;
import inventory.domainLayer.*;

import java.util.Collection;
import java.util.List;

public interface InternalInventoryInterface extends InventoryInterface {

    void setSupplierService(SupplierInterface supplierInterface);

//    int orderAmountWhenLowStock(InventoryProduct i);
//
//    void checkAndReorderLowStockItems();
//
//    void addItem(InventoryProduct item);
//
//    List<InventoryProduct> getLowStockItems();
//
//    Discount getDiscountForItem(InventoryProduct item);
//
//    Collection<InventoryProduct> getAllItems();
//
//    void addCategory(Category category);
//
//    Collection<Category> getAllCategories();
//
//    InventoryReport generateReport(String reportId, List<Category> filterCategories, ItemStatus statusFilter);
//
//    boolean takInSupplierDelivery(String itemId, int backroomDelta);
//
//    void updateItemQuantity(String itemId, int shelfDelta, int backroomDelta);
//
//    void addDiscount(Discount discount);
//
//    List<InventoryReport> getAllReports();


}
