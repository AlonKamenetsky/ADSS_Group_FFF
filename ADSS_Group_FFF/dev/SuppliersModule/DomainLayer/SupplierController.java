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
import java.util.HashMap;
import java.util.Map;

public class SupplierController {
    OrderController orderController;
    SupplyContractController supplyContractController;
    int numberOfSuppliers;
    ArrayList<Supplier> suppliersArrayList; // TEMP DATA STRUCTURE

    public SupplierController() {
        this.numberOfSuppliers = 0;
        this.suppliersArrayList = new ArrayList<>();
        orderController = new OrderController();
        supplyContractController = new SupplyContractController();
        this.readSuppliersFromCSVFile();
        //this.readSupplierContractDataFromCSV();
    }

    public void readSuppliersFromCSVFile() {
        InputStream in = SupplierController.class.getResourceAsStream("/suppliers_data.csv");
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

                String bankAccount = parts[8];
                String paymentMethodStr = parts[9].toUpperCase();
                PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodStr);

                this.registerNewSupplier(supplyMethod, supplierName, productCategory, deliveringMethod, phoneNumber, address, email, contactName, bankAccount, paymentMethod);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public void readSupplierContractDataFromCSV() {
//        Map<Integer, ArrayList<SupplyContractProductData>> supplierProductMap = new HashMap<>();
//
//        InputStream in = SupplierController.class.getResourceAsStream("/contracts_data.csv");
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
//            String line;
//            boolean isFirstLine = true;
//
//            while ((line = reader.readLine()) != null) {
//                if (isFirstLine){
//                    isFirstLine = false;
//                    continue; // Skip header
//                }
//
//                String[] parts = line.split(",");
//                for (int i = 0; i < parts.length; i++) {
//                    parts[i] = parts[i].trim();
//                    if (parts[i].startsWith("\"") && parts[i].endsWith("\"")) {
//                        parts[i] = parts[i].substring(1, parts[i].length() - 1);
//                    }
//                }
//
//                int supplierID = Integer.parseInt(parts[0]);
//                int productID = Integer.parseInt(parts[1]);
//                double productPrice = Double.parseDouble(parts[2]);
//                int quantityForDiscount = Integer.parseInt(parts[3]);
//                int discountPercentage = Integer.parseInt(parts[4]);
//
//                SupplyContractProductData mapping = new SupplyContractProductData(productID, productPrice, quantityForDiscount, discountPercentage);
//
//                supplierProductMap.computeIfAbsent(supplierID, k -> new ArrayList<>()).add(mapping);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        for (Map.Entry<Integer, ArrayList<SupplyContractProductData>> entry : supplierProductMap.entrySet()) {
//            Supplier supplier = this.getSupplier(entry.getKey());
//            SupplyMethod supplyMethod = supplier.getSupplyMethod();
//
//            SupplyContract supplyContract = new SupplyContract(supplyMethod, entry.getValue());
//            supplier.addSupplierContract(supplyContract);
//        }
//    }

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
        for(Supplier supplier : this.suppliersArrayList){
            if(supplier.supplierId == supplierID){
                return supplier.getSupplierDeliveringMethod();
            }
        }
        return null;
    }
    public ContactInfo getSupplierContactInfo(int supplierID){
        for(Supplier supplier : this.suppliersArrayList){
            if(supplier.supplierId == supplierID){
                return supplier.getSupplierContactInfo();
            }
        }
        return null;
    }


    public SupplyMethod getSupplierSupplyMethod(int supplierID) {
        for(Supplier supplier : this.suppliersArrayList){
            if(supplier.supplierId == supplierID){
                return supplier.getSupplyMethod();
            }
        }
        return null;
    }
    public boolean addNewContractToSupplier(int supplierId, SupplyContract contract){
        for(Supplier supplier : this.suppliersArrayList){
            if(supplier.supplierId == supplierId){
                supplier.supplierContracts.add(contract);
                return true;
            }
        }
        return false;
    }

    public int getSupplierContractId(int supplierId) {
        return 0;
    }
}
