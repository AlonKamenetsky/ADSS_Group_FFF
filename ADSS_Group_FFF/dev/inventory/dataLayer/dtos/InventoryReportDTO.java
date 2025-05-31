package inventory.dataLayer.dtos;

import java.util.Date;
import java.util.List;

public class InventoryReportDTO {
    private String id;
    private Date dateGenerated;
    private List<InventoryProductDTO> items;

    public InventoryReportDTO() {
        // Default constructor
    }

    public InventoryReportDTO(String id, Date dateGenerated, List<InventoryProductDTO> items) {
        this.id = id;
        this.dateGenerated = dateGenerated;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateGenerated() {
        return dateGenerated;
    }

    public void setDateGenerated(Date dateGenerated) {
        this.dateGenerated = dateGenerated;
    }

    public List<InventoryProductDTO> getItems() {
        return items;
    }

    public void setItems(List<InventoryProductDTO> items) {
        this.items = items;
    }
}
