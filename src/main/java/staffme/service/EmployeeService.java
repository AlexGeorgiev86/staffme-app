package staffme.service;



import staffme.model.service.EmployeeServiceModel;


import java.io.IOException;
import java.util.List;

public interface EmployeeService {
    EmployeeServiceModel add(EmployeeServiceModel employeeServiceModel, String id) throws IOException;

    List<EmployeeServiceModel>findAllEmployeesWithCategory(String category);

    EmployeeServiceModel findById(String id);

    void deleteEmployee(String id) throws IOException;

    void updateAvailability(String id);

    EmployeeServiceModel editEmployee(String id, EmployeeServiceModel employeeServiceModel);

}
