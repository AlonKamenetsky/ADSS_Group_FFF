package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.*;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;
import SuppliersModule.PresentationLayer.CLI;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

public class ServiceController {
    SupplierService supplierService;
    ProductService productService;

    int numberOfSuppliers = 0;
    int numberOfProducts = 0;

    public ServiceController() {
        supplierService = new SupplierService();
        productService = new ProductService();
    }
    public void ReadProductsFromCSVFile(String filePath) {
        InputStream in = CLI.class.getResourceAsStream("/products_data.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                String[] parts = line.split(",");
                // Handle possible quotes around fields
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                    if (parts[i].startsWith("\"") && parts[i].endsWith("\"")) {
                        parts[i] = parts[i].substring(1, parts[i].length() - 1);
                    }
                }
                this.numberOfProducts++;
                String productName = parts[0];
                String productCompanyName = parts[1];
                String categoryStr = parts[2].toUpperCase();
                ProductCategory productCategory = ProductCategory.valueOf(categoryStr);

                Product product = new Product(this.numberOfProducts, productName, productCompanyName, productCategory);
                this.productService.RegisterNewProduct(product);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void RegisterNewProduct(String productName, String productCompanyName, ProductCategory productCategory) {
        this.numberOfProducts++;
        Product product = new Product(this.numberOfProducts, productName, productCompanyName, productCategory);
        this.productService.RegisterNewProduct(product);
    }
    public void DeleteProduct(int productID){
        this.productService.DeleteProduct(productID);
    }
    public ArrayList<Product> GetAllProducts(){
        return this.productService.GetAllProducts();
    }
    public void RegisterNewSupplier(String supplierName, SupplyMethod supplyMethod, DeliveringMethod deliveringMethod, SupplyContract supplyContract, PaymentInfo paymentInfo){
        this.numberOfSuppliers++;
    }
    public void DeleteSupplier(int supplierID){}



}
