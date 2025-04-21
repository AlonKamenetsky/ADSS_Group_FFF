package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.ProductCategory;
import SuppliersModule.DomainLayer.Enums.WeekDay;

import java.util.EnumSet;

public class ScheduledSupplier extends Supplier {
    private EnumSet<WeekDay> supplyDays;

    public ScheduledSupplier(int supplierId, String supplierName, ProductCategory productCategory, DeliveringMethod supplierDeliveringMethod, SupplyContract supplierContract, ContactInfo supplierContactInfo, PaymentInfo supplierPaymentInfo) {
        super(supplierId, supplierName, productCategory, supplierDeliveringMethod, supplierContract, supplierContactInfo, supplierPaymentInfo);
    }
}
