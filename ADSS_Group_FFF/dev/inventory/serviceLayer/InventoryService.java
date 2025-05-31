package inventory.serviceLayer;

import IntegrationInventoryAndSupplier.*;
import inventory.domainLayer.*;

import java.util.*;

public class InventoryService implements InventoryInterface {
    private Map<Integer, InventoryProduct> products;
    private Map<String, Category> categories;
    private Map<Integer, Discount> productDiscounts;
    private Map<String, Discount> categoryDiscounts;
    private List<InventoryReport> reports;
    private static InventoryService instance = null;

    private SupplierInterface supplierInterface;

    private InventoryService() {
        this.products = new HashMap<>();
        this.categories = new HashMap<>();
        this.productDiscounts = new HashMap<>();
        this.categoryDiscounts = new HashMap<>();
        this.reports = new ArrayList<>();
        supplierInterface = supplierInterface.getInstance();
    }

    public static InventoryService getInstance() {
        if (instance == null)
            instance = new InventoryService();

        return instance;
    }


    public void setSupplierService(SupplierInterface supplierInterface) {
        this.supplierInterface = supplierInterface;
    }

    public int orderAmountWhenLowStock(InventoryProduct i) {
        return i.getMinThreshold() * 2;
    }

    public void checkAndReorderLowStockItems() {
        if (supplierInterface == null) {
            System.err.println("SupplierService not set.");
            return;
        }

        for (InventoryProduct product : getLowStockItems()) {
            supplierInterface.placeUrgentOrderSingleProduct(product.getId(), orderAmountWhenLowStock(product));
        }
    }



    //add InventoryService interface and make it have a function updateItemQuantity(String itemId, int shelfDelta, int backroomDelta)


    public void addItem(InventoryProduct item) {
        if (products.containsKey(item.getId())) {
            throw new IllegalArgumentException("Item ID already exists: " + item.getId());
        }
        products.put(item.getId(), item);
    }

    public List<InventoryProduct> getLowStockItems() {
        List<InventoryProduct> lowStock = new ArrayList<>();
        for (InventoryProduct item : products.values()) {
            int totalQty = item.getShelfQuantity() + item.getBackroomQuantity();
            if (totalQty < item.getMinThreshold()) {
                lowStock.add(item);
            }
        }
        return lowStock;
    }

    public Discount getDiscountForItem(InventoryProduct item) {
        Date now = new Date();
        // First try item-specific discount
        Discount discount = productDiscounts.get(item.getId());
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

    public Collection<InventoryProduct> getAllItems() {
        return products.values();
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
        List<InventoryProduct> filtered = new ArrayList<>();
        for (InventoryProduct item : products.values()) {
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


    public boolean takInSupplierDelivery(String itemId, int backroomDelta) {
        updateItemQuantity(itemId, 0, backroomDelta);
        return true; //if we want we can add logic to make sure order was accepteed
    }

    //method for both adding "buying" and subtracting "selling" Stock
    public void updateItemQuantity(String itemId, int shelfDelta, int backroomDelta) {
        InventoryProduct item = products.get(itemId);
        if (item != null) {
            item.setShelfQuantity(item.getShelfQuantity() + shelfDelta);
            item.setBackroomQuantity(item.getBackroomQuantity() + backroomDelta);
        }
        if (shelfDelta < 0 || backroomDelta < 0) {
            checkAndReorderLowStockItems();
        }
    }

    public void addDiscount(Discount discount) {
        if (discount.getAppliesToItem() != null) {
            productDiscounts.put(discount.getAppliesToItem().getId(), discount);
        } else if (discount.getAppliesToCategory() != null) {
            categoryDiscounts.put(discount.getAppliesToCategory().getName(), discount);
        } else {
            throw new IllegalArgumentException("Discount must apply to an item or a category");
        }
    }

    public List<InventoryReport> getAllReports() {
        return reports;
    }


    @Override
    public boolean acceptDelivery(int itemId, int quantity) {
        return false;
    }
}


