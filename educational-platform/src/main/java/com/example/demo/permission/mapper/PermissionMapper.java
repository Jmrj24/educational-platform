package com.example.demo.permission.mapper;

import com.example.demo.permission.Permission;
import com.example.demo.permission.dto.PermissionRequestDTO;
import com.example.demo.permission.dto.PermissionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {
    public PermissionResponseDTO toPermissionResponse (Permission permission) {
        return new PermissionResponseDTO(permission.getId(), permission.getPermissionName());
    }
}
