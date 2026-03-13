package com.example.demo.role;

import com.example.demo.permission.dto.PermissionResponseDTO;
import com.example.demo.role.dto.RoleRequestDTO;
import com.example.demo.role.dto.RoleResponseDTO;

import java.util.HashSet;
import java.util.Set;

public class RoleTestDataFactory {
    public static RoleResponseDTO createRoleResponseFromRequest(RoleRequestDTO roleRequestDTO, Set<PermissionResponseDTO> listPermissions) {
        return new RoleResponseDTO(
                456L,
                roleRequestDTO.role(),
                listPermissions
        );
    }

    public static Role createRole() {
        return new Role(
                741L,
                NameRole.ADMINISTRADOR,
                new HashSet<>()
        );
    }

    public static RoleResponseDTO createRoleResponse(Role role, Set<PermissionResponseDTO> listPermission) {
        return new RoleResponseDTO(
                role.getId(),
                role.getRole(),
                listPermission
        );
    }
}
