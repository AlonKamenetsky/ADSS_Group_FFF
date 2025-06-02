package HR.Domain;

import Transportation.Domain.LicenseMapper;
import Transportation.Domain.LicenseType;

import java.util.ArrayList;
import java.util.List;

public class DriverInfo {
    private final String employeeId;
    private List<LicenseType> licenseType;

    public enum LicenseType {
        B, C, C1;
    }

    public DriverInfo(String employeeId, List<LicenseType> licenseType) {
        this.employeeId = employeeId;
        this.licenseType = licenseType;
    }

    public void setLicenses(List<LicenseType> licenseTypes) {
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
