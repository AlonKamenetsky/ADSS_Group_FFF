package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Product;
import SuppliersModule.DomainLayer.ProductController;

import java.io.IOException;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;

public class ProductService {
    ProductController productController;

    public ProductService() {
        this.productController = new ProductController();
    }

    public void RegisterNewProduct(Product product){
        this.productController.RegisterNewProduct(product);
    }
    public void DeleteProduct(int productID){
        this.productController.DeleteProduct(productID);
    }
    public ArrayList<Product> GetAllProducts() {
        return this.productController.GetAllProducts();
    }
}

