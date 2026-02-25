package com.example.demo.application.bootstrap;

import com.example.demo.permission.Permission;
import com.example.demo.permission.PermissionService;
import com.example.demo.role.NameRole;
import com.example.demo.role.Role;
import com.example.demo.role.RoleService;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdminBootstrapTest {
    private final UserSecRepository userSecRepository = Mockito.mock(UserSecRepository.class);
    private final RoleService roleService = Mockito.mock(RoleService.class);
    private final PermissionService permissionService = Mockito.mock(PermissionService.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final String adminUsername = "admin";
    private final String adminPassword = "admin1234";

    private final AdminBootstrap adminBootstrap = new AdminBootstrap(userSecRepository, roleService, permissionService, passwordEncoder, adminUsername, adminPassword);

    private final ArgumentCaptor<UserSec> userSecCaptor = ArgumentCaptor.forClass(UserSec.class);

    @Test
    @DisplayName("Genera un usuario administrador si no existe ninguno en la BD")
    void run_userSecNoExist_userSecCreate() throws Exception {
        String adminPasswordEncode = "admin1234";
        Permission p1 = new Permission(1L,"CREATE");
        Permission p2 = new Permission(2L,"READ");
        Permission p3 = new Permission(3L,"UPDATE");
        Permission p4 = new Permission(4L,"DELETE");
        Role adminRole = new Role(15L, NameRole.ADMINISTRADOR, new HashSet<>(Set.of(p1,p2,p3,p4)));

        when(userSecRepository.findUserEntityByUsername(adminUsername)).thenReturn(Optional.empty());
        when(permissionService.findOrCreatePermission("CREATE")).thenReturn(p1);
        when(permissionService.findOrCreatePermission("READ")).thenReturn(p2);
        when(permissionService.findOrCreatePermission("UPDATE")).thenReturn(p3);
        when(permissionService.findOrCreatePermission("DELETE")).thenReturn(p4);
        when(roleService.findOrCreateRole(new HashSet<>(Set.of(p1, p2, p3 ,p4)), NameRole.ADMINISTRADOR)).thenReturn(adminRole);
        when(passwordEncoder.encode(adminPassword)).thenReturn(adminPasswordEncode);

        adminBootstrap.run();

        verify(userSecRepository).save(userSecCaptor.capture());

        UserSec userSecSave = userSecCaptor.getValue();

        assertEquals(userSecSave.getUsername(), adminUsername);
        assertEquals(userSecSave.getPassword(), adminPasswordEncode);
        assertTrue(userSecSave.isEnabled());
        assertTrue(userSecSave.isAccountNotExpired());
        assertTrue(userSecSave.isAccountNotLocked());
        assertTrue(userSecSave.isCredentialNotExpired());
        assertTrue(userSecSave.getRolesList().contains(adminRole));
        assertEquals(userSecSave.getSubjectType(), SubjectType.ADMINISTRADOR);
    }

    @Test
    @DisplayName("Genera un log de informacion si ya existe un usuario administrador")
    void run_userSecExist_logRun() throws Exception {

        when(userSecRepository.findUserEntityByUsername(adminUsername)).thenReturn(Optional.of(new UserSec()));

        adminBootstrap.run();

        verify(permissionService, never()).findOrCreatePermission(any());
        verify(roleService, never()).findOrCreateRole(any(), any());
        verify(userSecRepository, never()).save(any());
    }
}
