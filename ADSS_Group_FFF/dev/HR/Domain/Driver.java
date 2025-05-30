//package HR.Domain;
//
//import Transportation.Domain.LicenseType;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class Driver extends Employee{
//    private final ArrayList<LicenseType> licenseTypes;
//    private boolean isAvailable;
//
//    public Driver(String ID,
//                  List<Role> roles,
//                  String name,
//                  String password,
//                  String bankAccount,
//                  Float salary,
//                  Date employmentDate, LicenseType _licenseType){
//        super(String ID,
//                List<Role> roles,
//                String name,
//                String password,
//                String bankAccount,
//                Float salary,
//                Date employmentDate);
//        licenseTypes = new ArrayList<>(3);
//        licenseTypes.add(_licenseType);
//        isAvailable = true;
//    }
//
//    public String getDriverId() {
//        return driverId;
//    }
//
//    public boolean hasLicenseType(LicenseType _licenseType) {
//        return licenseTypes.contains(_licenseType);
//    }
//
//    public void addLicense(LicenseType _licenseType) {
//        if (_licenseType == null) {
//            throw new IllegalArgumentException("License cannot be null");
//        } else if (!licenseTypes.contains(_licenseType)) {
//            licenseTypes.add(_licenseType);
//        }
//    }
//    public boolean isAvailable() {
//        return isAvailable;
//    }
//
//    public void setAvailability(boolean status) {
//        isAvailable = status;
//    }
//
//    public String getName() {
//        return driverName;
//    }
//
//    public String toString() {
//        return String.format(
//                "Driver ID: %s\nName: %s\nLicense Types: %s\nAvailable: %s",
//                driverId,
//                driverName,
//                licenseTypes.toString(),
//                isAvailable ? "Yes" : "No"
//        );
//    }
//}