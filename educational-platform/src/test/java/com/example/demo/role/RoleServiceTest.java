package com.example.demo.role;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.permission.Permission;
import com.example.demo.permission.PermissionRepository;
import com.example.demo.permission.PermissionTestDataFactory;
import com.example.demo.permission.dto.PermissionResponseDTO;
import com.example.demo.role.dto.RoleRequestDTO;
import com.example.demo.role.dto.RoleResponseDTO;
import com.example.demo.role.dto.RoleUpdateDTO;
import com.example.demo.role.mapper.RoleMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private RoleMapper roleMapper;
    @InjectMocks
    private RoleService roleService;

    @Captor
    private ArgumentCaptor<Role> roleCaptor;

    @Test
    @DisplayName("Lanza una excepcion si el nombre del role ingresado, ya esta registrado")
    void saveRole_nameRoleDuplicated_runException() {
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO(NameRole.ADMINISTRADOR, new HashSet<>());

        when(roleRepository.findRolEntityByRole(roleRequestDTO.role())).thenReturn(Optional.of(new Role()));

        assertThrows(ConflictException.class, () -> {
            roleService.saveRole(roleRequestDTO);
        });

        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Guarda al el Rol en la base de datos, si todo sale bien")
    void saveRole_roleRequestValid_saveRoleSuccessful() {
        RoleRequestDTO roleRequestDTO = new RoleRequestDTO(NameRole.ADMINISTRADOR, Set.of(5L));
        Permission permission = PermissionTestDataFactory.createPermission();
        PermissionResponseDTO permissionResponseDTO = PermissionTestDataFactory.createPermissionResponse(permission);
        RoleResponseDTO roleResponseExpect = RoleTestDataFactory.createRoleResponseFromRequest(roleRequestDTO, Set.of(permissionResponseDTO));

        when(roleRepository.findRolEntityByRole(roleRequestDTO.role())).thenReturn(Optional.empty());
        when(permissionRepository.findById(any(Long.class))).thenReturn(Optional.of(permission));
        when(roleRepository.save(any(Role.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(roleMapper.toRoleResponse(any(Role.class))).thenReturn(roleResponseExpect);

        RoleResponseDTO roleResponseResult = roleService.saveRole(roleRequestDTO);

        verify(roleRepository).save(roleCaptor.capture());
        Role role = roleCaptor.getValue();
        assertEquals(role.getRole(), roleRequestDTO.role());
        assertTrue(role.getPermissionsList().stream()
                        .map(Permission::getId)
                        .anyMatch(p-> p.equals(permission.getId())));
        assertEquals(roleResponseExpect, roleResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un lista de Roles DTO si existen")
    void findAllRoles_rolesExist_returnRoles() {
        Role role = RoleTestDataFactory.createRole();
        List<Role> listRoles = new ArrayList<>(List.of(role));
        RoleResponseDTO roleResponseDTO = RoleTestDataFactory.createRoleResponse(role, new HashSet<>());
        Set<RoleResponseDTO> listRolesResponseExpect = new HashSet<>(List.of(roleResponseDTO));

        when(roleRepository.findAll()).thenReturn(listRoles);
        when(roleMapper.toRoleResponse(role)).thenReturn(roleResponseDTO);

        Set<RoleResponseDTO> listRolesResponseResult = roleService.findAllRoles();

        assertEquals(listRolesResponseExpect, listRolesResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un lista vacia si no hay registro de Roles")
    void findAllRoles_rolesNoExist_returnListEmpty() {
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        Set<RoleResponseDTO> listRolesResponseResult = roleService.findAllRoles();

        assertNotNull(listRolesResponseResult);
        assertTrue(listRolesResponseResult.isEmpty());
    }

    @Test
    @DisplayName("Debe devolver un rol si el Id existe")
    void findByIdRole_roleExist_returnRoleDTO() {
        Long id = 741L;
        Role role = RoleTestDataFactory.createRole();
        RoleResponseDTO roleResponseExpect = RoleTestDataFactory.createRoleResponse(role, new HashSet<>());

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));
        when(roleMapper.toRoleResponse(role)).thenReturn(roleResponseExpect);

        RoleResponseDTO roleResponseResult = roleService.findByIdRole(id);

        assertEquals(roleResponseExpect, roleResponseResult);
        verify(roleRepository).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar una excepcion al no existir el id del rol")
    void findByIdRole_roleNoExist_runException() {
        Long id = 1L;

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            roleService.findByIdRole(id);
        });
    }

    @Test
    @DisplayName("Debe lanzar una exception si no existe el ID")
    void updateRole_roleNoExist_runException() {
        Long idRole = 4L;
        RoleUpdateDTO roleUpdate = new RoleUpdateDTO(NameRole.ADMINISTRADOR, new HashSet<>());

        when(roleRepository.findById(idRole)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            roleService.updateRole(idRole, roleUpdate);
        });

        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar el rol si todo sale bien")
    void updateRole_roleExist_roleUpdateSuccessful() {
        Long idRole = 741L;
        Role role = RoleTestDataFactory.createRole();
        RoleUpdateDTO roleUpdate = new RoleUpdateDTO(NameRole.PROFESOR, new HashSet<>());

        when(roleRepository.findById(idRole)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);
        RoleResponseDTO roleResponseExpected = RoleTestDataFactory.createRoleResponse(role, new HashSet<>());
        when(roleMapper.toRoleResponse(role)).thenReturn(roleResponseExpected);

        RoleResponseDTO roleResponseResult = roleService.updateRole(idRole, roleUpdate);

        assertEquals(role.getRole(), roleUpdate.getRole());
        verify(roleRepository).save(role);
        assertEquals(roleResponseExpected, roleResponseResult);
    }
}