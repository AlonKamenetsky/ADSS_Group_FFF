package inventory;

import java.util.*;

public class InventoryService {
    private Map<String, InventoryItem> items;
    private Map<String, Category> categories;
    private Map<String, Discount> itemDiscounts;
    private Map<String, Discount> categoryDiscounts;
    private List<InventoryReport> reports;

    public InventoryService() {
        this.items = new HashMap<>();
        this.categories = new HashMap<>();
        this.itemDiscounts = new HashMap<>();
        this.categoryDiscounts = new HashMap<>();
        this.reports = new ArrayList<>();
    }

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

    public void updateQuantities(String itemId, int shelfDelta, int backroomDelta) {
        InventoryItem item = items.get(itemId);
        if (item != null) {
            item.setShelfQuantity(item.getShelfQuantity() + shelfDelta);
            item.setBackroomQuantity(item.getBackroomQuantity() + backroomDelta);
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
}