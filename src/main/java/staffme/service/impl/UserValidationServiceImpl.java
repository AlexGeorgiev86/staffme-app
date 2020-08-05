package staffme.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import staffme.model.service.UserServiceModel;
import staffme.repository.UserRepository;
import staffme.service.UserValidationService;
@Service
public class UserValidationServiceImpl implements UserValidationService {

    private final UserRepository userRepository;

    @Autowired
    public UserValidationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(UserServiceModel userServiceModel) {
        return this.isEmailFree(userServiceModel.getEmail()) &&
                this.arePasswordsValid(userServiceModel.getPassword(), userServiceModel.getConfirmPassword()) &&
                this.isUsernameFree(userServiceModel.getUsername());
    }
    @Override
    public boolean arePasswordsValid(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
    @Override
    public boolean isUsernameFree(String username) {
        return !userRepository.existsByUsername(username);
    }
    @Override
    public boolean isEmailFree(String email) {
        return !userRepository.existsByEmail(email);
    }
}
