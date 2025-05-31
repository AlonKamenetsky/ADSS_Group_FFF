package inventory.dataLayer.dtos;

import java.util.Date;

public class DiscountDTO {
    private String id;
    private double percent;
    private Date startDate;
    private Date endDate;
    private String categoryName; // Nullable
    private String itemName;     // Nullable

    public DiscountDTO() {
        // Default constructor
    }

    public DiscountDTO(String id, double percent, Date startDate, Date endDate,
                       String categoryName, String itemName) {
        this.id = id;
        this.percent = percent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryName = categoryName;
        this.itemName = itemName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
