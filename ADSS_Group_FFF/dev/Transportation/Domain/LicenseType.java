package Transportation.Domain;

public enum LicenseType {
    B, C, C1;

    public static LicenseType fromString(String _licenseType) {
        for (LicenseType type : LicenseType.values()) {
            if (type.name().equalsIgnoreCase(_licenseType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("License does not exist");
    }
}