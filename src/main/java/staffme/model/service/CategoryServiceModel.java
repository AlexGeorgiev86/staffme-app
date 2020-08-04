package staffme.model.service;

import staffme.model.entity.CategoryName;

public class CategoryServiceModel extends BaseServiceModel {

    private CategoryName categoryName;

    public CategoryServiceModel() {
    }

    public CategoryName getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(CategoryName categoryName) {
        this.categoryName = categoryName;
    }

}
