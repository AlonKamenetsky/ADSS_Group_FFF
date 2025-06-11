package HR.tests.DomainTests;

import HR.Domain.DriverInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DriverInfoTest {

    @Test
    public void constructor_and_getters() {
        List<DriverInfo.LicenseType> licenses = new ArrayList<>();
        licenses.add(DriverInfo.LicenseType.B);
        DriverInfo info = new DriverInfo("emp1", licenses);

        assertEquals("emp1", info.getEmployeeId());
        assertEquals(1, info.getLicenses().size());
        assertTrue(info.getLicenses().contains(DriverInfo.LicenseType.B));
    }

    @Test
    public void setLicenses_replacesList() {
        List<DriverInfo.LicenseType> initial = new ArrayList<>();
        initial.add(DriverInfo.LicenseType.C);
        DriverInfo info = new DriverInfo("emp2", initial);

        List<DriverInfo.LicenseType> newList = new ArrayList<>();
        newList.add(DriverInfo.LicenseType.C1);
        newList.add(DriverInfo.LicenseType.B);
        info.setLicenses(newList);

        assertEquals(2, info.getLicenses().size());
        assertTrue(info.getLicenses().contains(DriverInfo.LicenseType.C1));
        assertTrue(info.getLicenses().contains(DriverInfo.LicenseType.B));
    }

    @Test
    public void addAndRemoveLicenseType() {
        List<DriverInfo.LicenseType> licenses = new ArrayList<>();
        licenses.add(DriverInfo.LicenseType.B);
        DriverInfo info = new DriverInfo("emp3", licenses);

        info.AddLicenseType(DriverInfo.LicenseType.C);
        assertTrue(info.getLicenses().contains(DriverInfo.LicenseType.C));

        info.RemoveLicenseType(DriverInfo.LicenseType.B);
        assertFalse(info.getLicenses().contains(DriverInfo.LicenseType.B));
        assertTrue(info.getLicenses().contains(DriverInfo.LicenseType.C));
    }

    @Test
    public void toString_containsEmployeeIdAndLicenses() {
        List<DriverInfo.LicenseType> licenses = new ArrayList<>();
        licenses.add(DriverInfo.LicenseType.C1);
        DriverInfo info = new DriverInfo("emp4", licenses);

        String str = info.toString();
        assertTrue(str.contains("emp4"));
        assertTrue(str.contains("C1"));
    }
}
