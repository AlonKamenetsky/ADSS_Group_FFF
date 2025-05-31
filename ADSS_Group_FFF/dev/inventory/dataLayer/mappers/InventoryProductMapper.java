package inventory.dataLayer.mappers;

import inventory.domainLayer.Category;
import inventory.domainLayer.InventoryProduct;
import inventory.domainLayer.ItemStatus;
import inventory.dataLayer.dtos.InventoryProductDTO;

public class InventoryProductMapper {
    public static InventoryProductDTO toDTO(InventoryProduct product) {
        return new InventoryProductDTO(
                product.getId(),
                product.getName(),
                product.getManufacturer(),
                product.getShelfQuantity(),
                product.getBackroomQuantity(),
                product.getMinThreshold(),
                product.getPurchasePrice(),
                product.getSalePrice(),
                product.getStatus().toString(),
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }

    public static InventoryProduct fromDTO(InventoryProductDTO dto, Category category) {
        return new InventoryProduct(
                dto.getId(),
                dto.getName(),
                dto.getManufacturer(),
                dto.getShelfQuantity(),
                dto.getBackroomQuantity(),
                dto.getMinThreshold(),
                dto.getPurchasePrice(),
                dto.getSalePrice(),
                ItemStatus.valueOf(dto.getStatus()),
                category // must be looked up by name before calling this
        );
    }
}

