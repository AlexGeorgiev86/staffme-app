package staffme.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import staffme.error.RequestNotFoundException;
import staffme.model.entity.Request;
import staffme.model.service.RequestServiceModel;
import staffme.repository.RequestRepository;
import staffme.service.EmployeeService;
import staffme.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {

    private final ModelMapper modelMapper;
    private final RequestRepository requestRepository;
    private final EmployeeService employeeService;

    public RequestServiceImpl(ModelMapper modelMapper, RequestRepository requestRepository, EmployeeService employeeService) {
        this.modelMapper = modelMapper;
        this.requestRepository = requestRepository;
        this.employeeService = employeeService;
    }

    @Override
    public RequestServiceModel makeRequest(String employeeId, RequestServiceModel requestServiceModel) {

        Request request = this.modelMapper.map(requestServiceModel, Request.class);
        request.setEmployeeId(employeeId);
        request.setExpiresTime(LocalDateTime.now().plusDays(requestServiceModel.getDays()));
        this.employeeService.updateAvailability(employeeId);

        return this.modelMapper.map(this.requestRepository.saveAndFlush(request), RequestServiceModel.class);

    }
    @Scheduled(fixedRate = 6000)
    @Override
    public void completeRequests() {

        List<Request> expiredRequests = this.requestRepository.findAll()
                .stream()
                .filter(request -> LocalDateTime.now().isAfter(request.getExpiresTime()))
                .collect(Collectors.toList());

        if(!expiredRequests.isEmpty()) {
            expiredRequests.forEach(request -> {
                this.employeeService.updateAvailability(request.getEmployeeId());
                this.requestRepository.delete(request);
            });
        }

    }

    @Override
    public List<RequestServiceModel> findAll() {
        return this.requestRepository.findAll()
                .stream()
                .map(request -> this.modelMapper.map(request, RequestServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public void completeRequest(String id) {
        Request request = this.requestRepository.findById(id).
                orElseThrow(() -> new RequestNotFoundException("Request with the given id was not found!"));
        this.employeeService.updateAvailability(request.getEmployeeId());
        this.requestRepository.delete(request);

    }


}
