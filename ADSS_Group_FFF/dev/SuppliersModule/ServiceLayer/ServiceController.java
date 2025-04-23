package SuppliersModule.ServiceLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.PaymentMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;
import SuppliersModule.DomainLayer.SupplyContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServiceController {
    SupplierService supplierService;
    ProductService productService;

    public ServiceController() {
        this.supplierService = new SupplierService();
        this.productService = new ProductService();
    }

    // --------------------------- VALIDATION FUNCTIONS ---------------------------

    private boolean validateProductCategory(int productCategory) {
        return (productCategory > 0 && productCategory < ProductCategory.values().length);
    }

    private boolean validateSupplyMethod(int supplyMethod) {
        return (supplyMethod > 0 && supplyMethod < SupplyMethod.values().length);
    }

    private boolean validateDeliveringMethod(int deliveringMethod) {
        return (deliveringMethod > 0 && deliveringMethod < DeliveringMethod.values().length);
    }

    private boolean validatePaymentMethod(int paymentMethod) {
        return (paymentMethod > 0 && paymentMethod < PaymentMethod.values().length);
    }

    private boolean validateSupplierAndProduct(int supplierID, int productID) {
        return this.supplierService.getSupplierProductCategory(supplierID) == this.productService.getProductCategory(productID);
    }

    private boolean validateContractProductData(int price, int quantityForDiscount, int discountPercentage) {
        return price <= 0 || quantityForDiscount <= 0 || !(discountPercentage > 0 && discountPercentage < 100);
    }
    private Date validateOrderDated(Date orderMade, String supplyDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        try {
            Date parsedSupplyDate = sdf.parse(supplyDate);
            if (parsedSupplyDate.after(orderMade)) {
                return parsedSupplyDate;
            } else {
                return null; // valid format but not after orderMade
            }
        } catch (ParseException e) {
            return null; // invalid date format
        }
    }
    private Date validateDate(String strDate){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // Makes sure it strictly checks the format (e.g., no 31/02/2024)
        try {
            return sdf.parse(strDate); // Returns a java.util.Date object
        } catch (ParseException e) {
            return null; // If invalid, return null
        }
    }



    // --------------------------- PRODUCT FUNCTIONS ---------------------------

    public int registerNewProduct(String productName, String productCompanyName, int productCategory) {
        if (validateProductCategory(productCategory))
            return this.productService.registerNewProduct(productName, productCompanyName, ProductCategory.values()[productCategory]);
        return -1;
    }

    public boolean updateProduct(int productID, String productName, String productCompanyName, int productCategory) {
        if (validateProductCategory(productCategory))
            return this.productService.updateProduct(productID, productName, productCompanyName, ProductCategory.values()[productCategory]);
        return false;
    }

    public boolean DeleteProduct(int productID) {
        return this.productService.deleteProduct(productID);
    }

    public String[] GetAllProductsAsStrings() {
        return this.productService.getProductsAsString();
    }

    public String GetProductAsString(int productID) {
        return this.productService.getProductAsString(productID);
    }

    // --------------------------- SUPPLIER FUNCTIONS ---------------------------

    public int RegisterNewSupplier(int supplyMethod, String supplierName, int productCategory, int deliveringMethod,
                                    String phoneNumber, String address, String email, String contactName,
                                    String bankAccount, int paymentMethod) {
        if (this.validateProductCategory(productCategory) && this.validateSupplyMethod(supplyMethod) && this.validateDeliveringMethod(deliveringMethod) && this.validatePaymentMethod(paymentMethod))
            return this.supplierService.registerNewSupplier(supplyMethod, supplierName, productCategory, deliveringMethod, phoneNumber, address, email, contactName, bankAccount, paymentMethod);
        return -1;
    }

    public boolean UpdateSupplierName(int supplierID, String supplierName) {
        return this.supplierService.updateSupplierName(supplierID, supplierName);
    }

    public boolean UpdateSupplierDeliveringMethod(int supplierID, int deliveringMethod) {
        if (this.validateDeliveringMethod(deliveringMethod))
            return this.supplierService.updateSupplierDeliveringMethod(supplierID, deliveringMethod);
        return false;
    }

    public boolean UpdateSupplierContactInfo(int supplierID, String phoneNumber, String address, String email, String contactName) {
        return this.supplierService.updateSupplierContactInfo(supplierID, phoneNumber, address, email, contactName);
    }

    public boolean UpdateSupplierPaymentInfo(int supplierId, String bankAccount, int paymentMethod) {
        if (this.validatePaymentMethod(paymentMethod))
            return this.supplierService.updateSupplierPaymentInfo(supplierId, bankAccount, paymentMethod);
        return false;
    }

    public boolean RegisterNewContract(int supplierID, ArrayList<int[]> dataList) {
        for (int[] data : dataList)
            if (!validateSupplierAndProduct(supplierID, data[0]) || !validateContractProductData(data[1], data[2], data[3]))
                return false;

        supplierService.registerNewContract(supplierID, dataList);
        return true;
    }

    public boolean DeleteSupplier(int supplierID) {
        return supplierService.deleteSupplier(supplierID);
    }

    public String[] GetAllSuppliersAsString() {
        return this.supplierService.getAllSuppliersAsString();
    }

    public String GetSupplierAsString(int supplierID) {
        return this.supplierService.getSupplierAsString(supplierID);
    }

//    public void PrintSupplierContracts(int supplierID) {
//        supplierService.PrintAllSupplierContracts(supplierID);
//    }
//
//    public void registerNewSupplierContract(){
//
//    }
    // --------------------------- ORDER FUNCTIONS ---------------------------

    public boolean registerNewOrder(int supplierId, ArrayList<int[]> dataList, Date creationDate, String deliveryDate) {
        Date deliveryDateAsDate = this.validateOrderDated(creationDate, deliveryDate);
        if(deliveryDateAsDate == null)
            return false;
        if(deliveryDateAsDate.before(creationDate))
            return false;
        return supplierService.registerNewOrder(supplierId, dataList, creationDate, deliveryDateAsDate);
    }
    public boolean updateOrderContactInfo(int orderId,  String phoneNumber, String address, String email, String contactName){
        return this.supplierService.updateOrderContactInfo(orderId, phoneNumber, address, email, contactName);
    }
    public boolean updateOrderSupplyDate(int orderID, String supplyDate){
        Date supplyDateAsDate = this.validateDate(supplyDate);
        if(supplyDateAsDate == null)
            return false;
        Date oldSupplyDate = supplierService.getSupplyDate(orderID);
        if(supplyDateAsDate.before(oldSupplyDate))
            return false;
        return supplierService.updateOrderSupplyDate(orderID, supplyDateAsDate);
    }
    public boolean updateOrderSupplyMethod(int orderID, int supplyMethod){
        return supplierService.updateOrderSupplyMethod(orderID, supplyMethod);
    }
    public boolean deleteOrder(int orderID) {
        return supplierService.deleteOrder(orderID);
    }
    public boolean printOrder(int orderID) {
        return supplierService.printOrder(orderID);
    }
    public void printAllOrders() {
        supplierService.printAllOrders();
    }
}
