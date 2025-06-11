package Transportation.Domain;

public enum TruckType {
    SMALL, MEDIUM, LARGE;

    public static TruckType fromString(String s) {
        for (TruckType type : TruckType.values()) {
            if (type.name().equalsIgnoreCase(s)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid truck type. Please enter Small, Medium, or Large.");
    }
}