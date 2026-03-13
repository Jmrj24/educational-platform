package com.example.demo.permission;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.permission.dto.PermissionRequestDTO;
import com.example.demo.permission.dto.PermissionResponseDTO;
import com.example.demo.permission.dto.PermissionUpdateDTO;
import com.example.demo.permission.mapper.PermissionMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTest {
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private PermissionMapper permissionMapper;
    @InjectMocks
    private PermissionService permissionService;

    @Captor
    private ArgumentCaptor<Permission> permissionCaptor;

    @Test
    @DisplayName("Lanza una excepcion si el nombre del permiso ingresado, ya esta registrado")
    void savePermission_namePermissionDuplicated_runException() {
        PermissionRequestDTO permissionRequest = new PermissionRequestDTO("NombrePermiso");

        when(permissionRepository.findPermissionEntityByPermissionName(permissionRequest.permissionName())).thenReturn(Optional.of(new Permission()));

        assertThrows(ConflictException.class, () -> {
            permissionService.savePermission(permissionRequest);
        });

        verify(permissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Guarda al el permiso en la base de datos, si todo sale bien")
    void savePermission_permissionRequestValid_savePermissionSuccessful() {
        PermissionRequestDTO permissionRequest = new PermissionRequestDTO("NombrePermiso");
        PermissionResponseDTO permissionResponseExpect = new PermissionResponseDTO(963L, permissionRequest.permissionName());

        when(permissionRepository.findPermissionEntityByPermissionName(permissionRequest.permissionName())).thenReturn(Optional.empty());
        when(permissionRepository.save(any(Permission.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(permissionMapper.toPermissionResponse(any(Permission.class))).thenReturn(permissionResponseExpect);

        PermissionResponseDTO permissionResponseResult = permissionService.savePermission(permissionRequest);

        verify(permissionRepository).save(permissionCaptor.capture());
        Permission permission = permissionCaptor.getValue();
        assertEquals(permission.getPermissionName(), permissionRequest.permissionName());
        assertEquals(permissionResponseExpect, permissionResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un lista de Permisos DTO si existen")
    void findAllPermissions_permissionsExist_returnPermissions() {
        Permission permission = PermissionTestDataFactory.createPermission();
        List<Permission> listPermissions = new ArrayList<>(List.of(permission));
        PermissionResponseDTO permissionResponse = PermissionTestDataFactory.createPermissionResponse(permission);
        Set<PermissionResponseDTO> listPermissionResponseExpect = new HashSet<>(Set.of(permissionResponse));

        when(permissionRepository.findAll()).thenReturn(listPermissions);
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(permissionResponse);

        Set<PermissionResponseDTO> listPermissionResponseResult = permissionService.findAllPermissions();

        assertEquals(listPermissionResponseExpect, listPermissionResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un lista vacia si no hay registro de Roles")
    void findAllPermissions_permissionsNoExist_returnListEmpty() {
        when(permissionRepository.findAll()).thenReturn(Collections.emptyList());

        Set<PermissionResponseDTO> listPermissionResponseResult = permissionService.findAllPermissions();

        assertNotNull(listPermissionResponseResult);
        assertTrue(listPermissionResponseResult.isEmpty());
    }

    @Test
    @DisplayName("Debe devolver un permiso si el Id existe")
    void findByIdPermission_permissionExist_returnPermissionDTO() {
        Long id = 5L;
        Permission permission = PermissionTestDataFactory.createPermission();
        PermissionResponseDTO permissionResponseExpect = PermissionTestDataFactory.createPermissionResponse(permission);

        when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(permissionResponseExpect);

        PermissionResponseDTO permissionResponseResult = permissionService.findByIdPermission(id);

        assertEquals(permissionResponseExpect, permissionResponseResult);
        verify(permissionRepository).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar una excepcion al no existir el id del permiso")
    void findByIdPermission_permissionNoExist_runException() {
        Long id = 1L;

        when(permissionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            permissionService.findByIdPermission(id);
        });
    }

    @Test
    @DisplayName("Debe lanzar una exception si no existe el ID")
    void updatePermission_permissionNoExist_runException() {
        Long idPermission = 4L;
        PermissionUpdateDTO permissionUpdate = new PermissionUpdateDTO("Nuevo Name");

        when(permissionRepository.findById(idPermission)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            permissionService.updatePermission(idPermission, permissionUpdate);
        });

        verify(permissionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar el permiso si todo sale bien")
    void updatePermission_permissionExist_permissionUpdateSuccessful() {
        Long idPermission = 5L;
        Permission permission = PermissionTestDataFactory.createPermission();
        PermissionUpdateDTO permissionUpdate = new PermissionUpdateDTO("Nuevo Name");

        when(permissionRepository.findById(idPermission)).thenReturn(Optional.of(permission));
        when(permissionRepository.save(permission)).thenReturn(permission);
        PermissionResponseDTO permissionResponseExpected = PermissionTestDataFactory.createPermissionResponse(permission);
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(permissionResponseExpected);

        PermissionResponseDTO permissionResponseResult = permissionService.updatePermission(idPermission, permissionUpdate);

        assertEquals(permission.getPermissionName(), permissionUpdate.getPermissionName());
        verify(permissionRepository).save(permission);
        assertEquals(permissionResponseExpected, permissionResponseResult);
    }
}