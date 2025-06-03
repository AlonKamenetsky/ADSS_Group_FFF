package HR.DTO;

import java.util.List;
import java.util.Objects;

/**
 * DTO for carrying driver‐specific license information.
 * Uses List<String> for licenseType (values: "B", "C", "C1", matching the enum names).
 */
public class DriverInfoDTO {
    private String employeeId;
    private List<String> licenseType;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriverInfoDTO that = (DriverInfoDTO) o;
        return Objects.equals(employeeId, that.employeeId) &&
                Objects.equals(licenseType, that.licenseType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, licenseType);
    }

    @Override
    public String toString() {
        return "DriverInfoDTO{" +
                "employeeId='" + employeeId + '\'' +
                ", licenseType=" + licenseType +
                '}';
    }
}
