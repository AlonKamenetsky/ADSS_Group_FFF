package inventory.dataLayer.mappers;

import inventory.domainLayer.InventoryProduct;
import inventory.domainLayer.InventoryReport;
import inventory.dataLayer.dtos.InventoryReportDTO;
import inventory.dataLayer.dtos.InventoryProductDTO;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryReportMapper {

    public static InventoryReportDTO toDTO(InventoryReport report) {
        List<InventoryProductDTO> dtoItems = report.getItems().stream()
                .map(InventoryProductMapper::toDTO)
                .collect(Collectors.toList());

        return new InventoryReportDTO(
                report.getId(),
                report.getDateGenerated(),
                dtoItems
        );
    }

    public static InventoryReport fromDTO(InventoryReportDTO dto, List<InventoryProduct> resolvedItems) {
        return new InventoryReport(
                dto.getId(),
                dto.getDateGenerated(),
                resolvedItems
        );
    }
}
