package com.example.demo.role;

import com.example.demo.exception.ConflictException;
import com.example.demo.permission.PermissionRepository;
import com.example.demo.role.dto.RoleRequestDTO;
import com.example.demo.role.dto.RoleResponseDTO;
import com.example.demo.role.dto.RoleUpdateDTO;
import com.example.demo.role.mapper.RoleMapper;
import com.example.demo.exception.NotFoundException;
import com.example.demo.permission.Permission;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    public RoleService (RoleRepository roleRepository, PermissionRepository permissionRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public RoleResponseDTO saveRole(RoleRequestDTO roleRequestDTO) {
        if(this.roleRepository.findRolEntityByRole(roleRequestDTO.role()).isPresent()) {
            throw new ConflictException("El nombre del Rol ya está registrado.");
        }
        Role roleNew = new Role();
        roleNew.setRole(roleRequestDTO.role());
        roleNew.setPermissionsList(this.findPermissions(roleRequestDTO.permissionsListIds()));
        return this.roleMapper.toRoleResponse(this.roleRepository.save(roleNew));
    }

    @Override
    public Set<RoleResponseDTO> findAllRoles() {
        return this.roleRepository.findAll().stream()
                .map(this.roleMapper::toRoleResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public RoleResponseDTO findByIdRole(Long id) {
        return this.roleMapper.toRoleResponse(this.findByIdEntityRole(id));
    }

    @Override
    public void deleteByIdRole(Long id) {
        this.roleRepository.deleteById(id);
    }

    @Override
    public RoleResponseDTO updateRole(Long id, RoleUpdateDTO roleUpdateDTO) {
        Role updateRole = this.findByIdEntityRole(id);
        if(roleUpdateDTO.getRole()!=null) {
            updateRole.setRole(roleUpdateDTO.getRole());
        }
        if(!roleUpdateDTO.getPermissionsListIds().isEmpty()) {
            updateRole.setPermissionsList(this.findPermissions(roleUpdateDTO.getPermissionsListIds()));
        }
        return this.roleMapper.toRoleResponse(this.roleRepository.save(updateRole));
    }

    private Role findByIdEntityRole(Long id) {
        return this.roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Rol no Encontrado"));
    } 

    private Set<Permission> findPermissions(Set<Long> permissionsListIds) {
        Set<Permission> permissionsList = new HashSet<>();
        for(Long permissionId:permissionsListIds) {
            permissionsList.add(this.permissionRepository.findById(permissionId).orElseThrow(() -> new NotFoundException("Permiso no Encontrado")));
        }
        return permissionsList;
    }

    public Role findOrCreateRole(Set<Permission> permissionsList, NameRole nameRole) {
        return this.roleRepository.findRolEntityByRole(nameRole).orElseGet(() -> {
            Role rol = new Role();
            rol.setRole(nameRole);
            rol.setPermissionsList(permissionsList);
            return this.roleRepository.save(rol);
        });
    }
}
