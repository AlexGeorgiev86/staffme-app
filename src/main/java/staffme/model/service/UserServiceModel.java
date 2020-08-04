package staffme.model.service;

import java.util.Set;

public class UserServiceModel extends BaseServiceModel {

    private String username;
    private String password;
    private String ConfirmPassword;
    private String email;
    private Set<RoleServiceModel> authorities;

    public UserServiceModel() {
    }

    public UserServiceModel(String username, String password, String confirmPassword,
                            String email, Set<RoleServiceModel> authorities) {
        this.username = username;
        this.password = password;
        ConfirmPassword = confirmPassword;
        this.email = email;
        this.authorities = authorities;
    }

    public String getConfirmPassword() {
        return ConfirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        ConfirmPassword = confirmPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleServiceModel> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<RoleServiceModel> authorities) {
        this.authorities = authorities;
    }
}
