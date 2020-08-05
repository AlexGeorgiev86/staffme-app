package staffme.service;

import staffme.model.service.UserServiceModel;

public interface UserValidationService {

    boolean isValid(UserServiceModel userServiceModel);

    boolean isEmailFree(String email);

    boolean arePasswordsValid(String password, String confirmPassword);

    boolean isUsernameFree(String username);
}
