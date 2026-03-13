package com.example.demo.userSec;

import com.example.demo.role.dto.RoleResponseDTO;
import com.example.demo.userSec.dto.UserSecRequestDTO;
import com.example.demo.userSec.dto.UserSecResponseDTO;

import java.util.HashSet;
import java.util.Set;

public class UserSecTestDataFactory {
    public static UserSecRequestDTO createUserSecRequest() {
        return new UserSecRequestDTO(
                "JuanProfesor",
                "1234",
                true,
                true,
                true,
                true,
                new HashSet<>()
        );
    }

    public static UserSecResponseDTO createUserSecResponseFromRequest(UserSecRequestDTO userSecRequest, Set<RoleResponseDTO> listRoles, SubjectType subjectType, Long idSubject) {
        return new UserSecResponseDTO(
                53L,
                userSecRequest.username(),
                userSecRequest.password(),
                userSecRequest.enabled(),
                userSecRequest.accountNotExpired(),
                userSecRequest.accountNotLocked(),
                userSecRequest.credentialNotExpired(),
                listRoles,
                subjectType,
                idSubject
        );
    }

    public static UserSec createUserSec() {
        return new UserSec(
                258L,
                "JuanProfesor",
                "1234",
                true,
                true,
                true,
                true,
                new HashSet<>(),
                SubjectType.PROFESOR,
                45L

        );
    }

    public static UserSecResponseDTO createUserSecResponse(UserSec userSec, Set<RoleResponseDTO> listRoles) {
        return new UserSecResponseDTO(
                userSec.getId(),
                userSec.getUsername(),
                userSec.getPassword(),
                userSec.isEnabled(),
                userSec.isAccountNotExpired(),
                userSec.isAccountNotLocked(),
                userSec.isCredentialNotExpired(),
                listRoles,
                userSec.getSubjectType(),
                userSec.getIdSubject()
        );
    }
}
