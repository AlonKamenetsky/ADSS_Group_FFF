package Transportation.DTO;

import Transportation.Domain.TruckType;

public record TruckDTO(
        Integer truckId,
        String  truckType,
        String licenseNumber,
        String model,
        float netWeight,
        float maxWeight,
        boolean isFree
) {
}