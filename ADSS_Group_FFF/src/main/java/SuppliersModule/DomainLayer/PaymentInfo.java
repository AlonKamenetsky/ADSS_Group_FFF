package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.PaymentMethod;

public class PaymentInfo {
    String supplierBankAccount;
    PaymentMethod supplierPaymentMethod;

    public PaymentInfo(String supplierBankAccount, PaymentMethod supplierPaymentMethod) {
        this.supplierBankAccount = supplierBankAccount;
        this.supplierPaymentMethod = supplierPaymentMethod;
    }
    public String getSupplierBankAccount() {
        return supplierBankAccount;
    }
    public void setSupplierBankAccount(String supplierBankAccount) {
        this.supplierBankAccount = supplierBankAccount;
    }
    public PaymentMethod getSupplierPaymentMethod() {
        return supplierPaymentMethod;
    }
    public void setSupplierPaymentMethod(PaymentMethod supplierPaymentMethod) {
        this.supplierPaymentMethod = supplierPaymentMethod;
    }
    public String toString(){
        return supplierBankAccount + "\t" + supplierPaymentMethod + "\t";
    }
}
