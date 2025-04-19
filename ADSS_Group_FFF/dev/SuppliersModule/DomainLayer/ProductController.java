package SuppliersModule.DomainLayer;

import java.util.ArrayList;

public class ProductController {
    ArrayList<Product> productsArrayList; // TEMP DATA STRUCTURE

    // Functions here
    public ProductController() {
        this.productsArrayList = new ArrayList<Product>();
    }
    public void RegisterNewProduct(Product product){
        this.productsArrayList.add(product);
    }
    public void DeleteProduct(int productID) {
        this.productsArrayList.removeIf(product -> product.productId == productID);
    }
    public ArrayList<Product> GetAllProducts(){
        return this.productsArrayList;
    }
}
