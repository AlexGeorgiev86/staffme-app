package staffme.web;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import staffme.error.EmailExistsException;
import staffme.error.PasswordsNotEqualException;
import staffme.error.UsernameExistsException;
import staffme.model.binding.UserEditBindingModel;
import staffme.model.binding.UserRegisterBindingModel;
import staffme.model.binding.UserRoleBindingModel;
import staffme.model.service.UserServiceModel;
import staffme.model.view.UserProfileViewModel;
import staffme.model.view.UserViewModel;
import staffme.service.CategoryService;
import staffme.service.UserService;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final CategoryService categoryService;

    public UsersController(UserService userService, ModelMapper modelMapper, CategoryService categoryService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
    }

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public String login() {
        return "user/login";
    }


    @PostMapping("/login-error")
    public ModelAndView onLoginError() {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("error", "bad_credentials");

        modelAndView.setViewName("user/login");

        return modelAndView;
    }

    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    public String register(Model model) {

        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserRegisterBindingModel());
        }
        return "user/register";
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerPost(@Valid @ModelAttribute("user") UserRegisterBindingModel userRegisterBindingModel,
                                     ModelAndView modelAndView, RedirectAttributes ra) {
        try {
            this.userService.registerUser(this.modelMapper.map(userRegisterBindingModel, UserServiceModel.class));
            modelAndView.setViewName("redirect:/users/login");
        } catch (UsernameExistsException e) {
            ra.addFlashAttribute("userErrorMessage", e.getMessage());
            ra.addFlashAttribute("user", userRegisterBindingModel);
            modelAndView.setViewName("redirect:/users/register");
        } catch (EmailExistsException e) {
            ra.addFlashAttribute("emailErrorMessage", e.getMessage());
            ra.addFlashAttribute("user", userRegisterBindingModel);
            modelAndView.setViewName("redirect:/users/register");
        } catch (PasswordsNotEqualException e) {
            ra.addFlashAttribute("passwordsErrorMessage", e.getMessage());
            ra.addFlashAttribute("user", userRegisterBindingModel);
            modelAndView.setViewName("redirect:/users/register");
        }
        return modelAndView;
    }

    @GetMapping("/roles/{r}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView roles(@PathVariable String r, ModelAndView modelAndView) {

        List<UserViewModel> users = this.userService
                .findAllUsersWithRole(r)
                .stream()
                .map(user -> this.modelMapper.map(user, UserViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("users", users);
        modelAndView.setViewName("user/roles");

        return modelAndView;
    }

    @PostMapping("/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView rolesPost(@ModelAttribute(name = "user") UserRoleBindingModel model,
                                  RedirectAttributes ra, ModelAndView modelAndView) {
        try {
            this.userService.changeRole(model.getUsername());
            modelAndView.addObject("categories", this.categoryService.findAll());
            modelAndView.setViewName("home/home");
        } catch (RoleNotFoundException e) {
            ra.addFlashAttribute("roleErrorMessage", e.getMessage());
            modelAndView.setViewName("redirect:/users/roles");
        }
        return modelAndView;
    }

    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfile(Principal principal, ModelAndView modelAndView) {

        UserProfileViewModel userProfileViewModel = this.modelMapper
                .map(this.userService.findByUsername(principal.getName()),
                        UserProfileViewModel.class);

        modelAndView.addObject("user", userProfileViewModel);
        modelAndView.setViewName("user/profile-edit");

        return modelAndView;
    }

    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfilePatch(@ModelAttribute(name = "user") UserEditBindingModel userEditBindingModel,
                                         RedirectAttributes ra, ModelAndView modelAndView) {
        try {
            this.userService.editUserProfile(
                    this.modelMapper.map(userEditBindingModel, UserServiceModel.class), userEditBindingModel.getOldPassword());
            modelAndView.setViewName("home/home");
            modelAndView.addObject("categories", this.categoryService.findAll());
        } catch (PasswordsNotEqualException e) {
            ra.addFlashAttribute("passwordsErrorMessage", e.getMessage());
            ra.addFlashAttribute("user", userEditBindingModel);
            modelAndView.setViewName("redirect:/users/edit");
        }
        return modelAndView;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ModelAndView handleException(UsernameExistsException exception) {
        ModelAndView modelAndView = new ModelAndView("error/custom-error");
        modelAndView.addObject("message", exception.getMessage());

        return modelAndView;
    }


}
