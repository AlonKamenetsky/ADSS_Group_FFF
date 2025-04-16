package inventory;

import java.util.Date;

public class Discount {
    private String id;
    private double percent;
    private Date startDate;
    private Date endDate;
    private Category appliesToCategory;       // nullable
    private InventoryItem appliesToItem;      // nullable

    public Discount(String id, double percent, Date startDate, Date endDate,
                    Category appliesToCategory, InventoryItem appliesToItem) {
        this.id = id;
        this.percent = percent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.appliesToCategory = appliesToCategory;
        this.appliesToItem = appliesToItem;
    }

    public String getId() {
        return id;
    }

    public double getPercent() {
        return percent;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Category getAppliesToCategory() {
        return appliesToCategory;
    }

    public InventoryItem getAppliesToItem() {
        return appliesToItem;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "id='" + id + '\'' +
                ", percent=" + percent +
                ", start=" + startDate +
                ", end=" + endDate +
                ", category=" + (appliesToCategory != null ? appliesToCategory.getName() : "none") +
                ", item=" + (appliesToItem != null ? appliesToItem.getName() : "none") +
                '}';
    }
}
