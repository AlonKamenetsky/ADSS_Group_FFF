package IntegrationInventoryAndSupplier;

import SuppliersModule.DomainLayer.Enums.ProductCategory;

public class MutualProduct {
    int productId;
    String productName;
    String productCompanyName;
    ProductCategory productCategory;

    public MutualProduct(int productId, String productName, String productCompanyName, ProductCategory productCategory) {
        this.productId = productId;
        this.productName = productName;
        this.productCompanyName = productCompanyName;
        this.productCategory = productCategory;
    }

    public int getId() {
        return productId;
    }

    public String getName() {
        return productName;
    }

    public String getManufacturer() {
        return productCompanyName;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }
}
