package staffme.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import staffme.error.EmailExistsException;
import staffme.error.PasswordsNotEqualException;
import staffme.error.UsernameExistsException;
import staffme.model.entity.Role;
import staffme.model.entity.User;
import staffme.model.service.RoleServiceModel;
import staffme.model.service.UserServiceModel;
import staffme.repository.UserRepository;
import staffme.service.RoleService;
import staffme.service.UserValidationService;

import javax.management.relation.RoleNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleService roleService;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    UserValidationService userValidationService;

    ModelMapper modelMapper;

    UserServiceImpl userService;

    Set<RoleServiceModel> userRoleServiceModels;

    Set<Role> roles;

    @BeforeEach
    public void before() {
        modelMapper = new ModelMapper();

        userService = new UserServiceImpl(this.userRepository, this.roleService, this.modelMapper,
                this.bCryptPasswordEncoder, this.userValidationService);

        userRoleServiceModels = new LinkedHashSet<>();
        userRoleServiceModels.add(new RoleServiceModel("ROLE_USER"));

        roles = new LinkedHashSet<>();
        roles.add(new Role("ROLE_USER"));
    }

    @Test
    void registersUserWithCorrectData_returnsCorrectRegisteredUser() {


        RoleServiceModel roleServiceModel = new RoleServiceModel();
        roleServiceModel.setAuthority("ROLE_USER");

        UserServiceModel userServiceToBeRegistered = new UserServiceModel("Pesho", "123", "123", "email", null);

        Mockito.when(roleService.findAllRoles()).thenReturn(userRoleServiceModels);
        Mockito.when(userRepository.count()).thenReturn(Long.valueOf(0));
        Mockito.when(bCryptPasswordEncoder.encode(userServiceToBeRegistered.getPassword())).thenReturn("123");
        Mockito.when(userRepository.saveAndFlush(any(User.class))).thenReturn(new User());


        userService.registerUser(userServiceToBeRegistered);


        assertEquals(userServiceToBeRegistered.getAuthorities().iterator().next().getAuthority(),
                roleServiceModel.getAuthority());


    }

    @Test()
    void registerUserShouldThrowException_whenUsernameExists() {

        UserServiceModel userServiceToBeRegistered = new UserServiceModel("Pesho", "123", "123", "email", userRoleServiceModels);
        userServiceToBeRegistered.setId("id");

        Mockito.when(userRepository.count()).thenReturn(Long.valueOf(1));
        Mockito.when(userValidationService.isValid(userServiceToBeRegistered)).thenReturn(false);
        Mockito.when(userValidationService.isUsernameFree(userServiceToBeRegistered.getUsername())).thenReturn(false);

        UsernameExistsException thrown = assertThrows(
                UsernameExistsException.class,
                () -> userService.registerUser(userServiceToBeRegistered));

        assertTrue(thrown.getMessage().contains("Pesho already exists"));
    }

    @Test()
    void registerUserShouldThrowException_whenEmailExists() {

        UserServiceModel userServiceToBeRegistered = new UserServiceModel("Pesho", "123", "123", "email", userRoleServiceModels);
        userServiceToBeRegistered.setId("id");

        Mockito.when(userRepository.count()).thenReturn(Long.valueOf(1));
        Mockito.when(userValidationService.isValid(userServiceToBeRegistered)).thenReturn(false);
        Mockito.when(userValidationService.isUsernameFree(userServiceToBeRegistered.getUsername())).thenReturn(true);
        Mockito.when(userValidationService.isEmailFree(userServiceToBeRegistered.getEmail())).thenReturn(false);

        EmailExistsException thrown = assertThrows(
                EmailExistsException.class,
                () -> userService.registerUser(userServiceToBeRegistered));

        assertTrue(thrown.getMessage().contains("email already exists"));
    }

    @Test()
    void registerUserShouldThrowException_whenPasswordsDoesNotMatch() {

        UserServiceModel userServiceToBeRegistered = new UserServiceModel("Pesho", "123", "123", "email", userRoleServiceModels);
        userServiceToBeRegistered.setId("id");

        Mockito.when(userRepository.count()).thenReturn(Long.valueOf(1));
        Mockito.when(userValidationService.isValid(userServiceToBeRegistered)).thenReturn(false);
        Mockito.when(userValidationService.isUsernameFree(userServiceToBeRegistered.getUsername())).thenReturn(true);
        Mockito.when(userValidationService.isEmailFree(userServiceToBeRegistered.getEmail())).thenReturn(true);
        Mockito.when(userValidationService
                .arePasswordsValid(userServiceToBeRegistered.getPassword(), userServiceToBeRegistered.getConfirmPassword()))
                .thenReturn(false);

        PasswordsNotEqualException thrown = assertThrows(
                PasswordsNotEqualException.class,
                () -> userService.registerUser(userServiceToBeRegistered));

        assertTrue(thrown.getMessage().contains("Passwords do not match!"));
    }


    @Test
    void editUserProfileShouldThrowException_whenUsernameNotExists() {

        UserServiceModel userServiceToBeEdited = new UserServiceModel("Pesho", "123", "123", "email", userRoleServiceModels);
        userServiceToBeEdited.setId("id");

        String oldPassword = "222";

        Mockito.when(userRepository.findByUsername(userServiceToBeEdited.getUsername())).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.editUserProfile(userServiceToBeEdited, oldPassword));

        assertTrue(thrown.getMessage().contains("Username does not exist"));
    }

    @Test
    void editUserProfileShouldThrowException_whenPasswordsNotMatch() {

        UserServiceModel userServiceToBeEdited = new UserServiceModel("Pesho", "123", "123", "email", userRoleServiceModels);
        userServiceToBeEdited.setId("id");

        User userToBeEdited = new User("Pesho", "123", "email", roles);
        userToBeEdited.setId("id");

        String oldPassword = "222";

        Mockito.when(userRepository
                .findByUsername(userServiceToBeEdited.getUsername()))
                .thenReturn(Optional.of(userToBeEdited));
        Mockito.when(bCryptPasswordEncoder.matches(oldPassword, userToBeEdited.getPassword())).thenReturn(false);

        PasswordsNotEqualException thrown = assertThrows(
                PasswordsNotEqualException.class,
                () -> userService.editUserProfile(userServiceToBeEdited, oldPassword));

        assertTrue(thrown.getMessage().contains("Incorrect old password!"));
    }

    @Test
    void editUserProfileShouldWorkCorrectly_whenDataIsValid() {

        UserServiceModel userServiceToBeEdited = new UserServiceModel("Pesho", "123", "123", "email", userRoleServiceModels);
        userServiceToBeEdited.setId("id");

        User userToBeEdited = new User("Pesho", "222", "email", roles);
        userToBeEdited.setId("id");

        String oldPassword = "222";

        Mockito.when(userRepository
                .findByUsername(userServiceToBeEdited.getUsername()))
                .thenReturn(Optional.of(userToBeEdited));
        Mockito.when(bCryptPasswordEncoder.matches(oldPassword, userToBeEdited.getPassword())).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.encode(userServiceToBeEdited.getPassword())).thenReturn(userServiceToBeEdited.getPassword());
        Mockito.when(userRepository.saveAndFlush(any(User.class))).thenReturn(new User());

        userService.editUserProfile(userServiceToBeEdited, oldPassword);

        assertEquals(userServiceToBeEdited.getPassword(),userToBeEdited.getPassword());
    }

    @Test
    void findAllUsersWithRole_returnsListWithCorrectUsers() {

        RoleServiceModel roleServiceModel = new RoleServiceModel();
        roleServiceModel.setAuthority("ROLE_USER");

        Role role = new Role();
        role.setAuthority("ROLE_USER");

        String roleStr = "ROLE_USER";

        UserServiceModel userServiceWithRole = new UserServiceModel("Pesho", "123", "123", "email", userRoleServiceModels);
        userServiceWithRole.setId("id");

        User userWithRole = new User("Pesho", "123", "email", roles);
        userWithRole.setId("id");

        List<User> userList = new ArrayList<>();
        userList.add(userWithRole);

        Mockito.when(roleService.findByAuthority(roleStr)).thenReturn(roleServiceModel);
        Mockito.when(userRepository.findAll()).thenReturn(userList);

        List<UserServiceModel> actual = userService.findAllUsersWithRole(roleStr);
        List<UserServiceModel> expected = new ArrayList<>();
        expected.add(userServiceWithRole);

        assertEquals(expected.get(0).getAuthorities().iterator().next().getAuthority(),
                actual.get(0).getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void findAllUsersWithRole_returnsListWithCorrectUsersAndNoRootUser() {

        RoleServiceModel roleServiceModel = new RoleServiceModel();
        roleServiceModel.setAuthority("ROLE_USER");

        Role role = new Role();
        role.setAuthority("ROLE_USER");

        String roleStr = "ROLE_USER";

        Set<Role> rolesWithRootUser = new LinkedHashSet<>();
        rolesWithRootUser.add(new Role("ROLE_USER"));
        rolesWithRootUser.add(new Role("ROLE_ROOT"));

        UserServiceModel userServiceWithRole = new UserServiceModel("Pesho", "123", "123", "email", userRoleServiceModels);
        userServiceWithRole.setId("id");

        User userWithRole = new User("Pesho", "123", "email", roles);
        userWithRole.setId("id");

        User userWithRootRole = new User("Gosho", "1234", "email2", rolesWithRootUser);
        userWithRootRole.setId("Gosho_id");

        List<User> userList = new ArrayList<>();
        userList.add(userWithRole);
        userList.add(userWithRootRole);

        Mockito.when(roleService.findByAuthority(roleStr)).thenReturn(roleServiceModel);
        Mockito.when(userRepository.findAll()).thenReturn(userList);

        List<UserServiceModel> actual = userService.findAllUsersWithRole(roleStr);
        List<UserServiceModel> expected = new ArrayList<>();
        expected.add(userServiceWithRole);

        assertEquals(expected.get(0).getAuthorities().iterator().next().getAuthority(),
                actual.get(0).getAuthorities().iterator().next().getAuthority());
        assertEquals(expected.size(), actual.size());
    }

    @Test
    void findByUsernameShouldWorkCorrectly_whenDataIsValid() {

        UserServiceModel userServiceToBeFound = new UserServiceModel("Pesho", "123", "123", "email", userRoleServiceModels);
        userServiceToBeFound.setId("id");

        User userToBeFound = new User("Pesho", "123", "email", roles);
        userToBeFound.setId("id");

        Mockito.when(userRepository
                .findByUsername(userServiceToBeFound.getUsername()))
                .thenReturn(Optional.of(userToBeFound));

        UserServiceModel actual = userService.findByUsername(userServiceToBeFound.getUsername());

        assertEquals(userServiceToBeFound.getUsername(), actual.getUsername());
    }

    @Test
    void findByUsernameShouldThrowException_whenUserDoNotExists() {

        String invalidUsername = "Stamat";

        Mockito.when(userRepository
                .findByUsername(invalidUsername))
                .thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.findByUsername(invalidUsername));

        assertTrue(thrown.getMessage().contains("Username does not exist"));
    }

    @Test()
    void changeRoleShouldThrowException_whenRoleNotExists() {

        Set<Role> rolesWithRootUser = new LinkedHashSet<>();
        rolesWithRootUser.add(new Role("ROLE_ROOT"));

        User userToBeChanged = new User("Pesho", "123", "email", rolesWithRootUser);
        userToBeChanged.setId("id");

        Mockito.when(userRepository.findByUsername(userToBeChanged.getUsername())).thenReturn(Optional.of(userToBeChanged));

        RoleNotFoundException thrown = assertThrows(
                RoleNotFoundException.class,
                () -> userService.changeRole(userToBeChanged.getUsername()));

        assertTrue(thrown.getMessage().contains("role does not exist!"));
    }

    @Test
    void changeRoleShouldReturnAdmin_whenUserWithRoleUserIsPassed() throws RoleNotFoundException {

        RoleServiceModel roleServiceModelAdmin = new RoleServiceModel();
        roleServiceModelAdmin.setAuthority("ROLE_ADMIN");

        User userToBeChanged = new User("Pesho", "123", "email", roles);
        userToBeChanged.setId("id");

        Mockito.when(userRepository
                .findByUsername(userToBeChanged.getUsername()))
                .thenReturn(Optional.of(userToBeChanged));

        Mockito.when(roleService.findByAuthority(roleServiceModelAdmin.getAuthority())).thenReturn(roleServiceModelAdmin);

        userService.changeRole(userToBeChanged.getUsername());

        assertEquals(userToBeChanged.getAuthorities().iterator().next().getAuthority(), roleServiceModelAdmin.getAuthority());
    }

    @Test
    void changeRoleShouldReturnUser_whenUserWithRoleAdminIsPassed() throws RoleNotFoundException {

        RoleServiceModel roleServiceModelUser = new RoleServiceModel();
        roleServiceModelUser.setAuthority("ROLE_USER");

        Set<Role> rolesWithAdmin = new LinkedHashSet<>();
        rolesWithAdmin.add(new Role("ROLE_ADMIN"));

        User userToBeChanged = new User("Pesho", "123", "email", rolesWithAdmin);
        userToBeChanged.setId("id");

        Mockito.when(userRepository
                .findByUsername(userToBeChanged.getUsername()))
                .thenReturn(Optional.of(userToBeChanged));

        Mockito.when(roleService.findByAuthority(roleServiceModelUser.getAuthority())).thenReturn(roleServiceModelUser);

        userService.changeRole(userToBeChanged.getUsername());

        assertEquals(userToBeChanged.getAuthorities().iterator().next().getAuthority(), roleServiceModelUser.getAuthority());
    }
    @Test
    void loadUserByUsernameShouldThrowException_whenUserDoNotExists() {

        String invalidUsername = "Stamat";

        Mockito.when(userRepository
                .findByUsername(invalidUsername))
                .thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(invalidUsername));

        assertTrue(thrown.getMessage().contains("Username does not exist"));
    }


}