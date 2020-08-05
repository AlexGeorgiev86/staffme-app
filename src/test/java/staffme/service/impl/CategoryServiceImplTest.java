package staffme.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import staffme.error.CategoryNotFoundException;
import staffme.model.entity.Category;
import staffme.model.entity.CategoryName;
import staffme.model.service.CategoryServiceModel;
import staffme.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    CategoryRepository categoryRepository;

    ModelMapper modelMapper;

    CategoryServiceImpl categoryService;

    @BeforeEach
    public void before() {
        modelMapper = new ModelMapper();

        categoryService = new CategoryServiceImpl(this.categoryRepository, this.modelMapper);
    }

    @Test
    void initCategories() {

        List<Category> fakeCategoryRepository = new ArrayList<>();

        Mockito.when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> {
                    fakeCategoryRepository.add((Category) invocation.getArguments()[0]);
                    return null;
                });

        Mockito.when(categoryRepository.count()).thenReturn(0L);

        categoryService.initCategories();
        int expected = 6;

        assertEquals(expected, fakeCategoryRepository.size());
        assertEquals("CHEF", fakeCategoryRepository.get(0).getCategoryName().name());
        assertEquals("WAITER", fakeCategoryRepository.get(1).getCategoryName().name());
        assertEquals("HOST", fakeCategoryRepository.get(2).getCategoryName().name());
        assertEquals("SUPERVISOR", fakeCategoryRepository.get(3).getCategoryName().name());
        assertEquals("DISHWASHER", fakeCategoryRepository.get(4).getCategoryName().name());
        assertEquals("BUSSER", fakeCategoryRepository.get(5).getCategoryName().name());
    }

    @Test
    void findByNameShouldReturnCorrectData_whenExistingCategoryNameIsPassed() {
        Category category = new Category();
        category.setCategoryName(CategoryName.CHEF);
        CategoryServiceModel expected = new CategoryServiceModel();
        expected.setCategoryName(CategoryName.CHEF);

        Mockito.when(categoryRepository.findByCategoryName(CategoryName.CHEF)).thenReturn(Optional.of(category));

        CategoryServiceModel actual = categoryService.findByName(CategoryName.CHEF);

        assertEquals(expected.getCategoryName().name(), actual.getCategoryName().name());
    }

    @Test()
    void findByNameShouldThrowException_whenCategoryNameDoNotExists() {

        Mockito.when(categoryRepository.findByCategoryName(CategoryName.CHEF)).thenReturn(Optional.empty());

        CategoryNotFoundException thrown = assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.findByName(CategoryName.CHEF));

        assertTrue(thrown.getMessage().contains("Category with this name does not exist!"));
    }

    @Test
    void findAllShouldReturnAllSixEnums() {

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(CategoryName.CHEF));
        categories.add(new Category(CategoryName.HOST));
        categories.add(new Category(CategoryName.SUPERVISOR));
        categories.add(new Category(CategoryName.WAITER));
        categories.add(new Category(CategoryName.BUSSER));
        categories.add(new Category(CategoryName.DISHWASHER));

        Mockito.when(categoryRepository.findAll()).thenReturn(categories);

        List<String> actual = categoryService.findAll();

        assertEquals(CategoryName.CHEF.name(), actual.get(0));
        assertEquals(CategoryName.HOST.name(), actual.get(1));
        assertEquals(CategoryName.SUPERVISOR.name(), actual.get(2));
        assertEquals(CategoryName.WAITER.name(), actual.get(3));
        assertEquals(CategoryName.BUSSER.name(), actual.get(4));
        assertEquals(CategoryName.DISHWASHER.name(), actual.get(5));
    }
}