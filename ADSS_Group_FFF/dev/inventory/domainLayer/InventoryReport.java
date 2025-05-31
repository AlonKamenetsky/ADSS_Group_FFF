package inventory.domainLayer;


import java.util.Date;
import java.util.List;

public class InventoryReport {
    private String id;
    private Date dateGenerated;
    private List<InventoryProduct> items;

    public InventoryReport(String id, Date dateGenerated, List<InventoryProduct> items) {
        this.id = id;
        this.dateGenerated = dateGenerated;
        this.items = items;
    }


    public String getId() {
        return id;
    }

    public Date getDateGenerated() {
        return dateGenerated;
    }

    public List<InventoryProduct> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "InventoryReport{" +
                "id='" + id + '\'' +
                ", date=" + dateGenerated +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}

