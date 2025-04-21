package SuppliersModule.PresentationLayer;

import java.util.ArrayList;
import java.util.Scanner;

import SuppliersModule.DomainLayer.*;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.PaymentMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;
import SuppliersModule.ServiceLayer.ServiceController;

public class CLI {
    ServiceController serviceController;
    Scanner sc;

    public CLI(Scanner sc) {
        this.sc = sc;
        this.serviceController = new ServiceController();

        this.serviceController.ReadProductsFromCSVFile("/products_data.csv");
    }

    // --------------------------- PRODUCT FUNCTIONS ---------------------------

    public void InsertNewProduct() {
        System.out.println("Enter product name: ");
        String productName = sc.nextLine();

        System.out.println("Enter product company: ");
        String productCompanyName = sc.nextLine();

        System.out.println("Enter product category (0-6): ");
        ProductCategory productCategory = ProductCategory.values()[sc.nextInt()];

        serviceController.RegisterNewProduct(productName, productCompanyName, productCategory);
        System.out.println("Product added successfully.");
    }

    public void UpdateProduct() {
        System.out.println("Which product you want to change? Enter product ");
        int productId = sc.nextInt();
        System.out.println("Enter new product name: ");
        String newProductName = sc.nextLine();
        System.out.println("Enter new product company: ");
        String newProductCompany = sc.nextLine();
        System.out.println("Enter product category (0-6): ");
        ProductCategory productCategory = ProductCategory.values()[sc.nextInt()];
        Product product = serviceController.GetProduct(productId);

    }

    public void DeleteProduct() {
        System.out.println("Which product you want to delete? Enter product ID");
        int productId = sc.nextInt();
        serviceController.DeleteProduct(productId);
    }

    public void PrintAllProducts() {
        ArrayList<Product> products = serviceController.GetAllProducts();
        for (Product product : products)
            System.out.println(product);
    }

    public void printProduct() {
        System.out.println("Which product you want to search? Enter product ID");
        int productId = sc.nextInt();
        ArrayList<Product> products = serviceController.GetAllProducts();
        for (Product product : products) {
            if (product.getProductId() == productId) {
                System.out.println(product);
                break;
            }
        }
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public void RegisterNewSupplier() {
        System.out.println("Enter supplier name: ");
        String supplierName = sc.nextLine();

        printProductCategoryDialog();
        ProductCategory productCategory = ProductCategory.values()[sc.nextInt()];

        System.out.println("Enter bank account info");
        String bankAccountInfo = sc.nextLine();
        PrintPaymentMethods();
        PaymentMethod paymentMethod = PaymentMethod.values()[sc.nextInt()];
        PaymentInfo paymentInfo = new PaymentInfo(bankAccountInfo, paymentMethod);

        PrintDeliveryMethod();
        DeliveringMethod deliveringMethod = DeliveringMethod.values()[sc.nextInt()];
        sc.nextLine();
        System.out.println("Creating new contact info");
        System.out.println("Enter phone number");
        String phoneNumber = sc.nextLine();
        System.out.println("Enter address");
        String address = sc.nextLine();
        System.out.println("Enter email");
        String email = sc.nextLine();
        System.out.println("Enter name");
        String name = sc.nextLine();
        ContactInfo contactInfo = new ContactInfo(phoneNumber, address, email, name);
        System.out.println("Register new contract section: \n");
        SupplyContract contract = RegisterNewContract();

        serviceController.RegisterNewSupplier(supplierName, productCategory, contract.getSupplierSupplyMethod(), deliveringMethod, contract, paymentInfo, contactInfo);
        System.out.println("Supplier added successfully.");
    }


    public void UpdateSupplier() {
        System.out.println("Which supplier to update? Enter supplier ID: ");
        int supplierId = sc.nextInt();
        sc.nextLine();
        String SupplierName = null;
        PaymentInfo paymentInfo = null;
        DeliveringMethod deliveringMethod = null;
        ContactInfo contactInfo = null;
        System.out.println("Do you want to update supplier name? \n 1. yes \n 2. no");
        int updateSupplierName = sc.nextInt();
        sc.nextLine();
        if(updateSupplierName == 1) {
            System.out.println("Enter new supplier name: ");
            SupplierName = sc.nextLine();
        }
        System.out.println("Do you want to update payment method? \n 1. yes \n 2. no");
        int updatePaymentMethod = sc.nextInt();
        sc.nextLine();
        if(updatePaymentMethod == 1) {
            System.out.println("Enter bank account info");
            String bankAccountInfo = sc.nextLine();
            PrintPaymentMethods();
            PaymentMethod paymentMethod = PaymentMethod.values()[sc.nextInt()];
            paymentInfo = new PaymentInfo(bankAccountInfo, paymentMethod);
        }

        System.out.println("Do you want to update delivery method? \n 1. yes \n 2. no");
        int updateDeliveryMethod = sc.nextInt();
        sc.nextLine();
        if(updateDeliveryMethod == 1) {
            PrintDeliveryMethod();
            deliveringMethod = DeliveringMethod.values()[sc.nextInt()];
        }

        System.out.println("Do you want to update contact info? \n 1. yes \n 2. no");
        int updateContactInfo = sc.nextInt();
        sc.nextLine();
        if(updateContactInfo == 1) {
            System.out.println("Enter phone number");
            String phoneNumber = sc.nextLine();
            System.out.println("Enter address");
            String address = sc.nextLine();
            System.out.println("Enter email");
            String email = sc.nextLine();
            System.out.println("Enter name");
            String name = sc.nextLine();
            contactInfo = new ContactInfo(phoneNumber, address, email, name);
        }
        serviceController.UpdateSupplier(supplierId, SupplierName, paymentInfo, deliveringMethod, contactInfo);
    }



    private void DeleteSupplier() {
        System.out.println("Which supplier you want to delete? Enter supplier ID");
        int supplierId = sc.nextInt();
        serviceController.DeleteSupplier(supplierId);
    }

    private void printSupplier() {
        System.out.println("Which supplier you want to delete? Enter supplier ID");
        int supplierId = sc.nextInt();
        Supplier supplier = serviceController.GetSupplier(supplierId);
        System.out.println(supplier);
    }

    private void PrintAllSuppliers() {
        ArrayList<Supplier> suppliers = serviceController.GetAllSuppliers();
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers found.");
        }
        for (Supplier supplier : suppliers) {
            System.out.println(supplier);
        }
    }

    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    private SupplyContract RegisterNewContract() {
        System.out.println("which type of supplier? \n 0. SCHEDULED supplier \n 1. ON_DEMAND supplier");
        SupplyMethod supplyMethod = SupplyMethod.values()[sc.nextInt()];

        SupplyContract supplyContract = new SupplyContract(supplyMethod);

        while (true) {
            System.out.println("Enter product ID (Enter -1 for exit): ");
            int productID = sc.nextInt();
            if (productID == -1)
                break;
            Product product = serviceController.GetProduct(productID);
            System.out.println("Enter product price: ");
            int price = sc.nextInt();
            System.out.println("Enter quantity for discount: ");
            int quantityForDiscount = sc.nextInt();
            System.out.println("Enter discount percentage: ");
            int discountPercentage = sc.nextInt();

            SupplyContractProductData data = new SupplyContractProductData(price, quantityForDiscount, discountPercentage, product);
            supplyContract.AddSupplyContractProductData(data);
        }

        return supplyContract;
    }

