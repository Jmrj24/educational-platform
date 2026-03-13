package com.example.demo.permission;

import com.example.demo.permission.dto.PermissionResponseDTO;

public class PermissionTestDataFactory {
    public static Permission createPermission() {
        return new Permission(5L, "Create");
    }

    public static PermissionResponseDTO createPermissionResponse(Permission permission) {
        return new PermissionResponseDTO(permission.getId(), permission.getPermissionName());
    }
}