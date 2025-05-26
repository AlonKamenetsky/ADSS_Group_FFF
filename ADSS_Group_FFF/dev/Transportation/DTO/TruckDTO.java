package Transportation.DTO;

import Transportation.Domain.TruckType;

public record TruckDTO(
        int truckId,
        String licenseNumber,
        String model,
        TruckType truckType,
        float netWeight,
        float maxWeight,
        boolean isFree
) {
}