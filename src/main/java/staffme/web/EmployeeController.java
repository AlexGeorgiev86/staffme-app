package staffme.web;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import staffme.error.CategoryNotFoundException;
import staffme.error.EmployeeNotFoundException;
import staffme.model.binding.EmployeeAddBindingModel;
import staffme.model.binding.RequestAddBindingModel;
import staffme.model.service.EmployeeServiceModel;
import staffme.model.service.RequestServiceModel;
import staffme.model.view.EmployeeDetailsViewModel;
import staffme.service.EmployeeService;
import staffme.service.RequestService;

import java.io.IOException;


@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final ModelMapper modelMapper;
    private final RequestService requestService;

    public EmployeeController(EmployeeService employeeService, ModelMapper modelMapper, RequestService requestService) {
        this.employeeService = employeeService;
        this.modelMapper = modelMapper;
        this.requestService = requestService;
    }


    @GetMapping("/by-category/{c}")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView allByCategory(@PathVariable String c, ModelAndView modelAndView) {

        modelAndView.addObject("employees", this.employeeService.findAllEmployeesWithCategory(c));
        modelAndView.setViewName("employee/employees-by-category");

        return modelAndView;
    }

    @PostMapping("/add/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addPost(@PathVariable String id, @ModelAttribute EmployeeAddBindingModel employeeAddBindingModel) throws IOException {
        EmployeeServiceModel employeeServiceModel = this.modelMapper.map(employeeAddBindingModel, EmployeeServiceModel.class);

        this.employeeService.add(employeeServiceModel, id);

        return "redirect:/home";
    }


    @GetMapping("/details/{id}")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView details(@PathVariable String id, ModelAndView modelAndView) {
        EmployeeDetailsViewModel employeeDetailsViewModel = this.modelMapper
                .map(this.employeeService.findById(id), EmployeeDetailsViewModel.class);

        modelAndView.addObject("employee", employeeDetailsViewModel);
        modelAndView.setViewName("employee/employee-details");

        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView editEmployee(@PathVariable String id, ModelAndView modelAndView) {

        EmployeeDetailsViewModel employeeDetailsViewModel = this.modelMapper
                .map(this.employeeService.findById(id), EmployeeDetailsViewModel.class);

        modelAndView.addObject("employee", employeeDetailsViewModel);
        modelAndView.setViewName("employee/employee-edit");

        return modelAndView;
    }

    @GetMapping("/hire/{id}")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView hireEmployee(@PathVariable String id, ModelAndView modelAndView) {

        EmployeeDetailsViewModel employeeDetailsViewModel = this.modelMapper
                .map(this.employeeService.findById(id), EmployeeDetailsViewModel.class);

        modelAndView.addObject("employee", employeeDetailsViewModel);
        modelAndView.setViewName("employee/employee-hire");

        return modelAndView;
    }
    @PostMapping("/hire/{id}")
    @PreAuthorize("isAuthenticated()")
    public String hireEmployeePost(@PathVariable String id, @ModelAttribute RequestAddBindingModel requestAddBindingModel) {
        RequestServiceModel requestServiceModel = this.modelMapper.map(requestAddBindingModel, RequestServiceModel.class);

        this.requestService
                .makeRequest(id, requestServiceModel);

        return "redirect:/home";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editEmployeePost(@PathVariable String id, @ModelAttribute EmployeeAddBindingModel employeeAddBindingModel) {
        EmployeeServiceModel employeeServiceModel = this.modelMapper.map(employeeAddBindingModel, EmployeeServiceModel.class);

        this.employeeService
                .editEmployee(id, employeeServiceModel);

        return "redirect:/home";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView deleteProduct(@PathVariable String id, ModelAndView modelAndView) {
        EmployeeDetailsViewModel employeeDetailsViewModel = this.modelMapper
                .map(this.employeeService.findById(id), EmployeeDetailsViewModel.class);

        modelAndView.addObject("employee", employeeDetailsViewModel);
        modelAndView.setViewName("employee/employee-delete");

        return modelAndView;
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteEmployeePost(@PathVariable String id) throws IOException {
        this.employeeService
                .deleteEmployee(id);

        return "redirect:/home";
    }

    @ExceptionHandler({EmployeeNotFoundException.class, CategoryNotFoundException.class, IOException.class})
    public ModelAndView handleException(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("error/custom-error");
        modelAndView.addObject("message", exception.getMessage());

        return modelAndView;
    }





}
