package inventory;

public class InventoryItem {
    private String id;
    private String name;
    private String manufacturer;
    private int shelfQuantity;
    private int backroomQuantity;
    private int minThreshold;
    private double purchasePrice;
    private double salePrice;
    private ItemStatus status;
    private Category category;

    // Full constructor
    public InventoryItem(String id, String name, String manufacturer, int shelfQuantity,
                         int backroomQuantity, int minThreshold, double purchasePrice,
                         double salePrice, ItemStatus status, Category category) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.shelfQuantity = shelfQuantity;
        this.backroomQuantity = backroomQuantity;
        this.minThreshold = minThreshold;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.status = status;
        this.category = category;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getShelfQuantity() {
        return shelfQuantity;
    }

    public int getBackroomQuantity() {
        return backroomQuantity;
    }

    public int getMinThreshold() {
        return minThreshold;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public Category getCategory() {
        return category;
    }

    // Setters
    public void setShelfQuantity(int shelfQuantity) {
        this.shelfQuantity = shelfQuantity;
    }

    public void setBackroomQuantity(int backroomQuantity) {
        this.backroomQuantity = backroomQuantity;
    }

    public void setMinThreshold(int minThreshold) {
        this.minThreshold = minThreshold;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", totalQuantity=" + (shelfQuantity + backroomQuantity) +
                ", status=" + status +
                ", category=" + (category != null ? category.getName() : "none") +
                '}';
    }
}
