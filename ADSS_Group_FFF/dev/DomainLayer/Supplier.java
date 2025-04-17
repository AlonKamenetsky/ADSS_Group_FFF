package DomainLayer;

import DomainLayer.Enums.DeliveringMethod;

public abstract class Supplier {
    String supplierName;
    int supplierId;

    DeliveringMethod supplierDeliveringMethod;

    SupplyContract supplierContract;

    ContactInfo supplierContactInfo;

    PaymentInfo supplierPaymentInfo;
}