//package Transportation.Service;
//
//import javax.management.InstanceAlreadyExistsException;
//import java.util.NoSuchElementException;
//
//public class DriverService {
//    private final DriverManager driverManager;
//
//    public DriverService(DriverManager driverManager) {
//        this.driverManager = driverManager;
//    }
//
//    public void AddDriver(String _driverId, String _driverName, String _licenseType) throws IllegalArgumentException, NullPointerException, InstanceAlreadyExistsException {
//        if (_driverId == null || _driverName == null || _licenseType == null) {
//            throw new NullPointerException();
//        }
//        driverManager.addDriver(_driverId, _driverName.toLowerCase(), _licenseType);
//    }
//
//    public String getDriverById(String _driverId) throws NullPointerException {
//        if (_driverId == null) {
//            throw new NullPointerException();
//        }
//        if (driverManager.doesDriverExist(_driverId)) {
//            return driverManager.getDriverById(_driverId).toString();
//        }
//        return "Driver doesn't exist";
//    }
//
//    public void deleteDriver(String _driverId) throws NullPointerException, NoSuchElementException {
//        if (_driverId == null) {
//            throw new NullPointerException();
//        }
//        if (driverManager.doesDriverExist(_driverId)) {
//            driverManager.removeDriver(_driverId);
//        }
//        else {
//            throw new NoSuchElementException();
//        }
//    }
//
//    public void AddLicense(String _driverId, String _licenseType) {
//        if (_driverId == null || _licenseType == null) {
//            return;
//        }
//        if(!hasLicense(_driverId, _licenseType)) {
//            driverManager.addLicense(_driverId, _licenseType);
//        }
//    }
//
//    public boolean hasLicense(String driverId, String licenseType) {
//        if (driverId == null || licenseType == null) {
//            return false;
//        }
//        return driverManager.hasLicenseType(driverId, licenseType);
//    }
//
//    public boolean DriverHasAvailable(String _driverId) {
//        if (_driverId == null) {
//            return false;
//        }
//        return driverManager.doesDriverExist(_driverId);
//    }
//
//    public String viewAllDrivers() {
//        return driverManager.getAllDriversString();
//    }
//
//    public void ChangeDriverAvailability(String driverId, boolean status) throws NoSuchElementException {
//        driverManager.setDriverAvailability(driverId, status);
//    }
//}