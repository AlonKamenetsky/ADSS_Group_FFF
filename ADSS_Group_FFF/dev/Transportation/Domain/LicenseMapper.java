package Transportation.Domain;

public class LicenseMapper {
    public static LicenseType getRequiredLicense(TruckType truckType) {
        return switch (truckType) {
            case SMALL -> LicenseType.B;
            case MEDIUM -> LicenseType.C;
            case LARGE -> LicenseType.C1;
            default -> throw new IllegalArgumentException("Unknown truck type");
        };
    }
}
