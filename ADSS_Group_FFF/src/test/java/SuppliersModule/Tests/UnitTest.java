/* DomainLayerAllTests.java */
package SuppliersModule.DomainLayer;

import SuppliersModule.DomainLayer.Enums.*;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 25 lightweight, DB-free unit-tests for the Domain layer.
 */
class DomainLayerAllTests {

    /* ---------- helpers ---------- */

    private static ContactInfo contact() {
        return new ContactInfo("050-0000000", "Nowhere 1", "x@y.z", "Dana");
    }

    private static PaymentInfo payment() {
        return new PaymentInfo("IL00-1234-5678-0000-000", PaymentMethod.CHECK);
    }

    /* ---------- Product ---------- */

    @Test
    void t01_product_constructor_and_toString() {
        Product p = new Product(1, "Widget", "Acme", ProductCategory.DRIED);

        String expected = p.getProductId() + "\t" +
                p.getProductName() + "\t" +
                p.getProductCompanyName() + "\t" +
                p.getProductCategory();

        assertAll(
                () -> assertEquals(1, p.getProductId()),
                () -> assertEquals("Widget", p.getProductName()),
                () -> assertEquals("Acme", p.getProductCompanyName()),
                () -> assertEquals(ProductCategory.DRIED, p.getProductCategory()),
                () -> assertEquals(expected, p.toString())
        );
    }

    @Test
    void t02_product_setters_update_toString() {
        Product p = new Product(7, "Tmp", "Foo", ProductCategory.FROZEN);
        p.setProductName("Bolt");
        p.setProductCategory(ProductCategory.MEATS);

        String expected = p.getProductId() + "\t" +
                "Bolt" + "\t" +
                p.getProductCompanyName() + "\t" +
                ProductCategory.MEATS;

        assertEquals(expected, p.toString());
    }

    /* ---------- OrderProductData ---------- */

    @Test
    void t03_order_product_totalPrice() {
        OrderProductData d = new OrderProductData(1, 99, 4, 2.5);
        assertEquals(10.0, d.getTotalPrice(), 1e-9);
    }

    @Test
    void t04_order_product_setters_and_toString() {
        OrderProductData d = new OrderProductData(2, 123, 2, 3);
        d.setProductQuantity(5);
        d.setProductPrice(4);

        String expected = "ProductID: " + d.getProductID() +
                " ProductQuantity: " + d.getProductQuantity() +
                " ProductPrice: " + d.getProductPrice();

        assertAll(
                () -> assertEquals(20.0, d.getTotalPrice(), 1e-9),
                () -> assertEquals(expected, d.toString())
        );
    }

    @Test
    void t05_order_product_getters() {
        OrderProductData d = new OrderProductData(7, 321, 3, 1.5);
        assertAll(
                () -> assertEquals(7,  d.getOrderID()),
                () -> assertEquals(321,d.getProductID()),
                () -> assertEquals(3,  d.getProductQuantity()),
                () -> assertEquals(1.5,d.getProductPrice(), 1e-9)
        );
    }

    /* ---------- SupplyContractProductData ---------- */

    @Test
    void t06_supply_contract_product_data_mutators() {
        SupplyContractProductData d =
                new SupplyContractProductData(11, 200, 7.5, 100, 0.10);
        d.setProductPrice(8.0);
        d.setQuantityForDiscount(150);

        assertAll(
                () -> assertEquals(8.0, d.getProductPrice(), 1e-9),
                () -> assertEquals(150, d.getQuantityForDiscount())
        );
    }

    /* ---------- SupplyContract ---------- */

    @Test
    void t07_supply_contract_add_product_reflected_in_toString() {
        SupplyContract c = new SupplyContract(3, 42);
        SupplyContractProductData data =
                new SupplyContractProductData(42, 10, 1, 10, 0);
        c.addSupplyContractProductData(data);

        String expected =
                "SupplyContract {\n" +
                        "  Supplier ID: 3,\n" +
                        "  Contract ID: 42,\n" +
                        "  Product Data List:\n" +
                        "    " + data + "\n" +
                        "}";

        assertEquals(expected, c.toString());
    }

