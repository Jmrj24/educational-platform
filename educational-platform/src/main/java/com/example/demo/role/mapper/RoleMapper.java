package com.example.demo.role.mapper;

import com.example.demo.permission.Permission;
import com.example.demo.permission.dto.PermissionResponseDTO;
import com.example.demo.permission.mapper.PermissionMapper;
import com.example.demo.role.Role;
import com.example.demo.role.dto.RoleRequestDTO;
import com.example.demo.role.dto.RoleResponseDTO;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class RoleMapper {
    private final PermissionMapper permissionMapper;

    public RoleMapper(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    public RoleResponseDTO toRoleResponse(Role role) {
        Set<PermissionResponseDTO> permissionListDto = new HashSet<>();
        for(Permission p:role.getPermissionsList()) {
            permissionListDto.add(this.permissionMapper.toPermissionResponse(p));
        }
        return new RoleResponseDTO(role.getId(), role.getRole(), permissionListDto);
    }
}
