package staffme.service;

import staffme.model.service.RoleServiceModel;

import java.util.Set;

public interface RoleService  {

    void seedRolesInDB();

    Set<RoleServiceModel> findAllRoles();

    RoleServiceModel findByAuthority(String authority);
}
