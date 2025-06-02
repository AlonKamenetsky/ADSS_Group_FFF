package SuppliersModule.DomainLayer;

import SuppliersModule.DataLayer.ScheduledOrderDataDTO;
import SuppliersModule.DataLayer.SupplierDaysDTO;
import SuppliersModule.DomainLayer.Enums.WeekDay;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class ScheduledOrder {
    private int supplierID;
    private WeekDay day;
    private Set<OrderProductData> productsData;

    public ScheduledOrder(int supplierID, WeekDay day, ArrayList<OrderProductData> productsData) {
        this.supplierID = supplierID;

        this.day = day;

        this.productsData = new HashSet<>();
        this.productsData.addAll(productsData);

    }

    public ScheduledOrder(int supplierID, WeekDay day) {
        this.supplierID = supplierID;

        this.day = day;

        this.productsData = new HashSet<>();
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public WeekDay getDay() {
        return day;
    }

    public void setDay(WeekDay day) {
        this.day = day;
    }

    public Set<OrderProductData> getProductsData() {
        return productsData;
    }

    public void setProductsData(Set<OrderProductData> productsData) {
        this.productsData = productsData;
    }

    public void addProductsData(Set<OrderProductData> productsData) {

        this.productsData.addAll(productsData);
    }

    public void Insert() {
        for (OrderProductData odp : productsData) {
            ScheduledOrderDataDTO scheduledOrderDataDTO = new ScheduledOrderDataDTO(supplierID, day.toString(), odp.getProductID(), odp.getProductQuantity());
            scheduledOrderDataDTO.Insert();
        }
    }

    @Override
    public String toString() {
        return "Day:" + this.day + " Products: " + this.productsData.toString();
    }
}
