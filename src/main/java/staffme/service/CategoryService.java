package staffme.service;

import staffme.model.entity.CategoryName;
import staffme.model.service.CategoryServiceModel;

import java.util.List;

public interface CategoryService {
    void initCategories();

    CategoryServiceModel findByName(CategoryName categoryName);
    List<String> findAll();



}
