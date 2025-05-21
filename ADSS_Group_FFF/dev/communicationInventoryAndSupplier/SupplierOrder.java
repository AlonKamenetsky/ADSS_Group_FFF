package communicationInventoryAndSupplier;

public class SupplierOrder {
    private String supplierId;
    private final String productId;
    private final int quantity;

    public SupplierOrder(String supplierId, String productId, int quantity) {
        this.supplierId = supplierId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void SetSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "SupplierOrder{" +
                "supplierId='" + supplierId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                '}';
    }

}
