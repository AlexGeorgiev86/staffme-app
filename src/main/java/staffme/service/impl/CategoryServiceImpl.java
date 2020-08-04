package staffme.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import staffme.error.CategoryNotFoundException;
import staffme.model.entity.Category;
import staffme.model.entity.CategoryName;
import staffme.model.service.CategoryServiceModel;
import staffme.repository.CategoryRepository;
import staffme.service.CategoryService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void initCategories() {
        if (this.categoryRepository.count() == 0) {
            Arrays.stream(CategoryName.values()).forEach(categoryName -> {
                this.categoryRepository.save(new Category(categoryName));
            });
        }
    }

    @Override
    public CategoryServiceModel findByName(CategoryName categoryName) {
        return this.categoryRepository
                .findByCategoryName(categoryName)
                .map(category -> this.modelMapper.map(category, CategoryServiceModel.class))
                .orElseThrow(() -> new CategoryNotFoundException("Category with this name does not exist!"));
    }

    @Override
    public List<String> findAll() {
        return this.categoryRepository.findAll()
                .stream()
                .map(category -> category.getCategoryName().name())
                .collect(Collectors.toList());
    }

}
