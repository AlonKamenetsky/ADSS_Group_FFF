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
        return("----------------------------------\n"+toRowString());
    }

        public String toRowString() {
            return String.format(
                    "%-10s | %-15s | %-17s | %-5d | %-8d | %-5d | %-8s | %-10s",
                    id,
                    name,
                    manufacturer,
                    shelfQuantity,
                    backroomQuantity,
                    minThreshold,
                    status,
                    category != null ? category.getName() : "None"
            );
        }

        public static String getTableHeader() {
            return String.format(
                    "%-10s | %-15s | %-17s | %-5s | %-8s | %-5s | %-8s | %-10s",
                    "ID", "Name", "Manufacturer", "Shelf", "Backroom", "Min", "Status", "Category"
            );
        }
    }

