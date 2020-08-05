package staffme.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import staffme.error.EmployeeNotFoundException;
import staffme.model.entity.Category;
import staffme.model.entity.CategoryName;
import staffme.model.entity.Employee;
import staffme.model.service.CategoryServiceModel;
import staffme.model.service.EmployeeServiceModel;
import staffme.repository.EmployeeRepository;
import staffme.service.CandidateService;
import staffme.service.CategoryService;
import staffme.service.CloudinaryService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    CategoryService categoryService;
    @Mock
    CandidateService candidateService;
    @Mock
    CloudinaryService cloudinaryService;

    ModelMapper modelMapper;

    EmployeeServiceImpl employeeService;

    @BeforeEach
    public void before() {
        modelMapper = new ModelMapper();

        employeeService = new EmployeeServiceImpl(this.modelMapper, this.employeeRepository, this.categoryService,
                this.candidateService, this.cloudinaryService);
    }

    @Test
    void addShouldReturnCorrectData() throws IOException {
        CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
        categoryServiceModel.setCategoryName(CategoryName.CHEF);
        Category category = new Category();
        category.setCategoryName(CategoryName.CHEF);
        String id = "id";

        EmployeeServiceModel employeeServiceModel = new EmployeeServiceModel("Pesho", new BigDecimal(1),
                "good","ïmg", categoryServiceModel, true);

        List<Employee> fakeEmployeeRepository = new ArrayList<>();

        Mockito.when(employeeRepository.saveAndFlush(any(Employee.class)))
                .thenAnswer(invocation -> {
                    fakeEmployeeRepository.add((Employee) invocation.getArguments()[0]);
                    return fakeEmployeeRepository.get(0);
                });

        Mockito.when(categoryService.findByName(employeeServiceModel
                        .getCategory()
                        .getCategoryName()))
                .thenReturn(categoryServiceModel);

        EmployeeServiceModel actual = employeeService.add(employeeServiceModel, id);

        assertEquals(employeeServiceModel.getCategory().getCategoryName(), actual.getCategory().getCategoryName());
        assertEquals(employeeServiceModel.getCost(), actual.getCost());
        assertEquals(employeeServiceModel.getDescription(), actual.getDescription());
        assertEquals(employeeServiceModel.getName(), actual.getName());
        assertEquals(employeeServiceModel.getImageUrl(), actual.getImageUrl());
        assertEquals(employeeServiceModel.getAvailable(), actual.getAvailable());

    }

    @Test
    void findAllEmployeesWithCategoryShouldReturnCorrectData_WhenExistingCategoryIsPassedAndIsAvailable() {
        Category category = new Category();
        category.setCategoryName(CategoryName.CHEF);
        CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
        categoryServiceModel.setCategoryName(CategoryName.CHEF);

        Employee employee = new Employee("Pesho", new BigDecimal(1),
                "good","ïmg", category, true);
        EmployeeServiceModel employeeServiceModel = new EmployeeServiceModel("Pesho", new BigDecimal(1),
                "good","ïmg", categoryServiceModel, true);

        List<Employee> employees = new ArrayList<>();
        employees.add(employee);
        List<EmployeeServiceModel> expected = new ArrayList<>();
        expected.add(employeeServiceModel);

        Mockito.when(employeeRepository.findAll()).thenReturn(employees);

       List<EmployeeServiceModel> actual = employeeService.
               findAllEmployeesWithCategory(category.getCategoryName().name());

       assertEquals(expected.get(0).getAvailable(), actual.get(0).getAvailable());
       assertEquals(expected.get(0).getCategory().getCategoryName().name(),
               actual.get(0).getCategory().getCategoryName().name());
    }

    @Test
    void findAllEmployeesWithCategoryShouldReturnEmptyList_WhenExistingCategoryIsPassedAndIsNotAvailable() {
        Category category = new Category();
        category.setCategoryName(CategoryName.CHEF);

        Employee employee = new Employee("Pesho", new BigDecimal(1),
                "good","ïmg", category, false);

        List<Employee> employees = new ArrayList<>();
        employees.add(employee);

        Mockito.when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeServiceModel> actual = employeeService.
                findAllEmployeesWithCategory(category.getCategoryName().name());

        List<EmployeeServiceModel> expected = new ArrayList<>();

        assertEquals(expected.size(), actual.size());
    }

    @Test
    void findAllEmployeesWithCategoryShouldReturnEmptyList_WhenCategoryIsNotExisting() {
        Category category = new Category();
        category.setCategoryName(CategoryName.CHEF);
        Category categoryNotExisting = new Category();
        categoryNotExisting.setCategoryName(CategoryName.HOST);

        Employee employee = new Employee("Pesho", new BigDecimal(1),
                "good","ïmg", category, true);

        List<Employee> employees = new ArrayList<>();
        employees.add(employee);

        Mockito.when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeServiceModel> actual = employeeService.
                findAllEmployeesWithCategory(categoryNotExisting.getCategoryName().name());

        List<EmployeeServiceModel> expected = new ArrayList<>();

        assertEquals(expected.size(), actual.size());
    }

    @Test
    void findByIdShouldReturnCorrectData_WhenExistingIdIsPassed() {
        Category category = new Category();
        category.setCategoryName(CategoryName.CHEF);
        CategoryServiceModel categoryServiceModel = new CategoryServiceModel();
        categoryServiceModel.setCategoryName(CategoryName.CHEF);

        Employee employee = new Employee("Pesho", new BigDecimal(1),
                "good","ïmg", category, true);
        EmployeeServiceModel expected = new EmployeeServiceModel("Pesho", new BigDecimal(1),
                "good","ïmg", categoryServiceModel, true);

        String id = "id";

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        EmployeeServiceModel actual = employeeService.findById(id);

        assertEquals(expected.getCategory().getCategoryName(), actual.getCategory().getCategoryName());
        assertEquals(expected.getCost(), actual.getCost());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getImageUrl(), actual.getImageUrl());
        assertEquals(expected.getAvailable(), actual.getAvailable());
    }

    @Test()
    void findByIdShouldThrowException_whenEmployeeDoNotExists() {

        String id = "id";

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        EmployeeNotFoundException thrown = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.findById(id));

        assertTrue(thrown.getMessage().contains("employee with the given id was not found!"));
    }

    @Test
    void deleteEmployeeShouldThrowException_whenEmployeeDoNotExists() {
        String id = "id";

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        EmployeeNotFoundException thrown = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(id));

        assertTrue(thrown.getMessage().contains("employee with the given id was not found!"));
    }

    @Test
    void updateAvailabilityShouldThrowException_whenEmployeeDoNotExists() {
        String id = "id";

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        EmployeeNotFoundException thrown = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.updateAvailability(id));

        assertTrue(thrown.getMessage().contains("employee with the given id was not found!"));
    }

    @Test
    void updateAvailabilityShouldChangeIsAvailableToTrue_whenIsAvailableIsFalse() {
        String id = "id";

        Category category = new Category();
        category.setCategoryName(CategoryName.CHEF);

        Employee employee = new Employee("Pesho", new BigDecimal(1),
                "good","ïmg", category, false);

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.saveAndFlush(any(Employee.class))).thenReturn(new Employee());

        employeeService.updateAvailability(id);

        assertEquals(true, employee.getAvailable());
    }

    @Test
    void editEmployeeShouldThrowException_whenEmployeeDoNotExists() {
        String id = "id";

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        EmployeeNotFoundException thrown = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.editEmployee(id, new EmployeeServiceModel()));

        assertTrue(thrown.getMessage().contains("employee with the given id was not found!"));
    }

    @Test
    void editEmployeeShouldEditFieldsCorrectly() {

        String id = "id";

        Employee expected = new Employee();
        expected.setCost(new BigDecimal(1));
        expected.setDescription("good");
        expected.setName("Jordan");

        EmployeeServiceModel employeeServiceModel = new EmployeeServiceModel();
        employeeServiceModel.setCost(new BigDecimal(1));
        employeeServiceModel.setDescription("good");
        employeeServiceModel.setName("Jordan");

        Employee actual = new Employee();

        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(actual));
        Mockito.when(employeeRepository.saveAndFlush(any(Employee.class))).thenReturn(new Employee());

        employeeService.editEmployee(id, employeeServiceModel);

        assertEquals(expected.getCost(), actual.getCost());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getName(), actual.getName());
    }
}