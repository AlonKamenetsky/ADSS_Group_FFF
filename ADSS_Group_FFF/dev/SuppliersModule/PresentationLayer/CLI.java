package SuppliersModule.PresentationLayer;

import java.util.*;
import SuppliersModule.ServiceLayer.ServiceController;


public class CLI {
    ServiceController serviceController;
    Scanner sc;
   boolean dataRead;

    public CLI() {
        this.sc = new Scanner(System.in);
        this.serviceController = new ServiceController();
        dataRead = false;
    }

    public void ReadDataFromCSVFiles() {
        System.out.println("Reading data from files...");
        this.serviceController.ReadDataFromCSVFiles();
        System.out.println("Done");
    }

    // --------------------------- PRODUCT FUNCTIONS ---------------------------

    public void registerNewProduct() {
        System.out.println("Enter product name: ");
        String productName = sc.nextLine();

        System.out.println("Enter product company: ");
        String productCompanyName = sc.nextLine();

        System.out.println("Enter product category (0-6): ");
        this.printProductCategoryMethods();
        int productCategory = readInt();

        int productID = serviceController.registerNewProduct(productName, productCompanyName, productCategory);
        if (productID != -1) System.out.println("Product added successfully.");
        else System.out.println("Error registering product.");

    }

    public void updateProduct() {
        System.out.println("Which product you want to change? Enter product: ");
        int productId = readInt();

        System.out.println("Enter new product name: ");
        String newProductName = sc.nextLine();

        System.out.println("Enter new product company: ");
        String newProductCompany = sc.nextLine();

        boolean result = serviceController.updateProduct(productId, newProductName, newProductCompany);
        if (result) System.out.println("Product updated successfully.");
        else System.out.println("Error updating product: no such product exists.");
    }

    public void deleteProduct() {
        System.out.println("Which product do you want to delete? Enter product ID");
        int productId = readInt();
        boolean result = serviceController.deleteProduct(productId);
        if (result) System.out.println("Product deleted successfully.");
        else System.out.println("Error deleting product: no such product exists.");
    }

    public void printProduct() {
        System.out.println("Which product do you want to search? Enter product ID: ");
        int productId = readInt();
        String result = this.serviceController.getProductAsString(productId);
        System.out.println(Objects.requireNonNullElse(result, "Error: No such product exists."));
    }

