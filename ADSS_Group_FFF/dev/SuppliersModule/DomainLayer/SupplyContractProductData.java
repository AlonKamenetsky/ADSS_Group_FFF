package SuppliersModule.DomainLayer;

public class SupplyContractProductData {
    int productID;
    double productPrice;
    int quantityForDiscount;
    double discountPercentage;

    public SupplyContractProductData(int productID, double productPrice, int quantityForDiscount, double discountPercentage) {
        this.productID = productID;
        this.productPrice = productPrice;
        this.quantityForDiscount = quantityForDiscount;
        this.discountPercentage = discountPercentage;
    }

    public String toString() {
        return String.format("productID: %d\tPrice: %.2f\tQuantity For Discount: %d\tDiscount Percentage: %.2f",productID, productPrice, quantityForDiscount, discountPercentage);
    }
}
