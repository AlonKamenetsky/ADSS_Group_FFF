package Transportation.Domain;

import java.util.ArrayList;

public class Driver {
    private final String driverId;
    private final String driverName;
    private final ArrayList<LicenseType> licenseTypes;
    private boolean isAvailable;

    public Driver(String _driverId, String _driverName, LicenseType _licenseType) {
        driverId = _driverId;
        driverName = _driverName;
        licenseTypes = new ArrayList<>(3);
        licenseTypes.add(_licenseType);
        isAvailable = true;
    }

    public String getDriverId() {
        return driverId;
    }

    public boolean hasLicenseType(LicenseType _licenseType) {
        return licenseTypes.contains(_licenseType);
    }

    public void addLicense(LicenseType _licenseType) {
        if (_licenseType == null) {
            throw new IllegalArgumentException("License cannot be null");
        } else if (!licenseTypes.contains(_licenseType)) {
            licenseTypes.add(_licenseType);
        }
    }
    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailability(boolean status) {
        isAvailable = status;
    }

    public String getName() {
        return driverName;
    }

    public String toString() {
        return String.format(
                "Driver ID: %s\nName: %s\nLicense Types: %s\nAvailable: %s",
                driverId,
                driverName,
                licenseTypes.toString(),
                isAvailable ? "Yes" : "No"
        );
    }
}