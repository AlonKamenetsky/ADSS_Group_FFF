package HR.tests.MapperTests;

import HR.Domain.DriverInfo;
import HR.Domain.DriverInfo.LicenseType;
import HR.DTO.DriverInfoDTO;

import HR.Mapper.DriverInfoMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DriverInfoMapperTest {

    @Test
    public void testToDTO_withValidDomain_convertsCorrectly() {
        // Domain object with licenses B and C1
        DriverInfo domain = new DriverInfo("E123", List.of(LicenseType.B, LicenseType.C1));

        DriverInfoDTO dto = DriverInfoMapper.toDTO(domain);

        assertNotNull(dto);
        assertEquals("E123", dto.getEmployeeId());
        List<String> licenses = dto.getLicenseType();
        assertNotNull(licenses);
        assertEquals(2, licenses.size());
        assertTrue(licenses.contains("B"));
        assertTrue(licenses.contains("C1"));
    }

    @Test
    public void testToDTO_withNullDomain_returnsNull() {
        assertNull(DriverInfoMapper.toDTO(null));
    }

    @Test
    public void testFromDTO_withValidDTO_convertsCorrectly() {
        // DTO with string licenses "B" and "C"
        DriverInfoDTO dto = new DriverInfoDTO("E456", List.of("B", "C"));

        DriverInfo domain = DriverInfoMapper.fromDTO(dto);

        assertNotNull(domain);
        assertEquals("E456", domain.getEmployeeId());
        List<LicenseType> licenses = domain.getLicenses();
        assertNotNull(licenses);
        assertEquals(2, licenses.size());
        assertTrue(licenses.contains(LicenseType.B));
        assertTrue(licenses.contains(LicenseType.C));
    }

    @Test
    public void testFromDTO_withNullDTO_returnsNull() {
        assertNull(DriverInfoMapper.fromDTO(null));
    }

    @Test
    public void testFromDTO_withInvalidLicense_throwsException() {
        DriverInfoDTO dto = new DriverInfoDTO("E789", List.of("B", "INVALID"));

        assertThrows(IllegalArgumentException.class, () -> {
            DriverInfoMapper.fromDTO(dto);
        });
    }
}
