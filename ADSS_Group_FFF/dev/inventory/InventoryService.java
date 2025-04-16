package inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventoryService {
    private List<InventoryItem> items;
    private List<Category> categories;
    private List<Discount> discounts;
    private List<InventoryReport> reports;

    public InventoryService() {
        this.items = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.discounts = new ArrayList<>();
        this.reports = new ArrayList<>();
    }

    public void addItem(InventoryItem item) {
        items.add(item);
    }

    public List<InventoryItem> getLowStockItems() {
        List<InventoryItem> lowStock = new ArrayList<>();
        for (InventoryItem item : items) {
            int totalQty = item.getShelfQuantity() + item.getBackroomQuantity();
            if (totalQty < item.getMinThreshold()) {
                lowStock.add(item);
            }
        }
        return lowStock;
    }

    public Discount getDiscountForItem(InventoryItem item) {
        Date now = new Date();
        for (Discount discount : discounts) {
            boolean inDateRange = !now.before(discount.getStartDate()) && !now.after(discount.getEndDate());

            if (inDateRange) {
                if (discount.getAppliesToItem() != null && discount.getAppliesToItem().equals(item)) {
                    return discount;
                }
                if (discount.getAppliesToCategory() != null &&
                        discount.getAppliesToCategory().equals(item.getCategory())) {
                    return discount;
                }
            }
        }
        return null;
    }


    public List<InventoryItem> getAllItems() {
        return items;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public List<Category> getAllCategories() {
        return categories;
    }

    public InventoryReport generateReport(String reportId, List<Category> filterCategories) {
        List<InventoryItem> filtered = new ArrayList<>();
        for (InventoryItem item : items) {
            if (filterCategories == null || filterCategories.contains(item.getCategory())) {
                filtered.add(item);
            }
        }

        InventoryReport report = new InventoryReport(reportId, new Date(), filtered);
        reports.add(report);
        return report;
    }


}
