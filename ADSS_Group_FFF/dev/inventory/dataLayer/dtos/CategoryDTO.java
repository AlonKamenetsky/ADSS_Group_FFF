package inventory.dataLayer.dtos;

import java.util.List;

public class CategoryDTO {
    private String name;
    private String parentCategoryName;
    private List<String> subCategoryNames;

    public CategoryDTO() {
        // Default constructor for serialization/deserialization
    }

    public CategoryDTO(String name, String parentCategoryName, List<String> subCategoryNames) {
        this.name = name;
        this.parentCategoryName = parentCategoryName;
        this.subCategoryNames = subCategoryNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    public List<String> getSubCategoryNames() {
        return subCategoryNames;
    }

    public void setSubCategoryNames(List<String> subCategoryNames) {
        this.subCategoryNames = subCategoryNames;
    }
}

