package staffme.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import staffme.error.EmployeeNotFoundException;
import staffme.model.entity.Employee;
import staffme.model.service.EmployeeServiceModel;
import staffme.repository.EmployeeRepository;
import staffme.service.CandidateService;
import staffme.service.CategoryService;
import staffme.service.CloudinaryService;
import staffme.service.EmployeeService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;
    private final CategoryService categoryService;
    private final CandidateService candidateService;
    private final CloudinaryService cloudinaryService;

    public EmployeeServiceImpl(ModelMapper modelMapper, EmployeeRepository employeeRepository,
                               CategoryService categoryService, CandidateService candidateService,
                               CloudinaryService cloudinaryService) {
        this.modelMapper = modelMapper;
        this.employeeRepository = employeeRepository;
        this.categoryService = categoryService;
        this.candidateService = candidateService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public EmployeeServiceModel add(EmployeeServiceModel employeeServiceModel, String id) throws IOException {
        employeeServiceModel
                .setCategory(this.categoryService.findByName(employeeServiceModel.getCategory().getCategoryName()));

        Employee employee = this.employeeRepository.
                saveAndFlush(this.modelMapper.map(employeeServiceModel, Employee.class));

        this.candidateService.deleteById(id, true);

        return this.modelMapper.map(employee, EmployeeServiceModel.class);
    }

    @Override
    public List<EmployeeServiceModel> findAllEmployeesWithCategory(String category) {
        return this.employeeRepository
                .findAll()
                .stream()
                .filter(employee -> employee.getCategory().getCategoryName().name().equals(category))
                .filter(Employee::getAvailable)
                .map(e -> this.modelMapper.map(e, EmployeeServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeServiceModel findById(String id) {
        return this.employeeRepository
                .findById(id)
                .map(e -> this.modelMapper.map(e, EmployeeServiceModel.class))
                .orElseThrow(() -> new EmployeeNotFoundException("employee with the given id was not found!"));
    }

    @Override
    public void deleteEmployee(String id) throws IOException {

        Employee employee = this.employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("employee with the given id was not found!"));

        String imgUrl = employee.getImageUrl();
        String cloudinaryId = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.lastIndexOf("."));
        this.cloudinaryService.deleteImage(cloudinaryId);

        this.employeeRepository.deleteById(id);
    }

    @Override
    public void updateAvailability(String id) {

        Employee employee = this.employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("employee with the given id was not found!"));

        employee.setAvailable(!employee.getAvailable());
        this.employeeRepository.saveAndFlush(employee);
    }

    @Override
    public EmployeeServiceModel editEmployee(String id, EmployeeServiceModel employeeServiceModel) {

        Employee employee = this.employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("employee with the given id was not found!"));

        employee.setName(employeeServiceModel.getName());
        employee.setDescription(employeeServiceModel.getDescription());
        employee.setCost(employeeServiceModel.getCost());

        return this.modelMapper.map(this.employeeRepository.saveAndFlush(employee), EmployeeServiceModel.class);
    }
}