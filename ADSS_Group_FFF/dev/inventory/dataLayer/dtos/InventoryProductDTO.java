package inventory.dataLayer.dtos;

public class InventoryProductDTO {
    private int id;
    private String name;
    private String manufacturer;
    private int shelfQuantity;
    private int backroomQuantity;
    private int minThreshold;
    private double purchasePrice;
    private double salePrice;
    private String status;
    private String categoryName;

    public InventoryProductDTO(int id, String name, String manufacturer, int shelfQuantity,
                               int backroomQuantity, int minThreshold, double purchasePrice,
                               double salePrice, String status, String categoryName) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.shelfQuantity = shelfQuantity;
        this.backroomQuantity = backroomQuantity;
        this.minThreshold = minThreshold;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.status = status;
        this.categoryName = categoryName;
    }

    public InventoryProductDTO() {
        // default no-arg constructor (needed for some serializers)
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getShelfQuantity() {
        return shelfQuantity;
    }

    public void setShelfQuantity(int shelfQuantity) {
        this.shelfQuantity = shelfQuantity;
    }

    public int getBackroomQuantity() {
        return backroomQuantity;
    }

    public void setBackroomQuantity(int backroomQuantity) {
        this.backroomQuantity = backroomQuantity;
    }

    public int getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(int minThreshold) {
        this.minThreshold = minThreshold;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
