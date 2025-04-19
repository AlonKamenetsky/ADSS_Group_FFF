package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.ProductCategory;

public class Product {
    int productId;
    String productName;
    String productCompanyName;
    ProductCategory productCategory;

    public Product(int productId, String productName, String productCompanyName, ProductCategory productCategory) {
        this.productId = productId;
        this.productName = productName;
        this.productCompanyName = productCompanyName;
        this.productCategory = productCategory;
    }

    public int getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductCompanyName() {
        return productCompanyName;
    }
    public void setProductCompanyName(String productCompanyName) {
        this.productCompanyName = productCompanyName;
    }
    public String toString() {
        return productId + "\t" + productName + "\t" + productCompanyName + "\t" + productCategory;
    }


}
