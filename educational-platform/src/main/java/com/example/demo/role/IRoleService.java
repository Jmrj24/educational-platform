package com.example.demo.role;

import com.example.demo.role.dto.RoleRequestDTO;
import com.example.demo.role.dto.RoleResponseDTO;
import com.example.demo.role.dto.RoleUpdateDTO;

import java.util.Set;

public interface IRoleService {
    RoleResponseDTO saveRole(RoleRequestDTO roleRequestDTO);
    Set<RoleResponseDTO> findAllRoles();
    RoleResponseDTO findByIdRole(Long id);
    void deleteByIdRole(Long id);
    RoleResponseDTO updateRole(Long id, RoleUpdateDTO roleUpdateDTO);
}
