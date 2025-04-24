package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.PaymentMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

public class SupplierController {
    OrderController orderController;
    SupplyContractController supplyContractController;
    int numberOfSuppliers;
    ArrayList<Supplier> suppliersArrayList; // TEMP DATA STRUCTURE

    public SupplierController() {
        this.numberOfSuppliers = 0;
        this.suppliersArrayList = new ArrayList<>();

        this.orderController = new OrderController();
        this.supplyContractController = new SupplyContractController();

        this.ReadSuppliersFromCSVFile();
    }

    private void ReadSuppliersFromCSVFile() {
        InputStream in = SupplierController.class.getResourceAsStream("/suppliers_data.csv");
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

                String bankAccount = parts[8];
                String paymentMethodStr = parts[9].toUpperCase();
                PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodStr);

                this.registerNewSupplier(supplyMethod, supplierName, productCategory, deliveringMethod, phoneNumber, address, email, contactName, bankAccount, paymentMethod);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        for (Supplier supplier : this.suppliersArrayList)
            for (SupplyContract supplyContract : this.supplyContractController.getAllSupplierContracts(supplier.getSupplierId()))
                supplier.addSupplierContract(supplyContract);
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public int registerNewSupplier(SupplyMethod supplyMethod, String supplierName, ProductCategory productCategory, DeliveringMethod deliveringMethod,
                                    String phoneNumber, String address, String email, String contactName,
                                    String bankAccount, PaymentMethod paymentMethod) {
        ContactInfo supplierContactInfo = new ContactInfo(phoneNumber, address, email, contactName);
        PaymentInfo supplierPaymentInfo = new PaymentInfo(bankAccount, paymentMethod);

        Supplier supplier = null;
        if (supplyMethod == SupplyMethod.ON_DEMAND)
            supplier = new OnDemandSupplier(this.numberOfSuppliers++, supplierName, productCategory, deliveringMethod, supplierContactInfo, supplierPaymentInfo);
        else if (supplyMethod == SupplyMethod.SCHEDULED)
            supplier = new ScheduledSupplier(this.numberOfSuppliers++, supplierName, productCategory, deliveringMethod, supplierContactInfo, supplierPaymentInfo);

        this.suppliersArrayList.add(supplier);
        return supplier.getSupplierId();
    }

    private Supplier getSupplier(int supplierID) {
        for (Supplier supplier : this.suppliersArrayList)
            if (supplier.supplierId == supplierID)
                return supplier;

        return null;
    }

    public boolean updateSupplierName(int supplierID, String supplierName) {
        Supplier supplier = getSupplier(supplierID);
        if (supplier == null)
            return false;

        supplier.setSupplierName(supplierName);
        return true;
    }

    public boolean updateSupplierDeliveringMethod(int supplierID, DeliveringMethod deliveringMethod) {
        Supplier supplier = getSupplier(supplierID);
        if (supplier == null)
            return false;

        supplier.setSupplierDeliveringMethod(deliveringMethod);
        return true;
    }

    public boolean updateSupplierContactInfo(int supplierID, String phoneNumber, String address, String email, String contactName) {
        Supplier supplier = getSupplier(supplierID);
        if (supplier == null)
            return false;

        ContactInfo contactInfo = new ContactInfo(phoneNumber, address, email, contactName);
        supplier.setSupplierContactInfo(contactInfo);
        return true;
    }

    public boolean updateSupplierPaymentInfo(int supplierID, String bankAccount, PaymentMethod paymentMethod) {
        Supplier supplier = getSupplier(supplierID);
        if (supplier == null)
            return false;

        PaymentInfo paymentInfo = new PaymentInfo(bankAccount, paymentMethod);
        supplier.setSupplierPaymentInfo(paymentInfo);
        return true;
    }

    public boolean deleteSupplier(int supplierID) {
        return this.suppliersArrayList.removeIf(supplier -> supplier.supplierId == supplierID);
    }

    public String[] getAllSuppliersAsString() {
        String[] suppliersAsString = new String[this.suppliersArrayList.size()];
        for (Supplier supplier : this.suppliersArrayList)
            suppliersAsString[supplier.supplierId] = supplier.toString();

        return suppliersAsString;
    }