    public void printAllProducts() {
        for (String productString : this.serviceController.getAllProductsAsStrings())
            System.out.println(productString);
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public void registerNewSupplier() {
        System.out.println("Enter supplier name: ");
        String supplierName = sc.nextLine();

        printProductCategoryMethods();
        int productCategory = readInt();

        System.out.println("Enter bank account ID: ");
        String bankAccountInfo = sc.nextLine();

        System.out.println("Enter payment method: ");
        int paymentMethod = choosePaymentMethod();

        System.out.println("Enter delivery method: ");
        int deliveringMethod = chooseDeliveryMethod();

        System.out.println("--Creating new contact info--");
        System.out.println("Enter phone number: ");
        String phoneNumber = sc.nextLine();
        System.out.println("Enter address");
        String address = sc.nextLine();
        System.out.println("Enter email");
        String email = sc.nextLine();
        System.out.println("Enter contact name");
        String contactName = sc.nextLine();
        int supplyMethod = chooseSupplyMethod();
        ArrayList<Integer> supplyDays = null;
        if (supplyMethod == 0) {
            System.out.println("Enter days for supplier (Enter -1 for exit), Enter 1-7: ");
            supplyDays = new ArrayList<>();
            while (true) {
                int day =  readInt();
                if (day == -1)
                    break;
                supplyDays.add(day);
            }
        }

        int supplierID = this.serviceController.registerNewSupplier(supplyMethod, supplierName, productCategory, deliveringMethod, phoneNumber, address, email, contactName, bankAccountInfo, paymentMethod, supplyDays);

        if (supplierID == -1) {
            System.out.println("Error creating new supplier.");
            return;
        }
        else System.out.println("Supplier registered successfully.");


        System.out.println("Register new contract section:");
        this.registerNewContract(supplierID);
        System.out.println("Contract added successfully.");
    }

    public void updateSupplier() {
        System.out.println("Which supplier to update? Enter supplier ID: ");
        int supplierID = readInt();

        printSupplierUpdateOption();
        System.out.println("Enter option: ");
        int option = readInt();

        chooseSupplierUpdateOption(option, supplierID);
    }

    private void updateSupplierName(int supplierID) {
        System.out.println("Enter new supplier name: ");
        String SupplierName = sc.nextLine();

        boolean result = this.serviceController.updateSupplierName(supplierID, SupplierName);
        if (result) System.out.println("Supplier name updated successfully.");
        else System.out.println("Supplier name update failed.");
    }

    private void updateSupplierDeliveryMethod(int supplierID) {
        System.out.println("Enter delivery method: ");
        this.printDeliveryMethods();
        int deliveryMethod = readInt();

        boolean result = this.serviceController.updateSupplierDeliveringMethod(supplierID, deliveryMethod);
        if (result) System.out.println("Supplier delivery method updated successfully.");
        else System.out.println("Supplier delivery method update failed.");
    }

    private void updateSupplierContactInfo(int supplierID) {
        System.out.println("Enter new phone number: ");
        String phoneNumber = sc.nextLine();
        System.out.println("Enter address: ");
        String address = sc.nextLine();
        System.out.println("Enter email: ");
        String email = sc.nextLine();
        System.out.println("Enter contact name: ");
        String ContactName = sc.nextLine();

        boolean result = this.serviceController.updateSupplierContactInfo(supplierID, phoneNumber, address, email, ContactName);
        if (result) System.out.println("Supplier contact info updated successfully.");
        else System.out.println("Supplier contact info update failed.");
    }

    private void updateSupplierPaymentMethod(int supplierID) {
        System.out.println("Enter new bank account ID: ");
        String bankAccountInfo = sc.nextLine();
        System.out.println("Enter payment method: ");
        int paymentMethod = readInt();

        boolean result = this.serviceController.updateSupplierPaymentInfo(supplierID, bankAccountInfo, paymentMethod);
        if (result) System.out.println("Supplier payment info updated successfully.");
        else System.out.println("Supplier payment info update failed.");
    }


    private void deleteSupplier() {
        System.out.println("Which supplier you want to delete? Enter supplier ID: ");
        int supplierId = readInt();

        boolean result = serviceController.deleteSupplier(supplierId);
        if (result) System.out.println("Supplier deleted successfully.");
        else System.out.println("Error: No such supplier exists.");
    }

    private void printSupplier() {
        System.out.println("Which supplier do you want to search? Enter supplier ID: ");
        int supplierId = readInt();

        String result = this.serviceController.getSupplierAsString(supplierId);
        System.out.println(Objects.requireNonNullElse(result, "Error: No such supplier exists."));
    }

    private void printAllSuppliers() {
        for (String supplierString : this.serviceController.getAllSuppliersAsString())
            System.out.println(supplierString);
    }

    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    private void registerNewContract(int supplierId) {
        ArrayList<int[]> dataArray = new ArrayList<>();
        while (true) {
            System.out.println("Enter product ID (Enter -1 for exit): ");
            int productID = readInt();
            if (productID == -1) break;
            System.out.println("Enter product price: ");
            int price = readInt();
            System.out.println("Enter quantity for discount: ");
            int quantityForDiscount = readInt();
            System.out.println("Enter discount percentage: ");
            int discountPercentage = readInt();

            int[] data = {productID, price, quantityForDiscount, discountPercentage};
            dataArray.add(data);
        }
        if(this.serviceController.registerNewContract(supplierId, dataArray)) {
            System.out.println("Supplier registered successfully.");
            return;
        }
        System.out.println("Error");

    }
    private void deleteSupplyContract() {
    }

    private void printSupplyContract() {
        System.out.println("Enter contract ID: ");
        int contractID = readInt();
        System.out.println(serviceController.getContractsAsString(contractID));
    }

    private void printAllSupplyContracts() {
        String[] contracts = serviceController.getAllContractToStrings();
        for(String contract : contracts){
            System.out.println(contract);
        }
    }
    private void printSupplierContracts(int supplierId) {
        String[] result = this.serviceController.getSupplierContractsAsString(supplierId);
        if (result == null) {
            System.out.println("Error: No such supplier exists.");
            return;
        }

        for (String contractAsString : result)
            System.out.println(contractAsString);
    }

    // ------------------- Order FUNCTIONS -----------------------------

    private void registerNewOrder() {
        ArrayList<int[]> dataArray = new ArrayList<>();
        Date today = new Date();
        this.printAllSuppliers();
        System.out.println("Which supplier you want to order from? Enter supplier ID");
        int supplierId = readInt();
        this.printSupplierContracts(supplierId);

        while (true) {
            System.out.println("Enter product ID (Enter -1 for exit): ");
            int productID = readInt();
            if (productID == -1) {
                break;
            }
            System.out.println("Enter quantity");
            int quantity = readInt();
            int data[] = {productID, quantity};
            dataArray.add(data);
        }
        System.out.println("enter delivery date");
        String deliveryDate = sc.nextLine();
        if (serviceController.registerNewOrder(supplierId, dataArray, today, deliveryDate)) {
            System.out.println("Order registered successfully.");
        } else {
            System.out.println("Error: Failed to register new order.");
        }
    }

    private void updateOrder() {
        System.out.println("Which order you want to update? Enter orderID: ");
        int orderID = readInt();
        if(!serviceController.orderExists(orderID)) {
            System.out.println("Order does not exist.");
        }
        System.out.println("What do you want to update?");
        printOrderUpdateOption();
        int option = readInt();
        switch (option) {
            case 1:
                updateOrderContactInfo(orderID);
                break;
            case 2:
                updateOrderSupplyDate(orderID);
                break;
            case 3:
                removeProductsFromOrder(orderID);
                break;
            case 4:
                addProductsToOrder(orderID);
                break;
            case 5:
                updateOrderStatus(orderID);
                break;
            default:
                break;
        }
    }

    private void updateOrderContactInfo(int orderID) {
        System.out.println("Enter new phone number: ");
        String phoneNumber = sc.nextLine();
        System.out.println("Enter address: ");
        String address = sc.nextLine();
        System.out.println("Enter email: ");
        String email = sc.nextLine();
        System.out.println("Enter contact name: ");
        String contactName = sc.nextLine();

        boolean result = serviceController.updateOrderContactInfo(orderID, phoneNumber, address, email, contactName);
        if (result)
            System.out.println("Supplier contact info updated successfully.");
        else
            System.out.println("Supplier contact info update failed.");

    }

    private void updateOrderSupplyDate(int orderID) {
        System.out.println("Enter new supply date: ");
        String newSupplyDate = sc.nextLine();
        boolean result = serviceController.updateOrderSupplyDate(orderID, newSupplyDate);
        if (result) {
            System.out.println("Supplier supply date updated successfully.");
        }
        else
            System.out.println("Supplier supply date update failed.");
    }

    private void updateOrderStatus(int orderID) {
        this.printOrderStatusMethods();
        System.out.println("Enter order status: ");
        int status = readInt();

        if (serviceController.updateOrderStatus(orderID, status))
            System.out.println("Order status updated successfully.");
        else
            System.out.println("Order status update failed.");
    }

    private void removeProductsFromOrder(int orderID) {
        ArrayList<Integer> dataArray = new ArrayList<>();
        while (true) {
            System.out.println("Enter product ID (Enter -1 for exit): ");
            int productID = readInt();
            if (productID == -1)
                break;
            dataArray.add(productID);
        }

        boolean result = serviceController.removeProductsFromOrder(orderID, dataArray);
        if (result)
            System.out.println("Products removed successfully.");
        else
            System.out.println("Products remove failed.");
    }

    private void addProductsToOrder(int orderID) {
        String[] resultString = this.serviceController.getAvailableContractsForOrderAsString(orderID);
        if (resultString == null) {
            System.out.println("Error: No such order exists.");
            return;
        }

        for (String conrtractString : resultString)
            System.out.println(conrtractString);

        System.out.println("Enter product ID (Enter -1 for exit): ");
        ArrayList<int[]> dataArray = new ArrayList<>();
        while (true) {
            System.out.println("Enter product ID (Enter -1 for exit): ");
            int productID = readInt();
            if (productID == -1)
                break;
            System.out.println("Enter quantity");
            int quantity = readInt();
            int [] data = {productID, quantity};
            dataArray.add(data);
        }

        boolean resultAdd = this.serviceController.addProductsToOrder(orderID, dataArray);
        if (resultAdd)
            System.out.println("Product added successfully.");
        else
            System.out.println("Error: Failed to add product to order.");
    }

    private void deleteOrder() {
        System.out.println("Enter Order ID: ");
        int orderID = readInt();
        if (!serviceController.orderExists(orderID)) {
            System.out.println("Order does not exist.");
            return;
        }
        boolean deleted = serviceController.deleteOrder(orderID);
        if (deleted)
            System.out.println("Order deleted successfully.");
        else
            System.out.println("Order delete failed.");
    }

    private void printOrder() {
        System.out.println("Enter Order ID: ");
        int orderID = readInt();
        String result = this.serviceController.getOrderAsString(orderID);
        System.out.println(Objects.requireNonNullElse(result, "Error: No such order exists."));
    }

    private void printAllOrders() {
        for (String orderString : this.serviceController.getAllOrdersAsString())
            System.out.println(orderString);
    }



    // ------------------- CLI print Functions -------------------

    public void printMenuOptions() {
        System.out.println("0. Read data from files");
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
    public void printSupplierOptions() {
        System.out.println("1. Register a new supplier");
        System.out.println("2. Update supplier info");
        System.out.println("3. Delete supplier");
        System.out.println("4. Print supplier");
        System.out.println("5. Print all suppliers");
        System.out.println("6. Exit");
    }
    public void printSupplierUpdateOption() {
        System.out.println("1. Update supplier name");
        System.out.println("2. Update supplier delivery method");
        System.out.println("3. Update supplier contact info");
        System.out.println("4. Update supplier payment info");
        System.out.println("5. Exit");
    }
    public void printContractOptions() {
        System.out.println("1. Register a new contract");
        System.out.println("2. Delete supplier contract");
        System.out.println("3. print contract info");
        System.out.println("4. print all contracts");
        System.out.println("5. Exit");
    }

    public void printOrderOptions() {
        System.out.println("1. Add order");
        System.out.println("2. Update order");
        System.out.println("3. Delete order");
        System.out.println("4. print order");
        System.out.println("5. print all orders");
        System.out.println("6. Exit");
    }

    private void printOrderUpdateOption() {
        System.out.println("1. update order contact info");
        System.out.println("2. update order supply date");
        System.out.println("3. remove products from order");
        System.out.println("4. add products to order");
        System.out.println("5. update order status");
        System.out.println("6. go back");
    }
    private void printPaymentMethods() {
        System.out.println("0. Check");
        System.out.println("1. BANK_TRANSACTION");
        System.out.println("2. Cash");
    }

    private void printDeliveryMethods() {
        System.out.println("0. Pick up");
        System.out.println("1. Self delivering");
    }
    private void printProductCategoryMethods() {
        System.out.println("0. DIARY");
        System.out.println("1. FROZEN");
        System.out.println("2. FRUITS AND VEGETABLES");
        System.out.println("3. DRINKS");
        System.out.println("4. MEAT");
        System.out.println("5. DRIED");
        System.out.println("6. MISCELLANEOUS");
    }

    private void printOrderStatusMethods() {
        System.out.println("0. RECEIVED");
        System.out.println("1. IN_PROCESS");
        System.out.println("2. DELIVERED");
        System.out.println("3. ARRIVED");
        System.out.println("4. CANCELLED");
    }
    // ------------------- Choose helper functions -------------------


    public void chooseProductsOption(int option) {
        switch (option) {
            case 1:
                this.registerNewProduct();
                break;
            case 2:
                this.updateProduct();
                break;
            case 3:
                this.deleteProduct();
                break;
            case 4:
                this.printProduct();
                break;
            case 5:
                this.printAllProducts();
                break;
            case 6:
                return;

        }
    }


    public void chooseSupplierOption(int option) {
        switch (option) {
            case 1:
                this.registerNewSupplier();
                break;
            case 2:
                this.updateSupplier();
                break;
            case 3:
                this.deleteSupplier();
                break;
            case 4:
                this.printSupplier();
                break;
            case 5:
                this.printAllSuppliers();
                break;
            case 6:
                return;
        }
    }

    public void chooseSupplierUpdateOption(int option, int supplierID) {
        switch (option) {
            case 1:
                this.updateSupplierName(supplierID);
                break;
            case 2:
                this.updateSupplierDeliveryMethod(supplierID);
                break;
            case 3:
                this.updateSupplierContactInfo(supplierID);
                break;
            case 4:
                this.updateSupplierPaymentMethod(supplierID);
                break;
            case 5:
                return;
        }
    }

    public void chooseOrderOption(int option) {
        switch (option) {
            case 1:
                registerNewOrder();
                break;
            case 2:
                updateOrder();
                break;
            case 3:
                deleteOrder();
                break;
            case 4:
                printOrder();
                break;
            case 5:
                printAllOrders();
                break;
            case 6:
                return;
        }
    }

    private void chooseContractOption(int userInput) {
        switch (userInput) {
            case 1:
                System.out.println("Enter supplier ID");
                int supplierId = readInt();
                this.registerNewContract(supplierId);
                break;
            case 2:
                deleteSupplyContract();
                break;
            case 3:
                printSupplyContract();
                break;
            case 4:
                printAllSupplyContracts();
                break;
            case 5:
                return;
        }
    }
    public int chooseSupplyMethod(){
        int supplyMethod = -1;
        while(true){
            System.out.println("which type of supplier? \n0. SCHEDULED supplier \n1. ON_DEMAND supplier");
            supplyMethod = readInt();
            if(supplyMethod == 0 || supplyMethod == 1) break;
            else System.out.println("Invalid supply method.");
        }
        return supplyMethod;
    }
    public int chooseDeliveryMethod(){
        int deliveryMethod = -1;
        printDeliveryMethods();
        while(true){
            deliveryMethod = readInt();
            if(deliveryMethod == 0 || deliveryMethod == 1) break;
            else System.out.println("Invalid delivery method.");
        }
        return deliveryMethod;
    }
    public int choosePaymentMethod(){
        int paymentMethod = -1;
        while(true){
            printPaymentMethods();
            paymentMethod = readInt();
            if(paymentMethod == 0 || paymentMethod ==1 || paymentMethod==2) break;
            else System.out.println("Invalid payment method.");

        }
        return paymentMethod;
    }

    public void mainCliMenu() {
        System.out.println("Welcome to SuppliersModule!");

        while (true) {
            printMenuOptions();
            System.out.println("Please select an option: ");
            int userInput = readInt();
            switch (userInput) {
                case 0:
                    if(!dataRead) {
                        ReadDataFromCSVFiles();
                        dataRead = true;
                    }
                    else
                        System.out.println("Data already loaded.");
                    break;
                case 1:
                    printProductOptions();
                    userInput = readInt();
                    chooseProductsOption(userInput);
                    break;
                case 2:
                    printSupplierOptions();
                    userInput = readInt();
                    chooseSupplierOption(userInput);
                    break;
                case 3:
                    printContractOptions();
                    userInput = readInt();
                    chooseContractOption(userInput);
                    break;
                case 4:
                    printOrderOptions();
                    userInput = readInt();
                    chooseOrderOption(userInput);
                    break;
                case 5:
                    System.out.println("Bye Bye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option, please choose again");
            }
        }
    }


    // ---------------helpers------------
    private int readInt() {
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input, please enter a number:");
            sc.next(); // discard invalid input
        }
        int num = sc.nextInt();
        sc.nextLine(); // consume leftover \n
        return num;
    }


}