package Transportation.Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class DriverManager {
    private final HashMap<String, Driver> allDrivers;

    public DriverManager() {
        allDrivers = new HashMap<>();
    }

    public void addDriver(String _driverId, String _driverName, String _licenseType) throws IllegalArgumentException, NullPointerException {
        if (_driverId == null || _driverName == null || _licenseType == null) {
            throw new NullPointerException();
        }
        LicenseType type = LicenseType.fromString(_licenseType);
        Driver newDriver = new Driver(_driverId, _driverName, type);
        allDrivers.putIfAbsent(_driverId, newDriver);
    }

    public void removeDriver(String driverId) {
        allDrivers.remove(driverId);
    }

    public Driver getDriverById(String driverId) {
        return allDrivers.get(driverId);
    }

    public boolean hasLicenseType(String driverId, String licenseType) {
        Driver currDriver = allDrivers.get(driverId);
        return currDriver.hasLicenseType(LicenseType.fromString(licenseType));
    }

    public void addLicense(String driverId, String _licenseType) {
        Driver driverToAdd = allDrivers.get(driverId);
        LicenseType license = LicenseType.fromString(_licenseType);
        if (!driverToAdd.hasLicenseType(license)) {
            driverToAdd.addLicense(license);
        }
    }

    public boolean doesDriverExist(String _driverId) {
        return allDrivers.containsKey(_driverId);
    }

    public List<Driver> getAllDrivers() {
        return new ArrayList<>(allDrivers.values());

    }

    public void setDriverAvailability(String driverId, boolean status) throws NoSuchElementException {
        Driver driverToChange = allDrivers.get(driverId);
        if (driverId != null) {
            driverToChange.setAvailability(status);
            return;
        }
        throw new NoSuchElementException();
    }

    public String getAllDriversString() {
        List<Driver> allDrivers = getAllDrivers();
        if (allDrivers.isEmpty()) return "No drivers available.";

        StringBuilder sb = new StringBuilder("All Drivers:\n");
        for (Driver d : allDrivers) {
            sb.append(d).append("\n----------------------\n");
        }
        return sb.toString();
    }

    public Driver getAvailableDriverByLicense(String licenseType) {
        for (Driver d : allDrivers.values()) {
            if (d.isAvailable() && d.hasLicenseType(LicenseType.valueOf(licenseType))) {
                return d;
            }
        }
        return null;
    }
}