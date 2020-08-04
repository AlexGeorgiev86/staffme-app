package staffme.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class Request extends BaseEntity {

    private String employeeId;
    private String clientName;
    private String phoneNumber;
    private Integer days;
    private LocalDateTime expiresTime;

    public Request() {
    }

    public Request(String employeeId, String clientName, String phoneNumber, Integer days) {
        this.employeeId = employeeId;
        this.clientName = clientName;
        this.phoneNumber = phoneNumber;
        this.days = days;
    }

    @Column(name = "expires_time", nullable = false)
    public LocalDateTime getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(LocalDateTime expiresTime) {
        this.expiresTime = expiresTime;
    }

    @Column(name = "employee_id", nullable = false, unique = true)
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    @Column(name = "client_name", nullable = false)
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    @Column(name = "phone_number", nullable = false)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    @Column(name = "days", nullable = false)
    @Min(value = 1)
    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
