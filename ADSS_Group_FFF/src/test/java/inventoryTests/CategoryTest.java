package inventoryTests;

import inventory.domainLayer.Category;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void constructorAndGetters_shouldReturnCorrectNameAndParent() {
        Category parent = new Category("Electronics", null);
        Category child = new Category("Smartphones", parent);

        assertEquals("Smartphones", child.getName());
        assertNotNull(child.getParentCategory());
        assertEquals("Electronics", child.getParentCategory().getName());
    }

    @Test
    void addAndRetrieveSubCategory_shouldAppearInSubCategoriesList() {
        Category root = new Category("RootCat", null);
        Category sub1 = new Category("SubCat1", root);
        Category sub2 = new Category("SubCat2", root);

        // assume addSubCategory is implemented as: root.getSubCategories().add(sub) or a dedicated method
        root.getSubCategories().add(sub1);
        root.getSubCategories().add(sub2);

        List<Category> subs = root.getSubCategories();
        assertEquals(2, subs.size());
        assertTrue(subs.contains(sub1));
        assertTrue(subs.contains(sub2));
    }

    @Test
    void toString_includesNameAndParentName() {
        Category parent = new Category("Food", null);
        Category child = new Category("Fruit", parent);

        String repr = child.toString();
        assertTrue(repr.contains("name='Fruit'"));
        assertTrue(repr.contains("parent=Food"));
    }
}

