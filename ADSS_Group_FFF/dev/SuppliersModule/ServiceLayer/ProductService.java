package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.Product;
import SuppliersModule.DomainLayer.ProductController;

import java.util.ArrayList;


public class ProductService {
    ProductController productController;

    public ProductService() {
        this.productController = new ProductController();
    }

    public void RegisterNewProduct(Product product){
        this.productController.RegisterNewProduct(product);
    }
    public void UpdateProduct(int productID, Product newProduct){
        this.productController.UpdateProduct(productID, newProduct);
    }
    public void DeleteProduct(int productID){
        this.productController.DeleteProduct(productID);
    }

    public ArrayList<Product> GetAllProducts() {
        return this.productController.GetAllProducts();
    }
    public Product GetProduct(int productID){
        return this.productController.GetProduct(productID);
    }
}

