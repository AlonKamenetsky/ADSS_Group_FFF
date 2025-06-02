package inventory;

import inventory.domainLayer.*;
import inventory.serviceLayer.InventoryService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;


public class InventoryServiceTest {
    private InventoryService service;
    private Category dairy;
    private InventoryProduct milk;

    @BeforeEach
    public void setup() {
        service = InventoryService.getInstance();
        service.addCategory("dairy",null);

        service.addProduct(777, "Milk", "Tnuva", 2, 1, 5,
                2.5, 5.0, ItemStatus.NORMAL, "dairy");
    }

    @Test
    public void testAddItem() {
        assertEquals(1,service.getAllProducts().size());
    }

    @Test
    public void testLowStockDetection() {
        assertEquals(1, service.getLowStockItems().size());
    }


}