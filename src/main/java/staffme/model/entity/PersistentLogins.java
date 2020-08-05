package staffme.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "persistent_logins")
public class PersistentLogins {

    private String series;
    private String username;
    private String token;
    private LocalDateTime lastUsed;

    public PersistentLogins() {
    }
    @Id
    @NotNull
    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }
    @Column(name = "username", nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    @Column(name = "token", nullable = false)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    @Column(name = "last_used", nullable = false)
    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }
}
