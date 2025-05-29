package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.SupplierDTO;
import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.util.ArrayList;

public abstract class Supplier {
    int supplierId;
    String supplierName;

    ProductCategory productCategory;

    DeliveringMethod supplierDeliveringMethod;

    ArrayList<SupplyContract> supplierContracts;

    ContactInfo supplierContactInfo;

    PaymentInfo supplierPaymentInfo;

    SupplierDTO supplierDTO;

    public Supplier(int supplierId, String supplierName, ProductCategory productCategory, DeliveringMethod supplierDeliveringMethod,  ContactInfo supplierContactInfo, PaymentInfo supplierPaymentInfo) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.productCategory = productCategory;
        this.supplierDeliveringMethod = supplierDeliveringMethod;

        this.supplierContracts = new ArrayList<>();

        this.supplierContactInfo = supplierContactInfo;

        this.supplierPaymentInfo = supplierPaymentInfo;

        this.supplierDTO = new SupplierDTO(this.supplierId, this.supplierName, this.productCategory.toString(), this.supplierDeliveringMethod.toString(),
                supplierContactInfo.name, supplierContactInfo.email, supplierContactInfo.phoneNumber, supplierContactInfo.address,
                supplierPaymentInfo.supplierBankAccount.toString(), supplierPaymentInfo.supplierPaymentMethod.toString(), "temp");
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
        this.supplierDTO.supplierName = supplierName;
    }

    public ProductCategory getSupplierProductCategory() {
        return productCategory;
    }

    public void setSupplierProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
        this.supplierDTO.productCategory = productCategory.toString();
    }

    public abstract SupplyMethod getSupplyMethod();

    public DeliveringMethod getSupplierDeliveringMethod() {
        return supplierDeliveringMethod;
    }

    public void setSupplierDeliveringMethod(DeliveringMethod supplierDeliveringMethod) {
        this.supplierDeliveringMethod = supplierDeliveringMethod;
        this.supplierDTO.deliveryMethod = supplierDeliveringMethod.toString();
    }

    public void addSupplierContract(SupplyContract supplierContract) {
        this.supplierContracts.add(supplierContract);
    }

    public ArrayList<SupplyContract> getSupplierContracts() {
        return supplierContracts;
    }

    public ContactInfo getSupplierContactInfo() {
        return supplierContactInfo;
    }

    public void setSupplierContactInfo(ContactInfo supplierContactInfo) {
        this.supplierContactInfo = supplierContactInfo;
        this.supplierDTO.contactName = supplierContactInfo.name.toString();
        this.supplierDTO.emailAddress = supplierContactInfo.email;
        this.supplierDTO.phoneNumber = supplierContactInfo.phoneNumber;
        this.supplierDTO.address = supplierContactInfo.address;
    }

    public PaymentInfo getSupplierPaymentInfo() {
        return supplierPaymentInfo;
    }

    public void setSupplierPaymentInfo(PaymentInfo supplierPaymentInfo) {
        this.supplierPaymentInfo = supplierPaymentInfo;
        this.supplierDTO.bankAccount = supplierPaymentInfo.supplierBankAccount;
        this.supplierDTO.paymentMethod = supplierPaymentInfo.supplierPaymentMethod.toString();
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