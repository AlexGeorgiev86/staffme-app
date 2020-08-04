package staffme.model.service;

public class RoleServiceModel extends BaseServiceModel {

    public RoleServiceModel(String authority) {
        this.authority = authority;
    }

    private String authority;

    public RoleServiceModel() {
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
