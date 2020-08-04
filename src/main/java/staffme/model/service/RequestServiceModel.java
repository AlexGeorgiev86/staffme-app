package staffme.model.service;

import java.time.LocalDateTime;

public class RequestServiceModel extends BaseServiceModel {

    private String employeeId;
    private String clientName;
    private String phoneNumber;
    private Integer days;
    private LocalDateTime expiresTime;
    private String id;


    public RequestServiceModel() {
    }

    public RequestServiceModel(String employeeId, String clientName, String phoneNumber, Integer days, String id) {
        this.employeeId = employeeId;
        this.clientName = clientName;
        this.phoneNumber = phoneNumber;
        this.days = days;
        this.id = id;
    }

    public LocalDateTime getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(LocalDateTime expiresTime) {
        this.expiresTime = expiresTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
