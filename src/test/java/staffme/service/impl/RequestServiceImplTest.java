package staffme.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import staffme.error.RequestNotFoundException;
import staffme.model.entity.Request;
import staffme.model.service.RequestServiceModel;
import staffme.repository.RequestRepository;
import staffme.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    RequestRepository requestRepository;
    @Mock
    EmployeeService employeeService;

    ModelMapper modelMapper;

    RequestServiceImpl requestService;

    @BeforeEach
    public void before() {
        modelMapper = new ModelMapper();

        requestService = new RequestServiceImpl(this.modelMapper, this.requestRepository, this.employeeService);
    }

    @Test
    void makeRequest() {
        String employeeId = "id";

        RequestServiceModel requestServiceModel = new RequestServiceModel(employeeId, "Jordan", "088774636238", 2,
                "requestId");

        List<Request> fakeRequestRepository = new ArrayList<>();

        Mockito.when(requestRepository.saveAndFlush(any(Request.class)))
                .thenAnswer(invocation -> {
                    fakeRequestRepository.add((Request) invocation.getArguments()[0]);
                    return fakeRequestRepository.get(0);
                });
        LocalDateTime time = LocalDateTime.now();
        RequestServiceModel actual = requestService.makeRequest(employeeId, requestServiceModel);
        requestServiceModel.setExpiresTime(time.plusDays(requestServiceModel.getDays()));

        assertEquals(requestServiceModel.getEmployeeId(), actual.getEmployeeId());
        assertEquals(requestServiceModel.getDays(), actual.getDays());
        assertEquals(requestServiceModel.getClientName(), actual.getClientName());
        assertEquals(requestServiceModel.getId(), actual.getId());
        assertEquals(requestServiceModel.getPhoneNumber(), actual.getPhoneNumber());
        assertTrue(time.isBefore(actual.getExpiresTime()) || time.isEqual(actual.getExpiresTime()));
    }

    @Test
    void completeRequestsShouldDeleteCorrectly_whenRequestIsExpired() {

        Request expiredRequest = new Request();
        expiredRequest.setExpiresTime(LocalDateTime.now());

        List<Request> expiredRequests = new ArrayList<>();
        expiredRequests.add(expiredRequest);

        Mockito.when(requestRepository.findAll()).thenReturn(expiredRequests);

        requestService.completeRequests();

        Mockito.verify(requestRepository, times(1)).delete(expiredRequests.get(0));
    }

    @Test
    void findAllShouldReturnCorrectData() {
        RequestServiceModel requestServiceModel = new RequestServiceModel("employeeId", "Jordan", "088774636238", 2,
                "requestId");
        List<RequestServiceModel> expected = new ArrayList<>();
        expected.add(requestServiceModel);

        Request request = new Request("employeeId", "Jordan", "088774636238", 2);
        List<Request> requests = new ArrayList<>();
        requests.add(request);

        Mockito.when(requestRepository.findAll()).thenReturn(requests);

        List<RequestServiceModel> actual = requestService.findAll();

        assertEquals(expected.get(0).getPhoneNumber(), actual.get(0).getPhoneNumber());
        assertEquals(expected.get(0).getEmployeeId(), actual.get(0).getEmployeeId());
        assertEquals(expected.get(0).getClientName(), actual.get(0).getClientName());
        assertEquals(expected.get(0).getDays(), actual.get(0).getDays());
    }

    @Test()
    void completeRequestShouldThrowException_whenRequestDoNotExists() {

        String id = "id";

        Mockito.when(requestRepository.findById(id)).thenReturn(Optional.empty());

        RequestNotFoundException thrown = assertThrows(
                RequestNotFoundException.class,
                () -> requestService.completeRequest(id));

        assertTrue(thrown.getMessage().contains("Request with the given id was not found!"));
    }

    @Test
    void completeRequestShouldDeleteCorrectly_whenExistingRequestIsPassed() {

        String id = "id";
        Request expiredRequest = new Request();
        expiredRequest.setExpiresTime(LocalDateTime.now());

        Mockito.when(requestRepository.findById(id)).thenReturn(Optional.of(expiredRequest));

        requestService.completeRequest(id);

        Mockito.verify(requestRepository, times(1)).delete(expiredRequest);
    }
}