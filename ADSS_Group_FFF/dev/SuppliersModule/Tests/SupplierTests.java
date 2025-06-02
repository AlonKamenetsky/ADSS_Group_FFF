//package SuppliersModule.Tests;
//
//import SuppliersModule.DomainLayer.*;
//import SuppliersModule.DomainLayer.Enums.*;
//
//import org.junit.jupiter.api.Test;
//
//import java.time.DayOfWeek;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class SupplierTests {
//
//    private static ContactInfo ci() {
//        return new ContactInfo("050-1111111", "Any St", "a@b.c", "Al");
//    }
//    private static PaymentInfo pi() {
//        return new PaymentInfo("IL00-0000-0000-0000-000", PaymentMethod.CASH);
//    }
//
//    @Test
//    void onDemand_supplier_has_correct_method() {
//        OnDemandSupplier s = new OnDemandSupplier(
//                9, "QuickBox", ProductCategory.DRINKS,
//                DeliveringMethod.PICK_UP, ci(), pi());
//        assertEquals(SupplyMethod.ON_DEMAND, s.getSupplyMethod());
//    }
//
//    @Test
//    void onDemand_toString_contains_name() {
//        OnDemandSupplier s = new OnDemandSupplier(
//                9, "QuickBox", ProductCategory.DRINKS,
//                DeliveringMethod.PICK_UP, ci(), pi());
//        assertTrue(s.toString().contains("QuickBox"));
//    }
//
//    @Test
//    void scheduled_supplier_has_correct_method() {
//        ScheduledSupplier s = new ScheduledSupplier(
//                1, "Weekly", ProductCategory.DAIRY,
//                DeliveringMethod.SELF_DELIVERING, ci(), pi(),
//                EnumSet.of(WeekDay.Friday));
//        assertEquals(SupplyMethod.SCHEDULED, s.getSupplyMethod());
//    }
//
//    @Test
//    void scheduled_supplier_contains_day_friday() {
//        ScheduledSupplier s = new ScheduledSupplier(
//                2, "Weekly", ProductCategory.DAIRY,
//                DeliveringMethod.SELF_DELIVERING, ci(), pi(),
//                EnumSet.of(WeekDay.Friday));
//        assertTrue(s.getSupplyDays().contains(WeekDay.Friday));
//    }
//
//    @Test
//    void nearest_weekday_date_returns_correct_day() {
//        Date monday = ScheduledSupplier.getNearestWeekdayDate(WeekDay.Monday);
//        DayOfWeek dow = ZonedDateTime.ofInstant(monday.toInstant(),
//                ZoneId.systemDefault()).getDayOfWeek();
//        assertEquals(DayOfWeek.MONDAY, dow);
//    }
//
//    @Test
//    void add_contract_increases_contract_list() {
//        OnDemandSupplier s = new OnDemandSupplier(
//                3, "Foo", ProductCategory.DRIED,
//                DeliveringMethod.PICK_UP, ci(), pi());
//        s.addSupplierContract(new SupplyContract(3, 10));
//        assertEquals(1, s.getSupplierContracts().size());
//    }
//
//    @Test
//    void add_scheduled_order_records_entry() {
//        ScheduledSupplier s = new ScheduledSupplier(
//                4, "Bar", ProductCategory.FROZEN,
//                DeliveringMethod.SELF_DELIVERING, ci(), pi(),
//                EnumSet.of(WeekDay.Wednesday));
//        WeekDay day = WeekDay.Wednesday;
//        s.addScheduledOrder(day,
//                new ScheduledOrder(4, day,
//                        new ArrayList<>(List.of(new OrderProductData(1, 1, 1, 1)))));
//        assertTrue(s.getScheduledOrders().containsKey(day));
//    }
//}
