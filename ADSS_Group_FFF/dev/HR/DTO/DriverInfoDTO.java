package HR.DTO;

import java.util.List;

public class DriverInfoDTO {
    private String employeeId;
    private List<String> licenseType;
    // (we use List<String> for licenseType; values: "B", "C", "C1", matching the enum)

    public DriverInfoDTO() { }

    public DriverInfoDTO(String employeeId, List<String> licenseType) {
        this.employeeId = employeeId;
        this.licenseType = licenseType;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public List<String> getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(List<String> licenseType) {
        this.licenseType = licenseType;
    }
}
