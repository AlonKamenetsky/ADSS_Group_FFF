package inventoryTests;

import inventory.domainLayer.ItemStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemStatusTest {

    @Test
    void enumContainsExpectedValues() {
        ItemStatus[] vals = ItemStatus.values();
        assertArrayEquals(new ItemStatus[]{ItemStatus.NORMAL, ItemStatus.DAMAGED, ItemStatus.EXPIRED}, vals);
    }
}
