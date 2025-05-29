package Transportation.Tests.Service;

import Transportation.Domain.ItemManager;
import Transportation.DTO.ItemDTO;
import Transportation.Service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    ItemManager itemManager;

    @InjectMocks
    ItemService itemService;

    @Test
    void addItem_ValidInput_CallsManager() throws SQLException {
        itemService.addItem("Apple", 1.5f);
        verify(itemManager).addItem("Apple", 1.5f);
    }

    @Test
    void addItem_InvalidWeight_DoesNothing() throws SQLException {
        itemService.addItem("Banana", -2f);
        verify(itemManager, never()).addItem(anyString(), anyFloat());
    }

    @Test
    void deleteItem_ItemExists_CallsRemove() throws SQLException {
        ItemDTO mockItem = new ItemDTO(5, "Milk", 3.0f);
        when(itemManager.getItemByName("Milk")).thenReturn(mockItem);

        itemService.deleteItem("Milk");
        verify(itemManager).removeItem(5);
    }

    @Test
    void deleteItem_NullName_DoesNothing() throws SQLException {
        itemService.deleteItem(null);
        verify(itemManager, never()).getItemByName(any());
    }

    @Test
    void viewAllItems_ReturnsList() throws SQLException {
        List<ItemDTO> mockList = Arrays.asList(
                new ItemDTO(1, "Item1", 1.0f),
                new ItemDTO(2, "Item2", 2.5f)
        );
        when(itemManager.getAllItems()).thenReturn(mockList);

        List<ItemDTO> result = itemService.viewAllItems();
        assertEquals(2, result.size());
    }

    @Test
    void doesItemExist_ItemFound_ReturnsTrue() throws SQLException {
        ItemDTO mockItem = new ItemDTO(10, "Cheese", 0.8f);
        when(itemManager.getItemByName("Cheese")).thenReturn(mockItem);
        when(itemManager.doesItemExist(10)).thenReturn(true);

        assertTrue(itemService.doesItemExist("Cheese"));
    }

    @Test
    void doesItemExist_NullName_ReturnsFalse() {
        assertFalse(itemService.doesItemExist(null));
    }

    @Test
    void doesItemExist_ThrowsSQLException_ReturnsRuntimeException() throws SQLException {
        when(itemManager.getItemByName("Bread")).thenThrow(new SQLException());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                itemService.doesItemExist("Bread")
        );
        assertEquals("Database access error", exception.getMessage());
    }
}
