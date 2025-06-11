package SuppliersModule.ServiceLayer;

import IntegrationInventoryAndSupplier.MutualProduct;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.ProductController;

import java.util.List;


public class ProductService {
    ProductController productController;

    public ProductService() {
        this.productController = new ProductController();
    }

    public int registerNewProduct(String productName, String productCompanyName, ProductCategory productCategory) {
        return this.productController.registerNewProduct(productName, productCompanyName, productCategory);
    }

    public boolean updateProduct(int productID, String productName, String productCompanyName) {
        return this.productController.updateProduct(productID, productName, productCompanyName);
    }

    public boolean deleteProduct(int productID) {
        return this.productController.deleteProduct(productID);
    }

    public String[] getProductsAsString() {
        return this.productController.getAllProductsAsString();
    }

    public String getProductAsString(int productID) {
        return this.productController.getProductAsString(productID);
    }

    public ProductCategory getProductCategory(int productID) {
        return this.productController.getProductCategory(productID);
    }

    public List<MutualProduct> getAllProductAsMutual() {
        return this.productController.getAllProductAsMutual();
    }
}

