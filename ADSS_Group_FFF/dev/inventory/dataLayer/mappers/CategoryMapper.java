package inventory.dataLayer.mappers;

import inventory.domainLayer.Category;
import inventory.dataLayer.dtos.CategoryDTO;

import java.util.Map;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryDTO toDTO(Category category) {
        return new CategoryDTO(
                category.getName(),
                category.getParentCategory() != null ? category.getParentCategory().getName() : null,
                category.getSubCategories().stream()
                        .map(Category::getName)
                        .collect(Collectors.toList())
        );
    }

    public static Category fromDTO(CategoryDTO dto, Map<String, Category> categoryLookup) {
        Category parent = dto.getParentCategoryName() != null
                ? categoryLookup.get(dto.getParentCategoryName())
                : null;

        Category category = new Category(dto.getName(), parent);

        if (dto.getSubCategoryNames() != null) {
            for (String subName : dto.getSubCategoryNames()) {
                Category sub = categoryLookup.get(subName);
                if (sub != null) {
                    category.addSubCategory(sub);
                }
            }
        }

        return category;
    }
}