    // ------------------- Order FUNCTIONS -----------------------------

    private void RegisterNewOrder() {

    }

    private void UpdateOrder() {
    }

    private void DeleteOrder() {
    }

    private void PrintOrder() {
    }

    private void PrintAllOrders() {
    }

    // --------------------- CLI print Functions


    public void printMenuOptions() {
        System.out.println("1. Product section");
        System.out.println("2. Supplier section");
        System.out.println("3. Supplier contract section");
        System.out.println("4. Order section");
        System.out.println("5. Exit");
    }

    public void printProductOptions() {
        System.out.println("Welcome to SuppliersModule!");
        System.out.println("1. Add product");
        System.out.println("2. Update product");
        System.out.println("3. Delete product");
        System.out.println("4. Print specific product");
        System.out.println("5. Print all products");
        System.out.println("6. Exit");
    }

    public void chooseProductsOption(int option) {
        switch (option) {
            case 1:
                this.InsertNewProduct();
                break;
            case 2:
                this.UpdateProduct();
                break;
            case 3:
                this.DeleteProduct();
                break;
            case 4:
                this.printProduct();
            case 5:
                this.PrintAllProducts();
                break;
            case 6:
                return;

        }
    }

    public void printSupplierOptions() {
        System.out.println("1. Register a new supplier");
        System.out.println("2. Update supplier info");
        System.out.println("3. Delete supplier");
        System.out.println("4. print supplier");
        System.out.println("5. print all suppliers");
        System.out.println("6. Exit");
    }

    public void chooseSupplierOption(int option) {
        switch (option) {
            case 1:
                this.RegisterNewSupplier();
                break;
            case 2:
                this.UpdateSupplier();
                break;
            case 3:
                this.DeleteSupplier();
                break;
            case 4:
                this.printSupplier();
            case 5:
                this.PrintAllSuppliers();
                break;
            case 6:
                return;

        }
    }


    public void printContractOptions() {
        System.out.println("1. Register a new contract");
        System.out.println("2. Update contract info");
        System.out.println("3. Delete contract info");
        System.out.println("4. print contract info");
        System.out.println("5. print all contracts");
        System.out.println("6. Exit");
    }
    

    public void printOrderOptions() {
        System.out.println("1. Add order");
        System.out.println("2. Update order");
        System.out.println("3. Delete order");
        System.out.println("4. print order");
        System.out.println("5. print all orders");
        System.out.println("6. Exit");
    }

    public void chooseOrderOption(int option) {
        switch (option) {
            case 1:
                RegisterNewOrder();
                break;
            case 2:
                UpdateOrder();
                break;
            case 3:
                DeleteOrder();
                break;
            case 4:
                PrintOrder();
                break;
            case 5:
                PrintAllOrders();
                break;
            case 6:
                return;

        }
    }

    private void PrintPaymentMethods() {
        System.out.println("0. Check");
        System.out.println("1. BANK_TRANSACTION");
        System.out.println("2. Cash");
    }

    private void PrintDeliveryMethod() {
        System.out.println("0. Pick up");
        System.out.println("1. Self delivering");
    }

    private void printProductCategoryDialog() {
        System.out.println("0. DIARY");
        System.out.println("1. FROZEN");
        System.out.println("2. FRUITS AND VEGETABLES");
        System.out.println("3. DRINKS");
        System.out.println("4. MEAT");
        System.out.println("5. DRIED");
        System.out.println("6. MISCELLANEOUS");
    }
}
