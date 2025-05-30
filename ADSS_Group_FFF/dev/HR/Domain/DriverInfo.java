package HR.Domain;

public class DriverInfo {
    private String employeeId;
    private String licenseType;

    public DriverInfo(String employeeId, String licenseType) {
        this.employeeId = employeeId;
        this.licenseType = licenseType;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "DriverInfo{" +
                "employeeId='" + employeeId + '\'' +
                ", licenseType='" + licenseType + '\'' +
                '}';
    }

}
