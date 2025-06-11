
package HR.tests.DAOTests;

import HR.DataAccess.ShiftTemplateDAO;
import HR.DataAccess.ShiftTemplateDAOImpl;
import HR.Domain.Role;
import HR.Domain.Shift;
import HR.Domain.ShiftTemplate;
import Util.Database;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShiftTemplateDAOTest {

    private ShiftTemplateDAO templateDAO;
    private Connection conn;

    @BeforeAll
    public void setup() {
        conn = Database.getConnection();
        templateDAO = new ShiftTemplateDAOImpl(conn);
    }

    @BeforeEach
    public void cleanUp() {
        try {
            conn.createStatement().execute("DELETE FROM ShiftTemplates");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInsertAndSelect() {
        ShiftTemplate template = new ShiftTemplate(DayOfWeek.MONDAY, Shift.ShiftTime.Morning);
        Role cook = new Role("Cook");
        template.setDefaultCount(cook, 2);

        templateDAO.insert(template);

        List<ShiftTemplate> all = templateDAO.selectAll();
        assertEquals(1, all.size());

        ShiftTemplate retrieved = all.get(0);
        assertEquals(DayOfWeek.MONDAY, retrieved.getDay());
        assertEquals(Shift.ShiftTime.Morning, retrieved.getTime());

        Map<Role, Integer> counts = retrieved.getDefaultCounts();
        assertTrue(counts.containsKey(cook));
        assertEquals(2, counts.get(cook));
    }

    @Test
    public void testDeleteAll() {
        ShiftTemplate t1 = new ShiftTemplate(DayOfWeek.TUESDAY, Shift.ShiftTime.Evening);
        t1.setDefaultCount(new Role("Driver"), 1);

        ShiftTemplate t2 = new ShiftTemplate(DayOfWeek.WEDNESDAY, Shift.ShiftTime.Morning);
        t2.setDefaultCount(new Role("Clerk"), 3);

        templateDAO.insert(t1);
        templateDAO.insert(t2);

        templateDAO.deleteAll();
        List<ShiftTemplate> all = templateDAO.selectAll();
        assertTrue(all.isEmpty());
    }
}
