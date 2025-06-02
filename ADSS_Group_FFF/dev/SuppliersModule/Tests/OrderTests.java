package SuppliersModule.Tests;

import SuppliersModule.DomainLayer.*;
import SuppliersModule.DomainLayer.Enums.*;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderAndOrderProductDataTests {

    /* ---------- helpers ---------- */

    private static ContactInfo ci() {
        return new ContactInfo("050-0000000", "Nowhere 1", "x@y.z", "Dana");
    }

    /* ---------- OrderProductData ---------- */

    @Test
    void orderProduct_totalPrice_is_quantity_times_unit() {
        OrderProductData d = new OrderProductData(5, 99, 4, 2.5);
        assertEquals(10.0, d.getTotalPrice(), 1e-9);
    }

    @Test
    void orderProduct_setters_change_quantity_and_price() {
        OrderProductData d = new OrderProductData(1, 123, 2, 3);
        d.setProductQuantity(5);
        d.setProductPrice(4);
        String expected = "ProductID: 123 ProductQuantity: 5 ProductPrice: 4.0";
        assertEquals(expected, d.toString());
    }

    /* ---------- Order ---------- */

    @Test
    void order_constructor_populates_all_fields() {
        List<OrderProductData> items = List.of(new OrderProductData(1, 10, 1, 1));
        Order o = new Order(77, 8, new ArrayList<>(items), 0, new Date(), new Date() ,DeliveringMethod.PICK_UP, SupplyMethod.ON_DEMAND, ci());

        assertAll(
                () -> assertEquals(77, o.getOrderID()),
                () -> assertEquals(SupplyMethod.ON_DEMAND, o.getSupplyMethod()),
                () -> assertEquals(1,  o.getProductArrayList().size())
        );
    }

    @Test
    void order_add_product_increases_list_size() {
        Order o = new Order(77, 8, null, 0, new Date(), new Date() ,DeliveringMethod.PICK_UP, SupplyMethod.ON_DEMAND, ci());

        o.addOrderProductData(new OrderProductData(1, 200, 1, 3));
        assertEquals(1, o.getProductArrayList().size());
    }

    @Test
    void order_total_price_can_be_set() {
        Order o = new Order(77, 8, null, 0, new Date(), new Date() ,DeliveringMethod.PICK_UP, SupplyMethod.ON_DEMAND, ci());

        o.setTotalPrice(123.45);
        assertEquals(123.45, o.getTotalPrice(), 1e-9);
    }

    @Test
    void order_status_mutator_changes_state() {
        Order o = new Order(77, 8, null, 0, new Date(), new Date() ,DeliveringMethod.PICK_UP, SupplyMethod.ON_DEMAND, ci());

        o.setOrderStatus(OrderStatus.IN_PROCESS);
        assertEquals(OrderStatus.IN_PROCESS, o.getOrderStatus());
    }

    /* ---------- ScheduledOrder ---------- */

    @Test
    void scheduledOrder_add_products_merges_sets() {
        ArrayList<OrderProductData> seed = new ArrayList<>(
                List.of(new OrderProductData(1, 11, 1, 1)));
        ScheduledOrder so = new ScheduledOrder(5, WeekDay.Monday, seed);
        so.addProductsData(Set.of(new OrderProductData(1, 12, 1, 1)));
        assertEquals(2, so.getProductsData().size());
    }
}
