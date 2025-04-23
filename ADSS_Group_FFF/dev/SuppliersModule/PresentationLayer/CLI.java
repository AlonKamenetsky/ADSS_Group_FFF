package SuppliersModule.PresentationLayer;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import SuppliersModule.DomainLayer.*;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.PaymentMethod;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;
import SuppliersModule.ServiceLayer.ServiceController;

import java.time.LocalDate;

public class CLI {
    ServiceController serviceController;
    Scanner sc;

    public CLI(Scanner sc) {
        this.sc = sc;
        this.serviceController = new ServiceController();
    }

    // --------------------------- PRODUCT FUNCTIONS ---------------------------

    public void RegisterNewProduct() {
        System.out.println("Enter product name: ");
        String productName = sc.nextLine();

        System.out.println("Enter product company: ");
        String productCompanyName = sc.nextLine();

        System.out.println("Enter product category (0-6): ");
        this.printProductCategoryDialog();
        int productCategory = sc.nextInt();

        int productID = serviceController.RegisterNewProduct(productName, productCompanyName, productCategory);
        if (productID != -1)
            System.out.println("Product added successfully.");
        else
            System.out.println("Error registering product.");

    }

    public void UpdateProduct() {
        System.out.println("Which product you want to change? Enter product: ");
        int productId = sc.nextInt();

        System.out.println("Enter new product name: ");
        String newProductName = sc.nextLine();

        System.out.println("Enter new product company: ");
        String newProductCompany = sc.nextLine();

        System.out.println("Enter product category: ");
        this.printProductCategoryDialog();
        int productCategory = sc.nextInt();

        boolean result = serviceController.UpdateProduct(productId, newProductName, newProductCompany, productCategory);
        if (result)
            System.out.println("Product updated successfully.");
        else
            System.out.println("Error updating product: no such product exists.");
    }

    public void DeleteProduct() {
        System.out.println("Which product do you want to delete? Enter product ID");
        int productId = sc.nextInt();
        boolean result = serviceController.DeleteProduct(productId);
        if (result)
            System.out.println("Product deleted successfully.");
        else
            System.out.println("Error deleting product: no such product exists.");
    }

    public void printProduct() {
        System.out.println("Which product do you want to search? Enter product ID: ");
        int productId = sc.nextInt();
        String result = this.serviceController.GetProductAsString(productId);
        System.out.println(Objects.requireNonNullElse(result, "Error: No such product exists."));
    }

