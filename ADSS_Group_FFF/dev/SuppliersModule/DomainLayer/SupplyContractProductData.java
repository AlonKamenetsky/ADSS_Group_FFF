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
    public int getProductID() {
        return productID;
    }
    public void setProductID(int productID) {
        this.productID = productID;
    }
    public double getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    public int getQuantityForDiscount() {
        return quantityForDiscount;
    }
    public void setQuantityForDiscount(int quantityForDiscount) {
        this.quantityForDiscount = quantityForDiscount;
    }
    public double getDiscountPercentage() {
        return discountPercentage;
    }
    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

}
