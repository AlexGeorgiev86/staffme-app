package staffme.web;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import staffme.error.*;
import staffme.model.binding.CandidateAddBindingModel;
import staffme.model.service.CandidateServiceModel;
import staffme.model.view.CandidateViewModel;
import staffme.service.CandidateService;
import staffme.service.CategoryService;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/candidates")
public class CandidateController {

    private final CandidateService candidateService;
    private final ModelMapper modelMapper;
    private final CategoryService categoryService;

    public CandidateController(CandidateService candidateService, ModelMapper modelMapper,
                              CategoryService categoryService) {
        this.candidateService = candidateService;
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
    }

    @GetMapping("/apply")
    @PreAuthorize("isAuthenticated()")
    public String apply(Model model) {

        if (!model.containsAttribute("candidate")) {
            model.addAttribute("candidate", new CandidateAddBindingModel());
        }
        return "candidate/candidate-apply";
    }

    @PostMapping("/apply")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView applyPost(@ModelAttribute("candidate") CandidateAddBindingModel candidateAddBindingModel,
                                  ModelAndView modelAndView, RedirectAttributes ra)  {

        try {
            CandidateServiceModel candidateServiceModel = this.modelMapper
                    .map(candidateAddBindingModel, CandidateServiceModel.class);
            this.candidateService.addCandidate(candidateServiceModel, candidateAddBindingModel.getImage());
            modelAndView.setViewName("redirect:/home");
        } catch (IOException e) {
            ra.addFlashAttribute("imageErrorMessage", e.getMessage());
            ra.addFlashAttribute("candidate", candidateAddBindingModel);
            modelAndView.setViewName("redirect:/candidates/apply");
        } catch (CategoryNotFoundException e) {
            ra.addFlashAttribute("categoryErrorMessage", e.getMessage());
            ra.addFlashAttribute("candidate", candidateAddBindingModel);
            modelAndView.setViewName("redirect:/candidates/apply");
        }

        return modelAndView;
    }

    @GetMapping("/by-category/{c}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView allByCategory(@PathVariable String c, ModelAndView modelAndView) {

        modelAndView.addObject("candidates", this.candidateService.findAllCandidatesWithCategory(c));
        modelAndView.setViewName("candidate/candidates-by-category");

        return modelAndView;
    }


    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView makeEmployee(@PathVariable String id, ModelAndView modelAndView) {

        CandidateViewModel candidateViewModel = this.modelMapper
                .map(this.candidateService.findById(id), CandidateViewModel.class);
        modelAndView.addObject("candidate", candidateViewModel);
        modelAndView.setViewName("candidate/candidate-details");

        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView deleteCandidate(@PathVariable String id, ModelAndView modelAndView) {
        CandidateViewModel candidateViewModel = this.modelMapper
                .map(this.candidateService.findById(id), CandidateViewModel.class);

        modelAndView.addObject("candidate", candidateViewModel);
        modelAndView.setViewName("candidate/candidate-delete");

        return modelAndView;
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteCandidatePost(@PathVariable String id) throws IOException {
        this.candidateService
                .deleteById(id, false);

        return "redirect:/home";
    }

    @GetMapping("/fetch")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public List<String> fetchCategories() {
        return this.categoryService
                .findAll();
    }

    @ExceptionHandler({CandidateNotFoundException.class, CategoryNotFoundException.class, IOException.class})
    public ModelAndView handleException(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("error/custom-error");
        modelAndView.addObject("message", exception.getMessage());

        return modelAndView;
    }

}
