package staffme.web;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import staffme.model.view.RequestViewModel;
import staffme.service.RequestService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;
    private final ModelMapper modelMapper;

    public RequestController(RequestService requestService, ModelMapper modelMapper) {
        this.requestService = requestService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView viewAll(ModelAndView modelAndView) {

        List<RequestViewModel> requests = this.requestService.findAll()
                .stream()
                .map(request -> this.modelMapper.map(request, RequestViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("requests", requests);
        modelAndView.setViewName("request/requests");

        return modelAndView;

    }

    @PostMapping("/complete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String complete(String id) {

        this.requestService.completeRequest(id);

        return "redirect:/requests/all";
    }


}
