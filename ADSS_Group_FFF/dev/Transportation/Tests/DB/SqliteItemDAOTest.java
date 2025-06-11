package Transportation.Tests.DB;

import Transportation.DataAccess.SqliteItemDAO;
import Transportation.DTO.ItemDTO;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SqliteItemDAOTest {

    private SqliteItemDAO itemDAO;

    @BeforeEach
    void setUp() throws SQLException {
        itemDAO = new SqliteItemDAO();
        clearItemsTable();
    }

    void clearItemsTable() throws SQLException {
        try (Statement st = Util.Database.getConnection().createStatement()) {
            st.executeUpdate("DELETE FROM items");
        }
    }

    @Test
    void insert_newItem_insertsAndReturnsWithId() throws SQLException {
        ItemDTO item = new ItemDTO(null, "SALT", 1.5f);
        ItemDTO inserted = itemDAO.insert(item);

        assertNotNull(inserted.itemId());
        assertEquals("SALT", inserted.itemName());
        assertEquals(1.5f, inserted.itemWeight());
    }

    @Test
    void insert_existingItem_updatesSuccessfully() throws SQLException {
        ItemDTO original = itemDAO.insert(new ItemDTO(null, "FLOUR", 2.0f));
        ItemDTO updated = new ItemDTO(original.itemId(), "FLOUR", 2.5f);

        ItemDTO result = itemDAO.insert(updated);

        assertEquals(original.itemId(), result.itemId());
        assertEquals(2.5f, result.itemWeight());
    }

    @Test
    void findById_existingItem_returnsCorrect() throws SQLException {
        ItemDTO item = itemDAO.insert(new ItemDTO(null, "SUGAR", 3.0f));

        Optional<ItemDTO> result = itemDAO.findById(item.itemId());

        assertTrue(result.isPresent());
        assertEquals("SUGAR", result.get().itemName());
    }

    @Test
    void findByName_existingItem_returnsCorrect() throws SQLException {
        ItemDTO item = itemDAO.insert(new ItemDTO(null, "RICE", 1.0f));

        Optional<ItemDTO> result = itemDAO.findByName("RICE");

        assertTrue(result.isPresent());
        assertEquals(item.itemId(), result.get().itemId());
    }

    @Test
    void delete_removesItemSuccessfully() throws SQLException {
        ItemDTO item = itemDAO.insert(new ItemDTO(null, "WATER", 0.5f));
        itemDAO.delete(item.itemId());

        Optional<ItemDTO> result = itemDAO.findById(item.itemId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_returnsAllInsertedItems() throws SQLException {
        itemDAO.insert(new ItemDTO(null, "APPLE", 0.4f));
        itemDAO.insert(new ItemDTO(null, "BANANA", 0.6f));

        List<ItemDTO> items = itemDAO.findAll();

        assertEquals(2, items.size());
        assertEquals("APPLE", items.get(0).itemName());
        assertEquals("BANANA", items.get(1).itemName());
    }
}

