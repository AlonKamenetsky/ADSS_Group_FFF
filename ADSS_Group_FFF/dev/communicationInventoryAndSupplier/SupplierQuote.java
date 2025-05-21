package communicationInventoryAndSupplier;

public class SupplierQuote {
    private final String supplierId;
    private final String productId;
    private final double pricePerUnit;
    private final int deliveryDays;
    private final int minimumOrderQuantity;

    public SupplierQuote(String supplierId, String productId, double pricePerUnit, int deliveryDays, int minimumOrderQuantity) {
        this.supplierId = supplierId;
        this.productId = productId;
        this.pricePerUnit = pricePerUnit;
        this.deliveryDays = deliveryDays;
        this.minimumOrderQuantity = minimumOrderQuantity;
    }

    public String getSupplierId() { return supplierId; }
    public String getProductId() { return productId; }
    public double getPricePerUnit() { return pricePerUnit; }
    public int getDeliveryDays() { return deliveryDays; }
    public int getMinimumOrderQuantity() { return minimumOrderQuantity; }
}
