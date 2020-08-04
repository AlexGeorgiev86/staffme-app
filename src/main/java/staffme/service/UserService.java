package staffme.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import staffme.model.service.UserServiceModel;

import javax.management.relation.RoleNotFoundException;
import java.util.List;


public interface UserService extends UserDetailsService {

    UserServiceModel registerUser(UserServiceModel userServiceModel);

    UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword);

    List<UserServiceModel> findAllUsersWithRole(String role);
    UserServiceModel findByUsername(String username);

    void changeRole(String username) throws RoleNotFoundException;

}
