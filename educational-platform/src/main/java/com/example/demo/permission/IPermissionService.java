package com.example.demo.permission;

import com.example.demo.permission.dto.PermissionRequestDTO;
import com.example.demo.permission.dto.PermissionResponseDTO;
import com.example.demo.permission.dto.PermissionUpdateDTO;

import java.util.Set;

public interface IPermissionService {
    PermissionResponseDTO savePermission(PermissionRequestDTO permissionDTO);
    Set<PermissionResponseDTO> findAllPermissions();
    PermissionResponseDTO findByIdPermission(Long id);
    void deleteByIdPermission(Long id);
    PermissionResponseDTO updatePermission(Long id, PermissionUpdateDTO updateDTO);
}
