package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.DeliveringMethod;
import SuppliersModule.DomainLayer.Enums.WeekDay;

import java.util.ArrayList;

public class OnDemandSupplier extends Supplier {

    public OnDemandSupplier(int supplierId, String supplierName, DeliveringMethod supplierDeliveringMethod, SupplyContract supplierContract, ContactInfo supplierContactInfo, PaymentInfo supplierPaymentInfo) {
        super(supplierId, supplierName, supplierDeliveringMethod, supplierContract, supplierContactInfo, supplierPaymentInfo);
    }
}
