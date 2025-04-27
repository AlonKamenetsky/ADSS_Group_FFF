package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.ProductCategory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ProductController {
    ArrayList<Product> productsArrayList; // TEMP DATA STRUCTURE
    int numberOfProducts; // ID Giver

    public ProductController() {
        this.numberOfProducts = 0;
        this.productsArrayList = new ArrayList<>();
        //this.readProductsFromCSVFile();
    }

    public void ReadProductsFromCSVFile() {
        InputStream in = ProductController.class.getResourceAsStream("/products_data.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                    if (parts[i].startsWith("\"") && parts[i].endsWith("\"")) {
                        parts[i] = parts[i].substring(1, parts[i].length() - 1);
                    }
                }
                String productName = parts[0];
                String productCompanyName = parts[1];
                String categoryStr = parts[2].toUpperCase();
                ProductCategory productCategory = ProductCategory.valueOf(categoryStr);

                this.registerNewProduct(productName, productCompanyName, productCategory);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int registerNewProduct(String productName, String productCompanyName, ProductCategory productCategory) {
        Product product = new Product(this.numberOfProducts++, productName, productCompanyName, productCategory);
        this.productsArrayList.add(product);
        return product.getProductId();
    }

    public boolean updateProduct(int productID, String productName, String productCompanyName) {
        for (Product product : this.productsArrayList) {
            if (product.getProductId() == productID) {
                product.setProductName(productName);
                product.setProductCompanyName(productCompanyName);
                return true;
            }
        }

        return false;
    }

    public boolean deleteProduct(int productID) {
        return this.productsArrayList.removeIf(product -> product.productId == productID);
    }

    public String[] getAllProductsAsString() {
        String[] productsAsString = new String[this.productsArrayList.size()];
        for (int i = 0; i < this.productsArrayList.size(); i++)
            productsAsString[i] = this.productsArrayList.get(i).toString();

        return productsAsString;
    }

    public String getProductAsString(int productID) {
        for (Product product : this.productsArrayList)
            if (product.getProductId() == productID)
                return product.toString();

        return null;
    }

    public ProductCategory getProductCategory(int productID) {
        for (Product product : this.productsArrayList)
            if (product.getProductId() == productID)
                return product.getProductCategory();

        return null;
    }
}
