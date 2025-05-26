package Transportation.DTO;

/**
 * Data Transfer Object representing a single item.
 */

public record ItemDTO(
        Integer itemId,
        String itemName,
        float itemWeight
) {
}