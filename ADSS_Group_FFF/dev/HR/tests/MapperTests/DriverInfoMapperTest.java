package HR.tests.MapperTests;

import HR.DTO.DriverInfoDTO;
import HR.Domain.DriverInfo;
import HR.Domain.DriverInfo.LicenseType;
import HR.Mapper.DriverInfoMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DriverInfoMapperTest {

    @Test
    void testToDTO_ValidInput() {
        DriverInfo domain = new DriverInfo("emp123", List.of(LicenseType.B, LicenseType.C1));
        DriverInfoDTO dto = DriverInfoMapper.toDTO(domain);

        assertNotNull(dto);
        assertEquals("emp123", dto.getEmployeeId());
        assertTrue(dto.getLicenseType().contains("B"));
        assertTrue(dto.getLicenseType().contains("C1"));
    }

    @Test
    void testFromDTO_ValidInput() {
        DriverInfoDTO dto = new DriverInfoDTO("emp456", List.of("C", "B"));
        DriverInfo domain = DriverInfoMapper.fromDTO(dto);

        assertNotNull(domain);
        assertEquals("emp456", domain.getEmployeeId());
        assertTrue(domain.getLicenses().contains(LicenseType.B));
        assertTrue(domain.getLicenses().contains(LicenseType.C));
    }

    @Test
    void testToDTO_NullInput() {
        assertNull(DriverInfoMapper.toDTO(null));
    }

    @Test
    void testFromDTO_NullInput() {
        assertNull(DriverInfoMapper.fromDTO(null));
    }
}
