package SuppliersModule.DomainLayer;

public class SupplyContractProductData {
    double productPrice;
    int quantityForDiscount;
    double discountPercentage;
    Product product;

    public SupplyContractProductData(double productPrice, int quantityForDiscount, double discountPercentage, Product product) {
        this.productPrice = productPrice;
        this.quantityForDiscount = quantityForDiscount;
        this.discountPercentage = discountPercentage;
        this.product = product;
    }
}
