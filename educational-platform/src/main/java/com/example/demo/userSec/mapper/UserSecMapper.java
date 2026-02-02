package com.example.demo.userSec.mapper;

import com.example.demo.role.Role;
import com.example.demo.role.dto.RoleResponseDTO;
import com.example.demo.role.mapper.RoleMapper;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.dto.UserSecRequestDTO;
import com.example.demo.userSec.dto.UserSecResponseDTO;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserSecMapper {
    private final RoleMapper roleMapper;

    public UserSecMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    public UserSecResponseDTO toUserSecResponse(UserSec userSec) {
        Set<RoleResponseDTO> roleResponseDTO = new HashSet<>();
        for(Role r: userSec.getRolesList()) {
            roleResponseDTO.add(this.roleMapper.toRoleResponse(r));
        }
        return new UserSecResponseDTO(userSec.getId(), userSec.getUsername(), userSec.getPassword(),
                userSec.isEnabled(), userSec.isAccountNotExpired(), userSec.isAccountNotLocked(),
                userSec.isCredentialNotExpired(), roleResponseDTO, userSec.getSubjectType(),
                userSec.getIdSubject()!=null ? userSec.getIdSubject() : null);
    }
}
