package inventory;

import IntegrationInventoryAndSupplier.*;

import java.time.DayOfWeek;
import java.util.*;

public class InventoryService implements InventoryInterface {
    private Map<String, InventoryItem> items;
    private Map<String, Category> categories;
    private Map<String, Discount> itemDiscounts;
    private Map<String, Discount> categoryDiscounts;
    private List<InventoryReport> reports;
    private List<PeriodicOrder> periodicOrders;
    private SimulationClock simulationClock;

    private SupplierInterface supplierInterface;

    public InventoryService() {
        this.items = new HashMap<>();
        this.categories = new HashMap<>();
        this.itemDiscounts = new HashMap<>();
        this.categoryDiscounts = new HashMap<>();
        this.reports = new ArrayList<>();
        this.simulationClock = new SimulationClock();
        periodicOrders = new ArrayList<>();
        // supplierService.getInstance();
    }


    public void setSupplierService(SupplierInterface supplierInterface) {
        this.supplierInterface = supplierInterface;
    }

    public int lowstockStratergy(InventoryItem i){
        return i.getMinThreshold()*2;
    }

    public void checkAndReorderLowStockItems() {
        if (supplierInterface == null) {
            System.err.println("SupplierService not set.");
            return;
        }

        for (InventoryItem item : getLowStockItems()) {

            SupplierOrder order = new SupplierOrder(null, item.getId(), lowstockStratergy(item));
             supplierInterface.placeOrderSingleProduct(order);
        }
    }


    // ass2

    //add InventoryService interface and make it have a function updateItemQuantity(String itemId, int shelfDelta, int backroomDelta)


    public void addItem(InventoryItem item) {
        if (items.containsKey(item.getId())) {
            throw new IllegalArgumentException("Item ID already exists: " + item.getId());
        }
        items.put(item.getId(), item);
    }

    public List<InventoryItem> getLowStockItems() {
        List<InventoryItem> lowStock = new ArrayList<>();
        for (InventoryItem item : items.values()) {
            int totalQty = item.getShelfQuantity() + item.getBackroomQuantity();
            if (totalQty < item.getMinThreshold()) {
                lowStock.add(item);
            }
        }
        return lowStock;
    }

    public Discount getDiscountForItem(InventoryItem item) {
        Date now = new Date();
        // First try item-specific discount
        Discount discount = itemDiscounts.get(item.getId());
        if (discount != null && !now.before(discount.getStartDate()) && !now.after(discount.getEndDate())) {
            return discount;
        }
        // Then try category-wide discount
        if (item.getCategory() != null) {
            discount = categoryDiscounts.get(item.getCategory().getName());
            if (discount != null && !now.before(discount.getStartDate()) && !now.after(discount.getEndDate())) {
                return discount;
            }
        }
        return null;
    }

    public Collection<InventoryItem> getAllItems() {
        return items.values();
    }

    public void addCategory(Category category) {
        if (categories.containsKey(category.getName())) {
            throw new IllegalArgumentException("Category name already exists: " + category.getName());
        }
        categories.put(category.getName(), category);
    }

    public Collection<Category> getAllCategories() {
        return categories.values();
    }

    public InventoryReport generateReport(String reportId, List<Category> filterCategories, ItemStatus statusFilter) {
        List<InventoryItem> filtered = new ArrayList<>();
        for (InventoryItem item : items.values()) {
            boolean matchesCategory = filterCategories == null || filterCategories.contains(item.getCategory());
            boolean matchesStatus = statusFilter == null || item.getStatus() == statusFilter;
            if (matchesCategory && matchesStatus) {
                filtered.add(item);
            }
        }
        InventoryReport report = new InventoryReport(reportId, new Date(), filtered);
        reports.add(report);
        return report;
    }


    public boolean takInSupplierDelivery(String itemId,  int backroomDelta)
    {
        updateItemQuantity(itemId,0,backroomDelta);
        return true; //if we want we can add logic to make sure order was accepteed
    }

    //method for both adding "buying" and subtracting "selling" Stock
    public void updateItemQuantity(String itemId, int shelfDelta, int backroomDelta) {
        InventoryItem item = items.get(itemId);
        if (item != null) {
            item.setShelfQuantity(item.getShelfQuantity() + shelfDelta);
            item.setBackroomQuantity(item.getBackroomQuantity() + backroomDelta);
        }
        if(shelfDelta < 0 || backroomDelta <0)
        {
            checkAndReorderLowStockItems();
        }
    }

    public void addDiscount(Discount discount) {
        if (discount.getAppliesToItem() != null) {
            itemDiscounts.put(discount.getAppliesToItem().getId(), discount);
        } else if (discount.getAppliesToCategory() != null) {
            categoryDiscounts.put(discount.getAppliesToCategory().getName(), discount);
        } else {
            throw new IllegalArgumentException("Discount must apply to an item or a category");
        }
    }

    public List<InventoryReport> getAllReports() {
        return reports;
    }

    public void addPeriodicOrder(String productId, int quantity, DayOfWeek orderDay) {
        PeriodicOrder p1 = new PeriodicOrder(productId, quantity, orderDay);
        periodicOrders.add(p1);
        //DB Access
        supplierInterface.takePeriodicOrder(p1);
    }



    public List<PeriodicOrder> getAllPeriodicOrders() {
        return periodicOrders;
    }

    public boolean removePeriodicOrderById(int id) {
        return periodicOrders.removeIf(order -> order.getOrderID() == id);
    }


}



//
//
//
//=== Inventory Management Menu ===
//        1. View all inventory
//2. View low stock items
//3. View categories
//4. Update item quantities
//5. Mark item as DAMAGED or EXPIRED
//6. Generate inventory report
//7. Add a new item to inventory
//8. Exit
//9. Add Item (from available)
//
//Enter your choice:
