package inventory;

import communicationInventoryAndSupplier.*;

import java.time.DayOfWeek;
import java.util.*;

public class InventoryController {
    private Map<String, InventoryItem> items;
    private Map<String, Category> categories;
    private Map<String, Discount> itemDiscounts;
    private Map<String, Discount> categoryDiscounts;
    private List<InventoryReport> reports;
    private List<PeriodicOrder> periodicOrders;
    private SimulationClock simulationClock;

    private SupplierService supplierService;

    public InventoryController() {
        this.items = new HashMap<>();
        this.categories = new HashMap<>();
        this.itemDiscounts = new HashMap<>();
        this.categoryDiscounts = new HashMap<>();
        this.reports = new ArrayList<>();
        this.simulationClock = new SimulationClock();
        periodicOrders = new ArrayList<>();
    }


    public void setSupplierService(SupplierService supplierService) {
        this.supplierService = supplierService;
    }


    public void checkAndReorderLowStockItems() {
        if (supplierService == null) {
            System.err.println("SupplierService not set.");
            return;
        }

        for (InventoryItem item : getLowStockItems()) {
            List<SupplierQuote> quotes = supplierService.getQuotesSingleProduct(item.getId());
            if (quotes.isEmpty()) {
                System.out.println("No suppliers found for item " + item.getId());
                continue;
            }

            SupplierQuote best = selectBestQuote(quotes, item);
            int reorderAmount = Math.max(best.getMinimumOrderQuantity(), item.getMinThreshold() * 2); // strategy
            SupplierOrder order = new SupplierOrder(best.getSupplierId(), item.getId(), reorderAmount);
            OrderConfirmation confirmation = supplierService.placeOrderSingleProduct(order);

            if (confirmation.isSuccess()) {
                System.out.println("Auto-order placed for item " + item.getId());
            } else {
                System.out.println("Order failed for item " + item.getId());
            }
        }
    }

    private SupplierQuote selectBestQuote(List<SupplierQuote> quotes, InventoryItem item) {
        // naive best price selector (can add tie-breakers on delivery days etc.)
        return quotes.stream()
                .filter(q -> q.getMinimumOrderQuantity() <= item.getMinThreshold() * 3)
                .min(Comparator.comparingDouble(SupplierQuote::getPricePerUnit))
                .orElse(quotes.get(0));
    }

    // ass2


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


    //method for both adding "buying" and subtracting "selling" Stock
    public void updateItemQuantity(String itemId, int shelfDelta, int backroomDelta) {
        InventoryItem item = items.get(itemId);
        if (item != null) {
            item.setShelfQuantity(item.getShelfQuantity() + shelfDelta);
            item.setBackroomQuantity(item.getBackroomQuantity() + backroomDelta);
        }
        checkAndReorderLowStockItems();
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
        PeriodicOrders.add(p1);
        //DB Access
    }

    public void advanceDay() {
        simulationClock.advanceDay();
        for PeriodicOrder p1 : PeriodicOrders
        {
            if (p1.getOrderDay() == simulationClock.getCurrentDay())
                p1.setSupplier;
                p1.PlaceOrder;
        }
    }

    public List<PeriodicOrder> getAllPeriodicOrders() {
        return periodicOrders;
    }

    public boolean removePeriodicOrderById(int id) {
        return periodicOrders.removeIf(order -> order.getOrderID() == id);
    }


}