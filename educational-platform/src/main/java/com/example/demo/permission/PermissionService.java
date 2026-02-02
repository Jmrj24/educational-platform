package com.example.demo.permission;

import com.example.demo.exception.ConflictException;
import com.example.demo.permission.dto.PermissionRequestDTO;
import com.example.demo.permission.dto.PermissionResponseDTO;
import com.example.demo.permission.dto.PermissionUpdateDTO;
import com.example.demo.permission.mapper.PermissionMapper;
import com.example.demo.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionService implements IPermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionService (PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public PermissionResponseDTO savePermission(PermissionRequestDTO permissionRequestDTO) {
        if(this.permissionRepository.findPermissionEntityByPermissionName(permissionRequestDTO.permissionName()).isPresent()) {
            throw new ConflictException("El nombre del Permiso ya está registrado.");
        }
        Permission permissionNew = new Permission();
        permissionNew.setPermissionName(permissionRequestDTO.permissionName());
        return this.permissionMapper.toPermissionResponse(this.permissionRepository.save(permissionNew));
    }

    @Override
    public Set<PermissionResponseDTO> findAllPermissions() {
        return this.permissionRepository.findAll().stream()
                .map(this.permissionMapper::toPermissionResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public PermissionResponseDTO findByIdPermission(Long id) {
        return this.permissionMapper.toPermissionResponse(this.findByIdEntityPermission(id));
    }

    @Override
    public void deleteByIdPermission(Long id) {
        this.permissionRepository.deleteById(id);
    }

    @Override
    public PermissionResponseDTO updatePermission(Long id, PermissionUpdateDTO updateDTO) {
         Permission updatePermission = this.findByIdEntityPermission(id);
         updatePermission.setPermissionName(updateDTO.getPermissionName());
        return this.permissionMapper.toPermissionResponse(this.permissionRepository.save(updatePermission));
    }

    private Permission findByIdEntityPermission(Long id) {
        return this.permissionRepository.findById(id).orElseThrow(() -> new NotFoundException("Permiso no Encontrado"));
    }

    public Permission findOrCreatePermission(String name) {
        return this.permissionRepository.findPermissionEntityByPermissionName(name).orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setPermissionName(name);
                    return this.permissionRepository.save(permission);
                });
    }
}
