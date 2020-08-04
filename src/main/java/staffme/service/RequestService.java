package staffme.service;

import staffme.model.service.RequestServiceModel;

import java.util.List;

public interface RequestService {

    RequestServiceModel makeRequest(String employeeId, RequestServiceModel requestServiceModel);
    void completeRequests();
    List<RequestServiceModel> findAll();
    void completeRequest(String id);


}
