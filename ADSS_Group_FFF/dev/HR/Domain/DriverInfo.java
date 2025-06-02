package HR.Domain;

import java.util.ArrayList;
import java.util.List;

public class DriverInfo {
    private final String employeeId;
    private List<LicenseType> licenseType;
    private boolean isAvaiable;

    public enum LicenseType {
        B, C, C1;
    }

    public DriverInfo(String employeeId, List<LicenseType> licenseType) {
        this.employeeId = employeeId;
        this.licenseType = licenseType;
    }

    public boolean getAvailable() { return isAvaiable; }

    public void setAvaiable(boolean status) { isAvaiable = status;}

    public void setLicenses(ArrayList<LicenseType> licenseTypes) {
        this.licenseType = licenseTypes;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public List<LicenseType> getLicenses() {
        return licenseType;
    }

    public void AddLicenseType(LicenseType licenseType) {
        this.licenseType.add(licenseType);
    }

    public void RemoveLicenseType(LicenseType licenseType) {this.licenseType.remove(licenseType);}

    @Override
    public String toString() {
        return "DriverInfo{" +
                "employeeId='" + employeeId + '\'' +
                ", licenseType='" + licenseType + '\'' +
                '}';
    }

}
