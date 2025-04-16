package inventory;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private Category parentCategory;
    private List<Category> subCategories;

    public Category(String name, Category parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
        this.subCategories = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Category getParentCategory() {
        return parentCategory;
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public void addSubCategory(Category subCategory) {
        subCategories.add(subCategory);
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", parent=" + (parentCategory != null ? parentCategory.getName() : "none") +
                '}';
    }
}
