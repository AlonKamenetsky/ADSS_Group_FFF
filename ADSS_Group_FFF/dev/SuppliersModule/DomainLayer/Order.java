package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.SupplyMethod;

import java.time.LocalDate;
import java.util.ArrayList;

public class Order {
    int orderID;
    int supplierID;
    ContactInfo supplierContactInfo;

    LocalDate orderDate;
    LocalDate supplyDate;

    double totalPrice;

    SupplyMethod supplyMethod;

    ArrayList<Product> productArrayList;
}
