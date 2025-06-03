package inventoryTests;

import inventory.domainLayer.InventoryProduct;
import inventory.domainLayer.InventoryReport;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryReportTest {

    @Test
    void constructorAndGetItems_shouldReturnCorrectList() {
        InventoryProduct p1 = new InventoryProduct(
                1, "Item1", "Mfr1", 10, 5, 2, 1.0, 2.0, null, null);
        InventoryProduct p2 = new InventoryProduct(
                2, "Item2", "Mfr2", 20, 10, 5, 5.0, 8.0, null, null);

        List<InventoryProduct> items = Arrays.asList(p1, p2);
        InventoryReport report = new InventoryReport("RPT1", new Date(), items);

        assertEquals(2, report.getItems().size());
        assertTrue(report.getItems().contains(p1));
        assertTrue(report.getItems().contains(p2));
    }

    @Test
    void toString_includesIdAndItemsCount() {
        InventoryReport report = new InventoryReport("R42", new Date(), null);
        String repr = report.toString();
        assertTrue(repr.contains("id='R42'"));
        // itemsCount should be 0 if list is null
        assertTrue(repr.contains("itemsCount=0"));
    }
}
