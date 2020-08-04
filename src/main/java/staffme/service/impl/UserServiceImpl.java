package staffme.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import staffme.error.EmailExistsException;
import staffme.error.PasswordsNotEqualException;
import staffme.error.UsernameExistsException;
import staffme.model.entity.Role;
import staffme.model.entity.User;
import staffme.model.service.UserServiceModel;
import staffme.repository.UserRepository;
import staffme.service.RoleService;
import staffme.service.UserService;
import staffme.service.UserValidationService;

import javax.management.relation.RoleNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserValidationService userValidationService;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService,
                           ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder, UserValidationService userValidationService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userValidationService = userValidationService;
    }

    @Override
    public UserServiceModel registerUser(UserServiceModel userServiceModel) {
        this.roleService.seedRolesInDB();

        if (this.userRepository.count() == 0) {
            userServiceModel.setAuthorities(this.roleService.findAllRoles());
        } else {
            if (!userValidationService.isValid(userServiceModel)) {
                if (!userValidationService.isUsernameFree(userServiceModel.getUsername())) {
                    throw new UsernameExistsException("User with username " + userServiceModel.getUsername() + " already exists");
                }
                if (!userValidationService.isEmailFree(userServiceModel.getEmail())) {
                    throw new EmailExistsException("User with email " + userServiceModel.getEmail() + " already exists");
                }
                if (!userValidationService.arePasswordsValid(userServiceModel.getPassword(), userServiceModel.getConfirmPassword())) {
                    throw new PasswordsNotEqualException("Passwords do not match!");
                }
            }

            userServiceModel.setAuthorities(new LinkedHashSet<>());
            userServiceModel
                    .getAuthorities()
                    .add(this.roleService.findByAuthority("ROLE_USER"));
        }

        User user = this.modelMapper.map(userServiceModel, User.class);
        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));

        return this.modelMapper
                .map(this.userRepository.saveAndFlush(user), UserServiceModel.class);
    }


    @Override
    public UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword) {
        User user = this.userRepository.findByUsername(userServiceModel.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username does not exist!"));

        if (!this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordsNotEqualException("Incorrect old password!");
        }

        user.setPassword(!"".equals(userServiceModel.getPassword())
                ? this.bCryptPasswordEncoder.encode(userServiceModel.getPassword())
                : user.getPassword()
        );

        return this.modelMapper.map(this.userRepository.saveAndFlush(user), UserServiceModel.class);
    }

    @Override
    public List<UserServiceModel> findAllUsersWithRole(String role) {

        Role dbRole = this.modelMapper.map(this.roleService.findByAuthority(role), Role.class);

        List<User> users = this.userRepository.findAll();

        List<User> foundUsers = new ArrayList<>();

        for (User user : users) {
            if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ROOT"))) {
                continue;
            }
            for (Role authority : user.getAuthorities()) {
                if (authority.getAuthority().equals(dbRole.getAuthority())) {
                    foundUsers.add(user);
                    break;
                }
            }
        }
        return foundUsers
                .stream()
                .map(user -> this.modelMapper.map(user, UserServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserServiceModel findByUsername(String username) {
        return this.userRepository
                .findByUsername(username)
                .map(u -> this.modelMapper.map(u, UserServiceModel.class))
                .orElseThrow(() -> new UsernameNotFoundException("Username does not exist!"));
    }

    @Override
    public void changeRole(String username) throws RoleNotFoundException {
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username does not exist!"));

        String role = user.getAuthorities().iterator().next().getAuthority();
        user.getAuthorities().clear();

        switch (role) {
            case "ROLE_USER":
                user.getAuthorities()
                        .add(this.modelMapper.map(this.roleService.findByAuthority("ROLE_ADMIN"), Role.class));
                break;
            case "ROLE_ADMIN":
                user.getAuthorities()
                        .add(this.modelMapper.map(this.roleService.findByAuthority("ROLE_USER"), Role.class));
                break;

            default:
                throw new RoleNotFoundException("role does not exist!");
        }
        this.userRepository.saveAndFlush(user);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username does not exist"));
    }
}
