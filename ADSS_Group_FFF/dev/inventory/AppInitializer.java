package inventory;

import java.util.Date;

public class AppInitializer {
    public static void loadSampleData(InventoryService service) {
        Category dairy = new Category("Dairy", null);
        Category milk = new Category("Milk", dairy);
        Category yogurt = new Category("Yogurt", dairy);
        dairy.addSubCategory(milk);
        dairy.addSubCategory(yogurt);

        Category snacks = new Category("Snacks", null);
        service.addCategory(dairy);
        service.addCategory(snacks);

        InventoryItem milkItem = new InventoryItem(
                "001", "Tnuva Milk 1L", "Tnuva",
                4, 2, 10, 2.40, 4.90, ItemStatus.NORMAL, milk
        );

        InventoryItem choco = new InventoryItem(
                "002", "Choco Milk", "Tnuva",
                2, 1, 6, 1.90, 3.90, ItemStatus.EXPIRED, milk
        );

        InventoryItem yogurtItem = new InventoryItem(
                "003", "Yolo Yogurt", "Strauss",
                5, 0, 4, 1.50, 3.00, ItemStatus.DAMAGED, yogurt
        );

        InventoryItem chips = new InventoryItem(
                "004", "Doritos", "Osem",
                10, 5, 8, 3.50, 6.50, ItemStatus.NORMAL, snacks
        );

        service.addItem(milkItem);
        service.addItem(choco);
        service.addItem(yogurtItem);
        service.addItem(chips);

        service.addDiscount(new Discount(
                "D10", 0.10,
                new Date(System.currentTimeMillis() - 100000),
                new Date(System.currentTimeMillis() + 100000),
                null, milkItem
        ));

        service.addDiscount(new Discount(
                "C20", 0.20,
                new Date(System.currentTimeMillis() - 100000),
                new Date(System.currentTimeMillis() + 100000),
                snacks, null
        ));
    }
}