package Transportation.Domain;

import HR.Domain.DriverInfo;

public class LicenseMapper {
    public static DriverInfo.LicenseType getRequiredLicense(TruckType truckType) {
        return switch (truckType) {
            case SMALL -> DriverInfo.LicenseType.B;
            case MEDIUM -> DriverInfo.LicenseType.C;
            case LARGE -> DriverInfo.LicenseType.C1;
            default -> throw new IllegalArgumentException("Unknown truck type");
        };
    }
}
