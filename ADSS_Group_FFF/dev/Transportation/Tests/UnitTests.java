import Transportation.Domain.*;

import org.junit.jupiter.api.Test;

import javax.management.InstanceAlreadyExistsException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {
    /// //////////////// Driver
    @Test
    public void addDriver() {
        DriverManager driverManager = new DriverManager();
        assertDoesNotThrow(() -> {
            driverManager.addDriver("12345678", "liel", "C");
        });
        Driver driver = driverManager.getDriverById("12345678");
        assertNotNull(driver);
        assertEquals("12345678", driver.getDriverId());
        assertEquals("liel", driver.getName());
        assertTrue(driver.isAvailable());
    }

    @Test
    public void addDriverWithBadLicense() {
        DriverManager driverManager = new DriverManager();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            driverManager.addDriver("123456789", "lidor", "a");
        });
        assertEquals("License does not exist", exception.getMessage());
    }

    @Test
    public void addDriverWithTheSameId() {
        DriverManager driverManager = new DriverManager();
        assertThrows(InstanceAlreadyExistsException.class, () -> {
            driverManager.addDriver("12345678", "liel", "C");
            driverManager.addDriver("12345678", "ALEX", "B");
        });
    }

    @Test
    public void deleteDriver() {
        DriverManager driverManager = new DriverManager();
        driverManager.removeDriver("12345678");
        Driver d = driverManager.getDriverById("12345678");
        assertNull(d);
    }

    /// //////// Trucks
    @Test
    public void addTruck() {
        TruckManager truckManager = new TruckManager();
        assertDoesNotThrow(() -> {
            truckManager.addTruck("Small", "123", "KIA", 120, 12);
        });
        Truck t = truckManager.getTruckIdByLicenseNumber("123");
        assertNotNull(t);
    }

    @Test
    public void addNullTruck() {
        TruckManager truckManager = new TruckManager();
        Exception e = assertThrows(NullPointerException.class, () -> {
            truckManager.addTruck(null, "123", "KIA", 120, 12);
        });
        assertEquals("Missed Parameters to added Truck", e.getMessage());
    }

    //bad input
    @Test
    public void addTruckWithBadType() {
        TruckManager truckManager = new TruckManager();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            truckManager.addTruck("mini", "1235", "KIA", 20, 12);
        });
        assertEquals("Invalid truck type. Please enter Small, Medium, or Large.", exception.getMessage());
    }

    @Test
    public void addTruckWithTheSameLicenseNumber() {
        TruckManager truckManager = new TruckManager();
        assertThrows(InstanceAlreadyExistsException.class, () -> {
            truckManager.addTruck("SMALL", "123", "KIA", 120, 12);
            truckManager.addTruck("LARGE", "123", "BMW", 120, 12);
        });
        Truck t = truckManager.getTruckIdByLicenseNumber("123");
        assertEquals(TruckType.SMALL, t.getTruckType());
    }

    @Test
    void removeTruck() {
        TruckManager truckManager = new TruckManager();
        assertDoesNotThrow(() -> {
            truckManager.addTruck("Small", "123", "KIA", 120, 12);
        });
        truckManager.removeTruck("123");
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            Truck t = truckManager.getTruckIdByLicenseNumber("123");
        });
        assertEquals("Truck does not exist", exception.getMessage());
    }

    /// Zone
    @Test
    public void addZone() {
        ZoneManager zoneManager = new ZoneManager();
        zoneManager.addZone("WEST");
        Zone Z = zoneManager.getZoneByName("WEST");
        assertNotNull(Z);
    }

    @Test
    public void addNullZone() {
        ZoneManager zoneManager = new ZoneManager();
        Exception e = assertThrows(NullPointerException.class, () -> {
            zoneManager.addZone(null);
        });
        assertEquals("Zone name cannot be null", e.getMessage());
    }

    @Test
    public void removeZone() {
        ZoneManager zoneManager = new ZoneManager();
        zoneManager.addZone("WEST");
        zoneManager.removeZone("WEST");
        assertThrows(NoSuchElementException.class, () -> {
            zoneManager.getZoneByName("WEST");
        });
    }

    /// / Site
    @Test
    public void addSite() {
        ZoneManager zoneManager = new ZoneManager();
        zoneManager.addZone("WEST");
        SiteManager siteManager = new SiteManager(zoneManager);
        siteManager.addSite("Rager", "liel", "065432", "WEST");
        Site s = siteManager.getSiteByAddress("Rager");
        assertNotNull(s);
    }

    @Test
    public void addSiteWithoutExistingZone() {
        ZoneManager zoneManager = new ZoneManager();
        SiteManager siteManager = new SiteManager(zoneManager);
        assertThrows(NoSuchElementException.class, () -> {
            siteManager.addSite("Rager", "liel", "065432", "WEST2");
        });
    }

    @Test
    public void removeSite() {
        ZoneManager zoneManager = new ZoneManager();
        zoneManager.addZone("WEST");
        SiteManager siteManager = new SiteManager(zoneManager);
        siteManager.addSite("Rager", "liel", "065432", "WEST");
        siteManager.removeSite("Rager");
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            Site s = siteManager.getSiteByAddress("Rager");
        });
        assertEquals("No site found", exception.getMessage());
    }

    //    /// Task
    @Test
    public void addTask() throws ParseException {
        ZoneManager zoneManager = new ZoneManager();
        zoneManager.addZone("SOUTH");
        SiteManager siteManager = new SiteManager(zoneManager);
        siteManager.addSite("Rager", "liel", "065432", "SOUTH");
        DriverManager driverManager = new DriverManager();
        TruckManager truckManager = new TruckManager();
        ItemManager itemManager = new ItemManager();
        TaskManager taskManager = new TaskManager(siteManager, driverManager, truckManager, itemManager);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        taskManager.addTask("12/12/2024", "12:44", "Rager");
        TransportationTask t = taskManager.getTask("12/12/2024", "12:44", "Rager");
        assertNotNull(t);
    }
}