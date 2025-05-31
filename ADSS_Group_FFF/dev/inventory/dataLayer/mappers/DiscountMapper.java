package inventory.dataLayer.mappers;

import inventory.domainLayer.Category;
import inventory.domainLayer.Discount;
import inventory.domainLayer.InventoryProduct;
import inventory.dataLayer.dtos.DiscountDTO;

public class DiscountMapper {

    public static DiscountDTO toDTO(Discount discount) {
        return new DiscountDTO(
                discount.getId(),
                discount.getPercent(),
                discount.getStartDate(),
                discount.getEndDate(),
                discount.getAppliesToCategory() != null ? discount.getAppliesToCategory().getName() : null,
                discount.getAppliesToItem() != null ? discount.getAppliesToItem().getName() : null
        );
    }

    public static Discount fromDTO(DiscountDTO dto, Category category, InventoryProduct item) {
        return new Discount(
                dto.getId(),
                dto.getPercent(),
                dto.getStartDate(),
                dto.getEndDate(),
                category,
                item
        );
    }
}
