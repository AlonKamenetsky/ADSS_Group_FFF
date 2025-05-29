package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.WeekDay;

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
}
