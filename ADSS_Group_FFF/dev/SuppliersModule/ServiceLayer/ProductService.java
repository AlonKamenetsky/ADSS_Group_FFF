package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.ProductController;


public class ProductService {
    ProductController productController;

    public ProductService() {
        this.productController = new ProductController();
    }

    public int RegisterNewProduct(String productName, String productCompanyName, ProductCategory productCategory) {
        return this.productController.RegisterNewProduct(productName, productCompanyName, productCategory);
    }

    public boolean UpdateProduct(int productID, String productName, String productCompanyName, ProductCategory productCategory) {
        return this.productController.UpdateProduct(productID, productName, productCompanyName, productCategory);
    }

    public boolean DeleteProduct(int productID) {
        return this.productController.DeleteProduct(productID);
    }

    public String[] GetProductsAsString() {
        return this.productController.GetAllProductsAsString();
    }

    public String GetProductAsString(int productID) {
        return this.productController.GetProductAsString(productID);
    }

    public ProductCategory GetProductCategory(int productID) {
        return this.productController.GetProductCategory(productID);
    }
}

