package inventory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;


public class InventoryServiceTest {
    private InventoryService service;
    private Category dairy;
    private InventoryItem milk;

    @BeforeEach
    public void setup() {
        service = new InventoryService();
        dairy = new Category("Dairy", null);
        service.addCategory(dairy);

        milk = new InventoryItem("001", "Milk", "Tnuva", 2, 1, 5,
                2.5, 5.0, ItemStatus.NORMAL, dairy);
        service.addItem(milk);
    }

    @Test
    public void testAddItem() {
        assertEquals(1, service.getAllItems().size());
    }

    @Test
    public void testLowStockDetection() {
        assertEquals(1, service.getLowStockItems().size());
    }

    @Test
    public void testUpdateQuantities() {
        service.updateQuantities("001", 5, 5);
        InventoryItem item = service.getAllItems().get(0);
        assertEquals(7, item.getShelfQuantity());
        assertEquals(6, item.getBackroomQuantity());
    }

    @Test
    public void testGenerateReportWithCategory() {
        InventoryReport report = service.generateReport("RPT001", Arrays.asList(dairy), null);
        assertEquals(1, report.getItems().size());
    }

    @Test
    public void testGetDiscountForItem() {
        Discount discount = new Discount("D1", 0.10,
                new Date(System.currentTimeMillis() - 1000 * 60 * 60),
                new Date(System.currentTimeMillis() + 1000 * 60 * 60),
                null, milk);
        service.addDiscount(discount);
        assertEquals(discount, service.getDiscountForItem(milk));
    }

    @Test
    public void testGetCategoryLevelDiscount() {
        Discount categoryDiscount = new Discount("D2", 0.20,
                new Date(System.currentTimeMillis() - 1000 * 60 * 60),
                new Date(System.currentTimeMillis() + 1000 * 60 * 60),
                dairy, null);
        service.addDiscount(categoryDiscount);
        assertEquals(categoryDiscount, service.getDiscountForItem(milk));
    }

    @Test
    public void testDamagedItemTracking() {
        milk.setStatus(ItemStatus.DAMAGED);
        assertEquals(ItemStatus.DAMAGED, milk.getStatus());
    }

    @Test
    public void testExpiredItemTracking() {
        milk.setStatus(ItemStatus.EXPIRED);
        assertEquals(ItemStatus.EXPIRED, milk.getStatus());
    }

    @Test
    public void testGenerateReportWithStatusFilter() {
        milk.setStatus(ItemStatus.EXPIRED);
        InventoryReport report = service.generateReport("RPT002", null, ItemStatus.EXPIRED);
        assertEquals(1, report.getItems().size());
    }

    @Test
    public void testAddDiscountToService() {
        Discount d = new Discount("X", 0.15,
                new Date(System.currentTimeMillis() - 1000),
                new Date(System.currentTimeMillis() + 1000),
                null, milk);
        service.addDiscount(d);
        assertEquals(d, service.getDiscountForItem(milk));
    }

    @Test
    public void testEmptyReportWhenNoMatch() {
        InventoryReport report = service.generateReport("NONE", Collections.emptyList(), ItemStatus.DAMAGED);
        assertEquals(0, report.getItems().size());
    }

    @Test
    public void testReportFilterByCategoryAndStatus() {
        milk.setStatus(ItemStatus.EXPIRED);
        InventoryReport report = service.generateReport("COMBO", Arrays.asList(dairy), ItemStatus.EXPIRED);
        assertEquals(1, report.getItems().size());
    }

}