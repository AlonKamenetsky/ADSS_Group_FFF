package inventory;

import java.util.Date;

public class AppInitializer {
    public static void loadSampleData(InventoryService controller) {
        // Categories
        Category dairy = new Category("Dairy", null);
        Category milk = new Category("Milk", dairy);
        Category yogurt = new Category("Yogurt", dairy);
        dairy.addSubCategory(milk);
        dairy.addSubCategory(yogurt);

        Category snacks = new Category("Snacks", null);
        Category drinks = new Category("Drinks", null);
        Category toiletries = new Category("Toiletries", null);
        Category shampoo = new Category("Shampoo", toiletries);
        Category conditioner = new Category("Conditioner", toiletries);
        toiletries.addSubCategory(shampoo);
        toiletries.addSubCategory(conditioner);

        controller.addCategory(dairy);
        controller.addCategory(snacks);
        controller.addCategory(drinks);
        controller.addCategory(toiletries);

        // Items
        controller.addItem(new InventoryItem("001", "Tnuva Milk 1L", "Tnuva", 4, 2, 10, 2.40, 4.90, ItemStatus.NORMAL, milk));
        controller.addItem(new InventoryItem("002", "Choco Milk", "Tnuva", 2, 1, 6, 1.90, 3.90, ItemStatus.EXPIRED, milk));
        controller.addItem(new InventoryItem("003", "Yolo Yogurt", "Strauss", 5, 0, 4, 1.50, 3.00, ItemStatus.DAMAGED, yogurt));
        controller.addItem(new InventoryItem("004", "Doritos", "Osem", 10, 5, 8, 3.50, 6.50, ItemStatus.NORMAL, snacks));
        controller.addItem(new InventoryItem("005", "Tapuchips BBQ", "Osem", 8, 2, 7, 3.00, 6.00, ItemStatus.NORMAL, snacks));
        controller.addItem(new InventoryItem("006", "Tnuva Cheese 250g", "Tnuva", 3, 3, 5, 4.00, 7.50, ItemStatus.NORMAL, dairy));
        controller.addItem(new InventoryItem("007", "Prigat Orange Juice 1L", "Prigat", 6, 1, 5, 2.00, 5.00, ItemStatus.NORMAL, drinks));
        controller.addItem(new InventoryItem("008", "Milky Chocolate", "Strauss", 5, 2, 6, 2.20, 4.00, ItemStatus.NORMAL, yogurt));
        controller.addItem(new InventoryItem("009", "Dove Shampoo 400ml", "Dove", 7, 2, 10, 8.00, 15.00, ItemStatus.NORMAL, shampoo));
        controller.addItem(new InventoryItem("010", "Herbal Essence Conditioner", "Procter & Gamble", 5, 1, 5, 7.00, 14.00, ItemStatus.DAMAGED, conditioner));
        controller.addItem(new InventoryItem("011", "Strawberry Yogurt", "Strauss", 4, 1, 6, 1.60, 3.20, ItemStatus.NORMAL, yogurt));
        controller.addItem(new InventoryItem("012", "Prigat Apple Juice 1L", "Prigat", 3, 0, 5, 2.10, 5.20, ItemStatus.EXPIRED, drinks));
        controller.addItem(new InventoryItem("013", "Bamba", "Osem", 12, 6, 10, 2.00, 4.00, ItemStatus.NORMAL, snacks));

        // Discounts
        Date now = new Date();

        InventoryItem firstItem = controller.getAllItems().stream().findFirst().orElse(null);
        if (firstItem != null) {
            controller.addDiscount(new Discount(
                    "D10",
                    0.10,
                    new Date(now.getTime() - 100000),
                    new Date(now.getTime() + 10000000),
                    null,
                    firstItem
            ));
        }

        controller.addDiscount(new Discount(
                "C20",
                0.20,
                new Date(now.getTime() - 100000),
                new Date(now.getTime() + 10000000),
                snacks,
                null
        ));

        InventoryItem ninthItem = controller.getAllItems().stream()
                .filter(item -> item.getId().equals("009")) // Dove Shampoo
                .findFirst()
                .orElse(null);
        if (ninthItem != null) {
            controller.addDiscount(new Discount(
                    "D15",
                    0.15,
                    new Date(now.getTime() - 100000),
                    new Date(now.getTime() + 10000000),
                    null,
                    ninthItem
            ));
        }

        controller.addDiscount(new Discount(
                "C5",
                0.05,
                new Date(now.getTime() - 100000),
                new Date(now.getTime() + 10000000),
                drinks,
                null
        ));
    }
}
