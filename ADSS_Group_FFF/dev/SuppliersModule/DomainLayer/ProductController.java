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
    public void UpdateProduct(int productID, Product newProduct){
        newProduct.setProductId(productID);
        this.productsArrayList.set(productID, newProduct);
    }
    public void DeleteProduct(int productID) {
        this.productsArrayList.removeIf(product -> product.productId == productID);
    }
    public ArrayList<Product> GetAllProducts(){
        return this.productsArrayList;
    }
    public Product GetProduct(int productID) {return this.productsArrayList.get(productID);}
}
