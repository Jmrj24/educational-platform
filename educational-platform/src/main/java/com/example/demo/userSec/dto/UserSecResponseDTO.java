package com.example.demo.userSec.dto;

import com.example.demo.role.dto.RoleResponseDTO;
import com.example.demo.userSec.SubjectType;

import java.util.Set;

public record UserSecResponseDTO(Long id, String username, String password,  boolean enabled,
                                  boolean accountNotExpired,  boolean accountNotLocked,
                                  boolean credentialNotExpired,  Set<RoleResponseDTO> rolesList,
                                  SubjectType subjectType, Long idSubject) {
}