    public String getSupplierAsString(int supplierID) {
        Supplier supplier = this.getSupplier(supplierID);
        if (supplier != null)
            return supplier.toString();
        return null;
    }

    public ProductCategory getSupplierProductCategory(int supplierID) {
        Supplier supplier = getSupplier(supplierID);
        if (supplier != null)
            return supplier.getSupplierProductCategory();
        return null;
    }

    public DeliveringMethod getSupplierDeliveringMethod(int supplierID) {
        Supplier supplier = getSupplier(supplierID);
        if (supplier != null)
            return supplier.getSupplierDeliveringMethod();

        return null;
    }

    public ContactInfo getSupplierContactInfo(int supplierID){
        Supplier supplier = getSupplier(supplierID);
        if (supplier != null)
            return supplier.getSupplierContactInfo();

        return null;
    }

    public SupplyMethod getSupplierSupplyMethod(int supplierID) {
        Supplier supplier = getSupplier(supplierID);
        if (supplier != null)
            return supplier.getSupplyMethod();

        return null;
    }

    // --------------------------- CONTRACT FUNCTIONS ---------------------------

    public boolean registerNewContract(int supplierID, ArrayList<int[]> dataList) {

        Supplier supplier = getSupplier(supplierID);
        if (supplier == null)
            return false;

        SupplyMethod supplyMethod = supplier.getSupplyMethod();

        SupplyContract contract = supplyContractController.registerNewContract(supplierID, dataList, supplyMethod);

        supplier.addSupplierContract(contract);

        return true;
    }

    public String getContractToString(int contractID) {
        return this.supplyContractController.getContractToString(contractID);
    }

    // --------------------------- ORDER FUNCTIONS ---------------------------

    private SupplyContract ValidateProductInContracts(int supplierID, int productID) {
        Supplier supplier = getSupplier(supplierID);
        ArrayList<SupplyContract> supplyContractArrayList = supplier.getSupplierContracts();
        if (supplyContractArrayList == null)
            return null;

        for (SupplyContract contract : supplyContractArrayList)
            if (contract.CheckIfProductInData(productID))
                return contract;

        return null;
    }

    public boolean registerNewOrder(int supplierId, ArrayList<int[]> dataList, Date creationDate, Date deliveryDate) {
        DeliveringMethod deliveringMethod = this.getSupplierDeliveringMethod(supplierId);
        ContactInfo contactInfo = this.getSupplierContactInfo(supplierId);

        double totalPrice = 0;
        for(int[] entry : dataList) {

            int productId = entry[0];
            SupplyContract supplyContract = ValidateProductInContracts(supplierId, productId);
            if (supplyContract == null)
                return false;
            SupplyContractProductData data = supplyContract.getSupplyContractProductDataOfProduct(productId);

            int quantity = entry[1];
            double productPrice = data.getProductPrice();

            if(quantity >=  data.getQuantityForDiscount()){
                productPrice = data.getProductPrice();
                productPrice = productPrice*((100 - data.getDiscountPercentage()) / 100);
                totalPrice +=  productPrice*quantity;
            }
            else {
                totalPrice += productPrice*quantity;
            }
        }

        this.orderController.registerOrder(supplierId, dataList, totalPrice, creationDate, deliveryDate, deliveringMethod, contactInfo);
        return true;
    }

    public boolean updateOrderContactInfo(int orderId, String phoneNumber, String address, String email, String contactName){
        return this.orderController.updateOrderContactInfo(orderId, phoneNumber, address, email, contactName);
    }

    public boolean updateOrderSupplyDate(int orderID, Date supplyDate){
        return this.orderController.updateOrderSupplyDate(orderID, supplyDate);
    }

    public boolean updateOrderSupplyMethod(int orderID, int supplyMethod){
        return this.orderController.updateOrderSupplyMethod(orderID, supplyMethod);
    }

    public boolean deleteOrder(int orderID) {
        return this.orderController.deleteOrder(orderID);
    }

    public Date getOrderSupplyDate(int orderID){
        return this.orderController.getOrderSupplyDate(orderID);
    }

    public String getOrderAsString(int orderID) {
        return this.orderController.getOrderAsString(orderID);
    }

    public String[] getAllOrdersAsString() {
        return this.orderController.getAllOrdersAsString();
    }
}