    /* ---------- Order ---------- */

//    @Test
//    void t08_order_constructor_fields() {
//        ArrayList<OrderProductData> prods = new ArrayList<>();
//        prods.add(new OrderProductData(1, 111, 2, 4));
//
//        Order o = new Order(
//                77,                      // orderID
//                8,                       // supplierID
//                prods,
//                null,null,
//                DeliveringMethod.PICK_UP,
//                SupplyMethod.ON_DEMAND,
//                contact());
//
//        // NB: getSupplierID() currently returns orderID (77)
//        assertAll(
//                () -> assertEquals(SupplyMethod.ON_DEMAND, o.getSupplyMethod()),
//                () -> assertEquals(77, o.getSupplierID()),
//                () -> assertEquals(1,  o.getProductArrayList().size())
//        );
//    }
//
//    @Test
//    void t09_order_add_product_updates_list() {
//        Order o = new Order(1, 2, new ArrayList<>(),
//                DeliveringMethod.PICK_UP,
//                SupplyMethod.ON_DEMAND,
//                contact());
//
//        o.addOrderProductData(new OrderProductData(1, 200, 2, 5));
//        assertEquals(1, o.getProductArrayList().size());
//    }
//
//    @Test
//    void t10_order_setters_modify_state() {
//        Order o = new Order(1, 2, new ArrayList<>(),
//                DeliveringMethod.PICK_UP,
//                SupplyMethod.ON_DEMAND,
//                contact());
//
//        o.setTotalPrice(123.45);
//        o.setOrderStatus(OrderStatus.IN_PROCESS);
//
//        assertEquals(123.45, o.getTotalPrice(), 1e-9);
//        // no public getter for status → rely on toString side-effect not required here
//    }

    /* ---------- ScheduledOrder ---------- */

    @Test
    void t11_scheduled_order_merges_products() {
        ArrayList<OrderProductData> seed = new ArrayList<>();
        seed.add(new OrderProductData(1, 11, 1, 1));

        ScheduledOrder so = new ScheduledOrder(5, WeekDay.Monday, seed);
        so.addProductsData(Set.of(new OrderProductData(1, 12, 1, 1)));

        assertEquals(2, so.getProductsData().size());
    }

    @Test
    void t12_scheduled_order_products_non_empty() {
        ScheduledOrder so = new ScheduledOrder(1, WeekDay.Tuesday,
                new ArrayList<>(List.of(new OrderProductData(1, 2, 1, 1))));
        assertFalse(so.getProductsData().isEmpty());
    }

    /* ---------- Suppliers ---------- */

    @Test
    void t13_on_demand_supplier_supply_method() {
        OnDemandSupplier s = new OnDemandSupplier(
                9, "QuickBox", ProductCategory.DRINKS,
                DeliveringMethod.PICK_UP, contact(), payment());

        assertEquals(SupplyMethod.ON_DEMAND, s.getSupplyMethod());
    }

    @Test
    void t14_on_demand_supplier_exact_toString() {
        OnDemandSupplier s = new OnDemandSupplier(
                9, "QuickBox", ProductCategory.DRINKS,
                DeliveringMethod.PICK_UP, contact(), payment());

        String expected = String.format(
                "Supplier ID: %d%n" +
                        "Name: %s%n" +
                        "Category: %s%n" +
                        "Delivery Method: %s%n" +
                        "Contact Info: %s%n" +
                        "Payment Info: %s%n" +
                        "Contracts: %s",
                9,
                "QuickBox",
                ProductCategory.DRINKS,
                DeliveringMethod.PICK_UP,
                contact(),
                payment(),
                new ArrayList<>()
        );

        assertEquals(expected, s.toString());
    }

