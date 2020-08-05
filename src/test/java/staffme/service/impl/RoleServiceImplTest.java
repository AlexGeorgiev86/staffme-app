package staffme.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import staffme.model.entity.Role;
import staffme.model.service.RoleServiceModel;
import staffme.repository.RoleRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    RoleRepository roleRepository;

    ModelMapper modelMapper;

    RoleServiceImpl roleServiceImpl;

    Set<RoleServiceModel> roleServiceModels;

    RoleServiceModel roleServiceModel;

    List<Role> roles;

    Role role;

    @BeforeEach
    public void before() {
        modelMapper = new ModelMapper();
        roleServiceImpl = new RoleServiceImpl(roleRepository, modelMapper);

        roleServiceModel = new RoleServiceModel("ROLE_USER");
        role = new Role("ROLE_USER");

        roleServiceModels = new LinkedHashSet<>();
        roleServiceModels.add(roleServiceModel);

        roles = new ArrayList<>();
        roles.add(role);
    }

    @Test
    void seedRolesInDBSHouldSeedRoles_whenRepositoryIsEmpty() {

        List<Role> fakeRoleRepository = new ArrayList<>();

        Mockito.when(roleRepository.saveAndFlush(any(Role.class)))
                .thenAnswer(invocation -> {
                    fakeRoleRepository.add((Role) invocation.getArguments()[0]);
                    return null;
                });

        Mockito.when(roleRepository.count()).thenReturn(0L);

        roleServiceImpl.seedRolesInDB();
        int expected = 3;

        assertEquals(expected, fakeRoleRepository.size());
        assertEquals("ROLE_USER", fakeRoleRepository.get(0).getAuthority());
        assertEquals("ROLE_ROOT", fakeRoleRepository.get(1).getAuthority());
        assertEquals("ROLE_ADMIN", fakeRoleRepository.get(2).getAuthority());
    }

    @Test
    void findAllRolesShouldReturnCorrectData() {

        Mockito.when(roleRepository.findAll()).thenReturn(roles);

        Set<RoleServiceModel> actual = roleServiceImpl.findAllRoles();

        assertEquals(roleServiceModels.iterator().next().getAuthority(), actual.iterator().next().getAuthority());

    }

    @Test
    void findByAuthorityShouldReturnCorrectData() {

        Mockito.when(roleRepository.findByAuthority(role.getAuthority())).thenReturn(role);

        RoleServiceModel actual = roleServiceImpl.findByAuthority(role.getAuthority());

        assertEquals(roleServiceModel.getAuthority(), actual.getAuthority());
    }
}