    public void PrintAllProducts() {
        for (String productString : this.serviceController.GetAllProductsAsStrings())
            System.out.println(productString);
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public void RegisterNewSupplier() {
        System.out.println("Enter supplier name: ");
        String supplierName = sc.nextLine();

        printProductCategoryDialog();
        int productCategory = sc.nextInt();

        System.out.println("Enter bank account ID: ");
        String bankAccountInfo = sc.nextLine();

        System.out.println("Enter payment method: ");
        PrintPaymentMethods();
        int paymentMethod = sc.nextInt();

        System.out.println("Enter delivery method: ");
        PrintDeliveryMethod();
        int deliveringMethod = sc.nextInt();

        System.out.println("--Creating new contact info--");
        System.out.println("Enter phone number: ");
        String phoneNumber = sc.nextLine();
        System.out.println("Enter address");
        String address = sc.nextLine();
        System.out.println("Enter email");
        String email = sc.nextLine();
        System.out.println("Enter contact name");
        String contactName = sc.nextLine();

        System.out.println("which type of supplier? \n0. SCHEDULED supplier \n1. ON_DEMAND supplier");
        int supplyMethod = sc.nextInt();

        int supplierID = this.serviceController.RegisterNewSupplier(supplyMethod, supplierName, productCategory, deliveringMethod, phoneNumber, address, email, contactName, bankAccountInfo, paymentMethod);

        if (supplierID == -1)
            System.out.println("Error creating new supplier.");
        else
            System.out.println("Supplier registered successfully.");


        System.out.println("Register new contract section:");
        this.RegisterNewContract(supplierID);
        System.out.println("Contract added successfully.");
    }

    private void UpdateSupplierName(int supplierID) {
        System.out.println("Enter new supplier name: ");
        String SupplierName = sc.nextLine();

        boolean result = this.serviceController.UpdateSupplierName(supplierID, SupplierName);
        if (result)
            System.out.println("Supplier name updated successfully.");
        else
            System.out.println("Supplier name update failed.");
    }

    private void UpdateSupplierDeliveryMethod(int supplierID) {
        System.out.println("Enter delivery method: ");
        this.PrintDeliveryMethod();
        int deliveryMethod = sc.nextInt();

        boolean result = this.serviceController.UpdateSupplierDeliveringMethod(supplierID, deliveryMethod);
        if (result)
            System.out.println("Supplier delivery method updated successfully.");
        else
            System.out.println("Supplier delivery method update failed.");
    }

    private void UpdateSupplierContactInfo(int supplierID) {
        System.out.println("Enter new phone number: ");
        String phoneNumber = sc.nextLine();
        System.out.println("Enter address: ");
        String address = sc.nextLine();
        System.out.println("Enter email: ");
        String email = sc.nextLine();
        System.out.println("Enter contact name: ");
        String ContactName = sc.nextLine();

        boolean result = this.serviceController.UpdateSupplierContactInfo(supplierID, phoneNumber, address, email, ContactName);
        if (result)
            System.out.println("Supplier contact info updated successfully.");
        else
            System.out.println("Supplier contact info update failed.");
    }

    private void UpdateSupplierPaymentMethod(int supplierID) {
        System.out.println("Enter new bank account ID: ");
        String bankAccountInfo = sc.nextLine();
        System.out.println("Enter payment method: ");
        int paymentMethod = sc.nextInt();

        boolean result = this.serviceController.UpdateSupplierPaymentInfo(supplierID, bankAccountInfo, paymentMethod);
        if (result)
            System.out.println("Supplier payment info updated successfully.");
        else
            System.out.println("Supplier payment info update failed.");
    }

    public void UpdateSupplier() {
        System.out.println("Which supplier to update? Enter supplier ID: ");
        int supplierID = sc.nextInt();
        sc.nextLine();

        printSupplierUpdateOption();
        System.out.println("Enter option: ");
        int option = sc.nextInt();

        chooseSupplierUpdateOption(option, supplierID);
    }

    private void DeleteSupplier() {
        System.out.println("Which supplier you want to delete? Enter supplier ID: ");
        int supplierId = sc.nextInt();

        boolean result = serviceController.DeleteSupplier(supplierId);
        if (result)
            System.out.println("Supplier deleted successfully.");
        else
            System.out.println("Error: No such supplier exists.");
    }

    private void PrintSupplier() {
        System.out.println("Which supplier do you want to search? Enter supplier ID: ");
        int supplierId = sc.nextInt();

        String result = this.serviceController.GetSupplierAsString(supplierId);
        System.out.println(Objects.requireNonNullElse(result, "Error: No such supplier exists."));
    }

    private void PrintAllSuppliers() {
        for (String supplier : this.serviceController.GetAllSuppliersAsString())
            System.out.println(supplier);
    }

    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    private void RegisterNewContract(int supplierId) {
        ArrayList<int[]> dataArray = new ArrayList<>();
        while (true) {
            System.out.println("Enter product ID (Enter -1 for exit): ");
            int productID = sc.nextInt();
            if (productID == -1)
                break;
            System.out.println("Enter product price: ");
            int price = sc.nextInt();
            System.out.println("Enter quantity for discount: ");
            int quantityForDiscount = sc.nextInt();
            System.out.println("Enter discount percentage: ");
            int discountPercentage = sc.nextInt();

            int[] data = {productID, price, quantityForDiscount, discountPercentage};
            dataArray.add(data);
        }

        boolean result = this.serviceController.RegisterNewContract(supplierId, dataArray);
        if (result)
            System.out.println("Contract registered successfully.");
        else
            System.out.println("Error: Failed to register new contract.");
    }

    private void PrintSupplierContract(int supplierId) {
       // serviceController.PrintSupplierContracts(supplierId);
    }

    // ------------------- Order FUNCTIONS -----------------------------

    private void RegisterNewOrder() {
        LocalDate today = LocalDate.now();
        this.PrintAllSuppliers();
        System.out.println("Which supplier you want to register? Enter supplier ID");
        int supplierId = sc.nextInt();
        sc.nextLine();
        this.PrintSupplierContract(supplierId);

    }

    private void UpdateOrder() {
    }

    private void DeleteOrder() {
    }

    private void PrintOrder() {
    }

    private void PrintAllOrders() {
    }

    // ------------------- CLI print Functions -------------------

    public void printMenuOptions() {
        System.out.println("1. Product section");
        System.out.println("2. Supplier section");
        System.out.println("3. Supplier contract section");
        System.out.println("4. Order section");
        System.out.println("5. Exit");
    }

    public void printProductOptions() {
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
                this.RegisterNewProduct();
                break;
            case 2:
                this.UpdateProduct();
                break;
            case 3:
                this.DeleteProduct();
                break;
            case 4:
                this.printProduct();
                break;
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
        System.out.println("4. Print supplier");
        System.out.println("5. Print all suppliers");
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
                this.PrintSupplier();
                break;
            case 5:
                this.PrintAllSuppliers();
                break;
            case 6:
                return;
        }
    }

    public void printSupplierUpdateOption() {
        System.out.println("1. Update supplier name");
        System.out.println("2. Update supplier delivery method");
        System.out.println("3. Update supplier contact info");
        System.out.println("4. Update supplier payment info");
        System.out.println("5. Exit");
    }

    public void chooseSupplierUpdateOption(int option, int supplierID) {
        switch (option) {
            case 1:
                this.UpdateSupplierName(supplierID);
                break;
            case 2:
                this.UpdateSupplierDeliveryMethod(supplierID);
                break;
            case 3:
                this.UpdateSupplierContactInfo(supplierID);
                break;
            case 4:
                this.UpdateSupplierPaymentMethod(supplierID);
                break;
            case 5:
                return;
        }
    }

    public void printContractOptions() {
        System.out.println("1. Register a new contract");
        System.out.println("2. Update supplier contract");
        System.out.println("3. Delete supplier contract");
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
