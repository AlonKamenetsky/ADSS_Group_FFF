
package HR.tests.DAOTests;

import HR.DataAccess.ShiftDAO;
import HR.DataAccess.ShiftDAOImpl;
import HR.Domain.Employee;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.Shift.ShiftTime;
import Util.Database;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShiftDAOTest {

    private ShiftDAO shiftDAO;
    private Connection conn;

    @BeforeAll
    public void setup() {
        conn = Database.getConnection();
        shiftDAO = new ShiftDAOImpl(conn);
    }

    @BeforeEach
    public void cleanUp() {
        try {
            conn.createStatement().execute("DELETE FROM Shifts"); // adjust if table name differs
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean Shifts table", e);
        }
    }

    private Shift createSampleShift(String id, Date date, ShiftTime type, String roleName, int count) {
        Role role = new Role(roleName);
        Map<Role, ArrayList<Employee>> roleMap = new HashMap<>();
        Map<Role, Integer> required = new HashMap<>();
        roleMap.put(role, new ArrayList<>());
        required.put(role, count);
        return new Shift(id, date, type, roleMap, required);
    }

    @Test
    public void testInsertAndRetrieveShift() {
        Date date = new Date();
        Shift shift = createSampleShift("S01", date, ShiftTime.Morning, "Cook", 2);
        shiftDAO.insert(shift);

        Shift retrieved = shiftDAO.selectById("S01");
        assertNotNull(retrieved);
        assertEquals(date, retrieved.getDate());
        assertEquals(ShiftTime.Morning, retrieved.getType());
    }

    @Test
    public void testUpdateShiftRequiredCounts() {
        Date date = new Date();
        Shift shift = createSampleShift("S02", date, ShiftTime.Evening, "Clerk", 1);
        shiftDAO.insert(shift);

        Role updatedRole = new Role("Clerk");
        shift.getRequiredCounts().put(updatedRole, 3);
        shiftDAO.update(shift);

        Shift updated = shiftDAO.selectById("S02");
        assertEquals(3, updated.getRequiredCounts().get(updatedRole));
    }

    @Test
    public void testGetAllShifts() {
        shiftDAO.insert(createSampleShift("S03", new Date(), ShiftTime.Morning, "Mgr", 1));
        shiftDAO.insert(createSampleShift("S04", new Date(), ShiftTime.Evening, "Driver", 2));

        List<Shift> allShifts = shiftDAO.selectAll();
        assertTrue(allShifts.size() >= 2);
    }

    @Test
    public void testDeleteShift() {
        shiftDAO.insert(createSampleShift("S05", new Date(), ShiftTime.Evening, "Temp", 1));
        shiftDAO.delete("S05");
        assertNull(shiftDAO.selectById("S05"));
    }
}
