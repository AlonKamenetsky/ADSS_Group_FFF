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
import java.util.HashMap;
import java.util.Map;

public class ServiceController {
    SupplierService supplierService;
    ProductService productService;

    int numberOfSuppliers = -1;
    int numberOfProducts = -1;
    int numberOfContracts = -1;

    public ServiceController() {
        supplierService = new SupplierService();
        productService = new ProductService();
    }

    // --------------------------- PRODUCT FUNCTIONS ---------------------------

    public void ReadProductsFromCSVFile() {
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

    public void DeleteProduct(int productID) {
        this.productService.DeleteProduct(productID);
    }

    public ArrayList<Product> GetAllProducts() {
        return this.productService.GetAllProducts();
    }

    public Product GetProduct(int productID) {
        return this.productService.GetProduct(productID);
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public void ReadSuppliersFromCSVFile() {
        InputStream in = CLI.class.getResourceAsStream("/suppliers_data.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                String[] parts = line.split(",");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                    if (parts[i].startsWith("\"") && parts[i].endsWith("\"")) {
                        parts[i] = parts[i].substring(1, parts[i].length() - 1);
                    }
                }

                String supplyMethodStr = parts[0].toUpperCase();
                SupplyMethod supplyMethod = SupplyMethod.valueOf(supplyMethodStr);

                String supplierName = parts[1];

                String categoryStr = parts[2].toUpperCase().replace(" ", "_");
                ProductCategory productCategory = ProductCategory.valueOf(categoryStr);

                String deliveryMethodStr = parts[3].toUpperCase();
                DeliveringMethod deliveringMethod = DeliveringMethod.valueOf(deliveryMethodStr);

                String phoneNumber = parts[4];
                String address = parts[5];
                String email = parts[6];
                String contactName = parts[7];

                ContactInfo supplierContactInfo = new ContactInfo(phoneNumber, address, email, contactName);

                String bankAccount = parts[8];
                String paymentMethodStr = parts[9].toUpperCase();
                PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodStr);

                PaymentInfo paymentInfo = new PaymentInfo(bankAccount, paymentMethod);

                this.numberOfSuppliers++;
                Supplier supplier = null;
                if (supplyMethod == SupplyMethod.ON_DEMAND)
                    supplier = new OnDemandSupplier(this.numberOfSuppliers, supplierName, productCategory, deliveringMethod, supplierContactInfo, paymentInfo);
                else if (supplyMethod == SupplyMethod.SCHEDULED)
                    supplier = new ScheduledSupplier(this.numberOfSuppliers, supplierName, productCategory, deliveringMethod, supplierContactInfo, paymentInfo);

                this.supplierService.RegisterNewSupplier(supplier);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ReadSupplierContractDataFromCSV() {
        Map<Integer, ArrayList<SupplyContractProductData>> supplierProductMap = new HashMap<>();

        InputStream in = CLI.class.getResourceAsStream("/contracts_data.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine){
                    isFirstLine = false;
                    continue; // Skip header
                }

                String[] parts = line.split(",");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                    if (parts[i].startsWith("\"") && parts[i].endsWith("\"")) {
                        parts[i] = parts[i].substring(1, parts[i].length() - 1);
                    }
                }

                int supplierId = Integer.parseInt(parts[0]);
                int productId = Integer.parseInt(parts[1]);
                double productPrice = Double.parseDouble(parts[2]);
                int quantityForDiscount = Integer.parseInt(parts[3]);
                int discountPercentage = Integer.parseInt(parts[4]);

                Product product = this.productService.GetProduct(productId);
                SupplyContractProductData mapping = new SupplyContractProductData(productPrice, quantityForDiscount, discountPercentage, product);

                // Add the product to the supplier's list in the map
                supplierProductMap.computeIfAbsent(supplierId, k -> new ArrayList<>()).add(mapping);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<Integer, ArrayList<SupplyContractProductData>> entry : supplierProductMap.entrySet()) {
            Supplier supplier = this.supplierService.GetSupplier(entry.getKey());
            SupplyMethod supplyMethod = supplier.getClass().getSimpleName().equals("OnDemandSupplier") ? SupplyMethod.ON_DEMAND : SupplyMethod.SCHEDULED;

            SupplyContract supplyContract = new SupplyContract(supplyMethod, entry.getValue(), 0);
            this.supplierService.RegisterNewSupplierContract(entry.getKey(), supplyContract);
        }
    }

    public void RegisterNewSupplier(String supplierName, ProductCategory productCategory, SupplyMethod supplyMethod, DeliveringMethod deliveringMethod, PaymentInfo paymentInfo, ContactInfo supplierContactInfo) {
        this.numberOfSuppliers++;
        Supplier supplier = null;
        if (supplyMethod == SupplyMethod.ON_DEMAND)
            supplier = new OnDemandSupplier(this.numberOfSuppliers, supplierName, productCategory, deliveringMethod, supplierContactInfo, paymentInfo);
        else if (supplyMethod == SupplyMethod.SCHEDULED)
            supplier = new ScheduledSupplier(this.numberOfSuppliers, supplierName, productCategory, deliveringMethod, supplierContactInfo, paymentInfo);

        this.supplierService.RegisterNewSupplier(supplier);
    }

    public void UpdateSupplier(int supplierID, String supplierName, PaymentInfo paymentInfo, DeliveringMethod deliveringMethod, ContactInfo contactInfo) {
        this.supplierService.UpdateSupplier(supplierID, supplierName, paymentInfo, deliveringMethod, contactInfo);
    }

    public void DeleteSupplier(int supplierID) {
        supplierService.DeleteSupplier(supplierID);
    }

    public ArrayList<Supplier> GetAllSuppliers() {
        return this.supplierService.GetAllSuppliers();
    }

    public Supplier GetSupplier(int supplierID) {
        return supplierService.GetSupplier(supplierID);
    }

    public void PrintSupplierContracts(int supplierID) {
        supplierService.PrintAllSupplierContracts(supplierID);
    }
    public void registerNewSupplierContract(){

    }
    public void addContractToSupplier(int supplierID, int contractID) {

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
    public int getNumberOfSuppliers(){
        return this.numberOfSuppliers;
    }
    public int getNumberOfContracts(){
        return this.numberOfContracts;
    }

}
