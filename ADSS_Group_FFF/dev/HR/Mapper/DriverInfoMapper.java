package HR.Mapper;

import HR.Domain.DriverInfo;
import HR.Domain.DriverInfo.LicenseType;
import HR.DTO.DriverInfoDTO;

import java.util.List;
import java.util.stream.Collectors;

public class DriverInfoMapper {
    public static DriverInfoDTO toDTO(DriverInfo di) {
        if (di == null) return null;

        // Domain has getEmployeeId() and getLicenses() returning List<LicenseType>
        List<String> licenses = di.getLicenses().stream()
                .map(LicenseType::name)
                .collect(Collectors.toList());

        return new DriverInfoDTO(di.getEmployeeId(), licenses);
    }

    public static DriverInfo fromDTO(DriverInfoDTO dto) {
        if (dto == null) return null;

        // Domain constructor is: public DriverInfo(String employeeId, List<LicenseType> licenseType)
        List<LicenseType> licenseEnums = dto.getLicenseType().stream()
                .map(LicenseType::valueOf)
                .collect(Collectors.toList());

        return new DriverInfo(dto.getEmployeeId(), licenseEnums);
    }
}
