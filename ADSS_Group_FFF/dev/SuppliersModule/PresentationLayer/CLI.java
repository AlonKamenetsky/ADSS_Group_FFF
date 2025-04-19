package SuppliersModule.PresentationLayer;

import java.util.ArrayList;
import java.util.Scanner;

import SuppliersModule.DomainLayer.ContactInfo;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.PaymentMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.PaymentInfo;
import SuppliersModule.DomainLayer.Product;
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
        String productCompanyName  = sc.nextLine();

        System.out.println("Enter product category (0-6): ");
        ProductCategory productCategory = ProductCategory.values()[sc.nextInt()];

        serviceController.RegisterNewProduct(productName, productCompanyName, productCategory);
        System.out.println("Product added successfully.");
    }
    public void UpdateProduct(){
        System.out.println("Which product you want to change? Enter product ");
        int productId = sc.nextInt();
        System.out.println("Enter new product name: ");
        String newProductName = sc.nextLine();
        System.out.println("Enter new product company: ");
        String newProductCompany = sc.nextLine();
        System.out.println("Enter product category (0-6): ");
        ProductCategory productCategory = ProductCategory.values()[sc.nextInt()];
        serviceController.DeleteProduct(productId);
        serviceController.RegisterNewProduct(newProductName, newProductCompany, productCategory);
    }
    public void DeleteProduct(){
        System.out.println("Which product you want to delete? Enter product ID");
        int productId = sc.nextInt();
        serviceController.DeleteProduct(productId);
    }
    public void PrintAllProducts(){
        ArrayList<Product> products = serviceController.GetAllProducts();
        for (Product product : products)
            System.out.println(product);
    }
    public void printProduct(){
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
        System.out.println("Enter bank account info");
        String bankAccountInfo = sc.nextLine();
        PrintPaymentMethods();
        PaymentMethod paymentMethod = PaymentMethod.values()[sc.nextInt()];
        PaymentInfo paymentInfo = new PaymentInfo(bankAccountInfo, paymentMethod);
        PrintDeliveryMethod();
        DeliveringMethod deliveringMethod = DeliveringMethod.values()[sc.nextInt()];
        System.out.println("Enter phone number");
        String phoneNumber = sc.nextLine();
        System.out.println("Enter address");
        String address = sc.nextLine();
        System.out.println("Enter email");
        String email = sc.nextLine();
        System.out.println("Enter name");
        String name = sc.nextLine();
        ContactInfo contactInfo = new ContactInfo(phoneNumber, address, email, name);
        PrintSupplierTypeOptions();




    }



    public void UpdateSupplier() {
    }
    private void DeleteSupplier() {
    }
    private void printSupplier() {
    }
    private void PrintAllSuppliers() {
    }






    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    private void RegisterNewContract() {
    }
    private void UpdateContract() {
    }
    private void DeleteContract() {
    }
    private void PrintContract() {
    }

    private void PrintAllContracts() {
    }
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



    public void printMenuOptions(){
        System.out.println("1. Product section");
        System.out.println("2. Supplier section");
        System.out.println("3. Supplier contract section");
        System.out.println("4. Order section");
        System.out.println("5. Exit");
    }
    public void printProductOptions(){
        System.out.println("Welcome to SuppliersModule!");
        System.out.println("1. Add product");
        System.out.println("2. Update product");
        System.out.println("3. Delete product");
        System.out.println("4. Print specific product");
        System.out.println("5. Print all products");
        System.out.println("6. Exit");
    }
    public void chooseProductsOption(int option){
        switch (option){
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
    public void printSupplierOptions(){
        System.out.println("1. Register a new supplier");
        System.out.println("2. Update supplier info");
        System.out.println("3. Delete supplier");
        System.out.println("4. print supplier");
        System.out.println("5. print all suppliers");
        System.out.println("6. Exit");
    }
    public void chooseSupplierOption(int option){
        switch (option){
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
    public void chooseContractOption(int option){
        switch (option){
            case 1:
                RegisterNewContract();
                break;
            case 2:
                UpdateContract();
                break;
            case 3:
                DeleteContract();
                break;
            case 4:
                PrintContract();
            case 5:
                PrintAllContracts();
                break;
            case 6:
                return;
        }
    }










    public void printOrderOptions(){
        System.out.println("1. Add order");
        System.out.println("2. Update order");
        System.out.println("3. Delete order");
        System.out.println("4. print order");
        System.out.println("5. print all orders");
        System.out.println("6. Exit");
    }
    public void chooseOrderOption(int option){
        switch (option){
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
    private void PrintPaymentMethods(){
        System.out.println("1.Check");
        System.out.println("2.BANK_TRANSACTION");
        System.out.println("3.Cash");
    }
    private void PrintDeliveryMethod(){
        System.out.println("1. Pick up");
        System.out.println("2. Self delivering");
    }

    private void PrintSupplierTypeOptions() {
    }



}
