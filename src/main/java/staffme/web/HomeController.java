package staffme.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import staffme.service.CategoryService;


@Controller
public class HomeController {

    private final CategoryService categoryService;

    public HomeController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/")
    @PreAuthorize("isAnonymous()")
    public String index() {
        return "home/index";
    }

    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView home(ModelAndView modelAndView) {

        modelAndView.addObject("categories", this.categoryService.findAll());
        modelAndView.setViewName("home/home");

        return modelAndView;
    }
}
