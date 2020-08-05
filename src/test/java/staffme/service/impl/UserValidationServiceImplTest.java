package staffme.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import staffme.model.service.UserServiceModel;
import staffme.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceImplTest {

    @Mock
    UserRepository userRepository;

    UserValidationServiceImpl userValidationService;

    UserServiceModel userServiceModelEqualPasswords;

    UserServiceModel userServiceModelDifferentPasswords;

    @BeforeEach
    public void before() {
        userValidationService = new UserValidationServiceImpl(this.userRepository);
        userServiceModelDifferentPasswords = new UserServiceModel("Pesho", "111", "123", "email", null);
        userServiceModelEqualPasswords = new UserServiceModel("Pesho", "123", "123", "email", null);
    }

    @Test
    void isValidShouldReturnTrue_whenAllMethodsAreTrue() {

        Mockito.when(userRepository.existsByEmail(userServiceModelEqualPasswords.getEmail())).thenReturn(false);
        Mockito.when(userRepository.existsByUsername(userServiceModelEqualPasswords.getUsername())).thenReturn(false);

        assertTrue(userValidationService.isValid(userServiceModelEqualPasswords));
    }

    @Test
    void isValidShouldReturnFalse_whenOneMethodIsFalse() {

        assertFalse(userValidationService.isValid(userServiceModelDifferentPasswords));
    }

    @Test
    void arePasswordsValidShouldReturnTrue_whenPasswordsAreEqual() {

        String password = "pass";
        String confirmPassword = "pass";

        assertTrue(userValidationService.arePasswordsValid(password, confirmPassword));
    }

    @Test
    void arePasswordsValidShouldReturnFalse_whenPasswordsAreNotEqual() {

        String password = "pass1";
        String confirmPassword = "pass2";

        assertFalse(userValidationService.arePasswordsValid(password, confirmPassword));
    }

}