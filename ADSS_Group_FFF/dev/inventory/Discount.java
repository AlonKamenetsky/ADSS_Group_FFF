package inventory;

import java.util.Date;

public class Discount {
    private String id;
    private double percent;
    private Date startDate;
    private Date endDate;
    private Category appliesToCategory;
    private InventoryItem appliesToItem;
}