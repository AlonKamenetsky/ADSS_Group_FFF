package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.*;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.PaymentMethod;
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

    int numberOfSuppliers = -1;
    int numberOfProducts = -1;

    public ServiceController() {
        supplierService = new SupplierService();
        productService = new ProductService();
    }

    // --------------------------- PRODUCT FUNCTIONS ---------------------------

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
    public void UpdateProduct(int productID, Product newProduct) {
        this.productService.UpdateProduct(productID, newProduct);
    }
    public void DeleteProduct(int productID){
        this.productService.DeleteProduct(productID);
    }

    public ArrayList<Product> GetAllProducts(){
        return this.productService.GetAllProducts();
    }

    public Product GetProduct(int productID) {
        return this.productService.GetProduct(productID);
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public void RegisterNewSupplier(String supplierName, ProductCategory productCategory ,SupplyMethod supplyMethod, DeliveringMethod deliveringMethod, SupplyContract supplyContract, PaymentInfo paymentInfo, ContactInfo supplierContactInfo){
        this.numberOfSuppliers++;
        Supplier supplier = null;
        if (supplyMethod == SupplyMethod.ON_DEMAND)
            supplier = new OnDemandSupplier(this.numberOfSuppliers, supplierName, productCategory, deliveringMethod, supplyContract, supplierContactInfo, paymentInfo);
        else if (supplyMethod == SupplyMethod.SCHEDULED)
            supplier = new ScheduledSupplier(this.numberOfSuppliers, supplierName, productCategory, deliveringMethod, supplyContract, supplierContactInfo, paymentInfo);

        this.supplierService.RegisterNewSupplier(supplier);

    }
    public void UpdateSupplier(int supplierID, String supplierName, PaymentInfo paymentInfo , DeliveringMethod deliveringMethod, ContactInfo contactInfo) {
        this.supplierService.UpdateSupplier(supplierID, supplierName, paymentInfo, deliveringMethod, contactInfo);
    }
    public void DeleteSupplier(int supplierID){
        supplierService.DeleteSupplier(supplierID);
    }

    public ArrayList<Supplier> GetAllSuppliers() {
        return this.supplierService.GetAllSuppliers();
    }

    public Supplier GetSupplier(int supplierID) {
        return supplierService.GetSupplier(supplierID);
    }


//    }
//    public SupplyContract findSupplyContract(int contractID){
//
//    }
//    public ContactInfo findContact(int contractID){
//
//    }
//
//    public Supplier findSupplier(int supplierID){
//
//    }
//    public ArrayList<Supplier> getAllSuppliers() {
//
//    }


}
