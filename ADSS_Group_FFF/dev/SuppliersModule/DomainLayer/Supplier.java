package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;

public abstract class Supplier {
    int supplierId;
    String supplierName;

    DeliveringMethod supplierDeliveringMethod;

    SupplyContract supplierContract;

    ContactInfo supplierContactInfo;

    PaymentInfo supplierPaymentInfo;

    public Supplier(int supplierId, String supplierName, DeliveringMethod supplierDeliveringMethod, SupplyContract supplierContract, ContactInfo supplierContactInfo, PaymentInfo supplierPaymentInfo) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierDeliveringMethod = supplierDeliveringMethod;
        this.supplierContract = supplierContract;
        this.supplierContactInfo = supplierContactInfo;
    }
    public int getSupplierId() {
        return supplierId;
    }
    public String getSupplierName() {
        return supplierName;
    }
    public DeliveringMethod getSupplierDeliveringMethod() {
        return supplierDeliveringMethod;
    }
    public SupplyContract getSupplierContract() {
        return supplierContract;
    }
    public ContactInfo getSupplierContactInfo() {
        return supplierContactInfo;
    }
    public PaymentInfo getSupplierPaymentInfo() {
        return supplierPaymentInfo;
    }
    public String toString() {
        return supplierId + "\t" + supplierName + "\t" + supplierContactInfo + "\t" + supplierPaymentInfo;
    }
}