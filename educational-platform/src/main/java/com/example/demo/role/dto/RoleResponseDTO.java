package com.example.demo.role.dto;

import com.example.demo.permission.dto.PermissionResponseDTO;
import com.example.demo.role.NameRole;

import java.util.Set;

public record RoleResponseDTO (Long id, NameRole role, Set<PermissionResponseDTO> permissionsList) {
}
