package DomainLayer;

import DomainLayer.Enums.SupplyMethod;

import java.time.LocalDate;

public class Order {
    int orderID;
    int supplierID;
    ContactInfo supplierContactInfo;

    LocalDate orderDate;
    LocalDate supplyDate;

    double totalPrice;

    SupplyMethod supplyMethod;
}