    @Test
    void t15_scheduled_supplier_supply_method_and_days() {
        ScheduledSupplier s = new ScheduledSupplier(
                1, "EveryWeek", ProductCategory.DAIRY,
                DeliveringMethod.SELF_DELIVERING, contact(), payment(),
                EnumSet.of(WeekDay.Friday));

        String expectedCore = String.format(
                "Supplier ID: %d%n" +
                        "Name: %s%n" +
                        "Category: %s%n" +
                        "Delivery Method: %s%n" +
                        "Contact Info: %s%n" +
                        "Payment Info: %s%n" +
                        "Contracts: %s",
                1,
                "EveryWeek",
                ProductCategory.DAIRY,
                DeliveringMethod.SELF_DELIVERING,
                contact(),
                payment(),
                new ArrayList<>()
        );

        String expected = expectedCore + "\nAvailable days: " + s.getSupplyDays();

        assertAll(
                () -> assertEquals(SupplyMethod.SCHEDULED, s.getSupplyMethod()),
                () -> assertTrue(s.getSupplyDays().contains(WeekDay.Friday)),
                () -> assertEquals(expected, s.toString())
        );
    }

    @Test
    void t16_scheduled_supplier_nearest_weekday() {
        Date monday = ScheduledSupplier.getNearestWeekdayDate(WeekDay.Monday);
        DayOfWeek dow = ZonedDateTime.ofInstant(monday.toInstant(),
                ZoneId.systemDefault()).getDayOfWeek();

        assertEquals(DayOfWeek.MONDAY, dow);
    }

    @Test
    void t17_supplier_add_contract_increments_list() {
        OnDemandSupplier s = new OnDemandSupplier(
                3, "Foo", ProductCategory.DRIED,
                DeliveringMethod.PICK_UP, contact(), payment());
        s.addSupplierContract(new SupplyContract(3, 10));

        assertEquals(1, s.getSupplierContracts().size());
    }

    @Test
    void t18_scheduled_supplier_add_order_keeps_map_size() {
        ScheduledSupplier s = new ScheduledSupplier(
                4, "Bar", ProductCategory.FROZEN,
                DeliveringMethod.SELF_DELIVERING, contact(), payment(),
                EnumSet.of(WeekDay.Wednesday));

        WeekDay day = WeekDay.Wednesday;
        s.addScheduledOrder(day,
                new ScheduledOrder(4, day,
                        new ArrayList<>(List.of(new OrderProductData(1, 1, 1, 1)))));

        assertTrue(s.getScheduledOrders().containsKey(day));
    }

    /* ---------- PaymentInfo / ContactInfo ---------- */

    @Test
    void t19_payment_info_exact_toString() {
        String expected = payment().getSupplierBankAccount() + "\t" +
                payment().getSupplierPaymentMethod() + "\t";
        assertEquals(expected, payment().toString());
    }

    @Test
    void t20_contact_info_exact_toString() {
        String expected = contact().getPhoneNumber() + "\t" +
                contact().getAddress() + "\t" +
                contact().getEmail() + "\t" +
                contact().getName() + "\t";
        assertEquals(expected, contact().toString());
    }

    /* ---------- Enum sanity checks ---------- */

    @Test
    void t21_weekday_has_seven_values() {
        assertEquals(7, WeekDay.values().length);
    }

    @Test
    void t22_product_category_non_empty() {
        assertTrue(ProductCategory.values().length > 0);
    }

    @Test
    void t23_payment_method_contains_check() {
        assertTrue(Arrays.asList(PaymentMethod.values()).contains(PaymentMethod.CHECK));
    }

    @Test
    void t24_supply_method_two_values() {
        assertEquals(
                EnumSet.of(SupplyMethod.ON_DEMAND, SupplyMethod.SCHEDULED),
                EnumSet.copyOf(List.of(SupplyMethod.values()))
        );
    }

    @Test
    void t25_delivering_method_contains_pick_up() {
        assertTrue(Arrays.asList(DeliveringMethod.values()).contains(DeliveringMethod.PICK_UP));
    }
}
