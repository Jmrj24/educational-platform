package com.example.demo.userSec;

import com.example.demo.userSec.dto.UserSecRequestDTO;
import com.example.demo.userSec.dto.UserSecResponseDTO;
import com.example.demo.userSec.dto.UserSecUpdateDTO;

import java.util.Optional;
import java.util.Set;

public interface IUserSecService {
    UserSecResponseDTO saveUserSec(UserSecRequestDTO userSecRequestDTO, SubjectType subjectType, Optional<Long> idSubject);
    Set<UserSecResponseDTO> findAllUserSecs();
    UserSecResponseDTO findByIdUserSec(Long id);
    void deleteByIdUserSec(Long id);
    UserSecResponseDTO updateUserSec(Long id, UserSecUpdateDTO userSecUpdateDTO);
}
