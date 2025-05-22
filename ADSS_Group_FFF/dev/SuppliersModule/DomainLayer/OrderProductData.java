package SuppliersModule.DomainLayer;

public class OrderProductData {
    private int orderID;
    private int productID;
    private int productQuantity;
    private double productPrice;

    public OrderProductData(int orderID, int productID, int productQuantity, double productPrice) {
        this.orderID = orderID;
        this.productID = productID;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
    }
    public OrderProductData(int productID, int productQuantity, double productPrice) {
        this.orderID = -1;
        this.productID = productID;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
    }
    public int getOrderID() {
        return orderID;
    }
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    public int getProductID() {
        return productID;
    }
    public void setProductID(int productID) {
        this.productID = productID;
    }
    public int getProductQuantity() {
        return productQuantity;
    }
    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
    public double getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }
    public double getTotalPrice() {
        return this.productPrice * this.productQuantity;
    }
    public String toString() {
        return "ProductID: " + this.productID + " ProductQuantity: " + this.productQuantity + " ProductPrice: " + this.productPrice;
    }
}
