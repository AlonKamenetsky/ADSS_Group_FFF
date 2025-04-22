package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;

import java.util.ArrayList;

public abstract class Supplier {
    int supplierId;
    String supplierName;

    ProductCategory productCategory;

    DeliveringMethod supplierDeliveringMethod;

    ArrayList<SupplyContract> supplierContracts;

    ContactInfo supplierContactInfo;

    PaymentInfo supplierPaymentInfo;

    public Supplier(int supplierId, String supplierName, ProductCategory productCategory, DeliveringMethod supplierDeliveringMethod,  ContactInfo supplierContactInfo, PaymentInfo supplierPaymentInfo) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.productCategory = productCategory;
        this.supplierDeliveringMethod = supplierDeliveringMethod;

        this.supplierContracts = new ArrayList<SupplyContract>();

        this.supplierContactInfo = supplierContactInfo;

        this.supplierPaymentInfo = supplierPaymentInfo;
    }
    public int getSupplierId() {
        return supplierId;
    }
    public String getSupplierName() {
        return supplierName;
    }
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    public DeliveringMethod getSupplierDeliveringMethod() {
        return supplierDeliveringMethod;
    }
    public void setSupplierDeliveringMethod(DeliveringMethod supplierDeliveringMethod) {
        this.supplierDeliveringMethod = supplierDeliveringMethod;
    }
    public ArrayList<SupplyContract> getSupplierContracts() {
        return supplierContracts;
    }
    public ContactInfo getSupplierContactInfo() {
        return supplierContactInfo;
    }
    public void setSupplierContactInfo(ContactInfo supplierContactInfo) {
        this.supplierContactInfo = supplierContactInfo;
    }
    public PaymentInfo getSupplierPaymentInfo() {
        return supplierPaymentInfo;
    }
    public void setSupplierPaymentInfo(PaymentInfo supplierPaymentInfo) {
        this.supplierPaymentInfo = supplierPaymentInfo;
    }
    public void AddNewContract(SupplyContract supplyContract) {
        this.supplierContracts.add(supplyContract);
    }
    public String toString() {
        return String.format(
                "Supplier ID: %d%n" +
                        "Name: %s%n" +
                        "Category: %s%n" +
                        "Delivery Method: %s%n" +
                        "Contact Info: %s%n" +
                        "Payment Info: %s%n" +
                        "Contracts: %s",
                supplierId,
                supplierName,
                productCategory,
                supplierDeliveringMethod,
                supplierContactInfo,
                supplierPaymentInfo,
                supplierContracts
        );
    }
}