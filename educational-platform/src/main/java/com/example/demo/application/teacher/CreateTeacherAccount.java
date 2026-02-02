package com.example.demo.application.teacher;


import com.example.demo.userSec.IUserSecService;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.dto.UserSecRequestDTO;
import com.example.demo.teacher.ITeacherService;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.dto.TeacherRequestDTO;
import com.example.demo.teacher.dto.TeacherResponseDTO;
import com.example.demo.teacher.mapper.TeacherMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class CreateTeacherAccount {
    private final ITeacherService teacherService;
    private final IUserSecService userSecService;
    private final TeacherMapper teacherMapper;

    public CreateTeacherAccount(ITeacherService teacherService, IUserSecService userSecService, TeacherMapper teacherMapper) {
        this.teacherService = teacherService;
        this.userSecService = userSecService;
        this.teacherMapper = teacherMapper;
    }

    public TeacherResponseDTO createTeacherAndAccount(TeacherRequestDTO teacherRequestDTO) {
        Teacher newTeacher = this.teacherService.saveTeacher(teacherRequestDTO.name(), teacherRequestDTO.mail(), teacherRequestDTO.specialty());
        this.userSecService.saveUserSec(
                new UserSecRequestDTO(teacherRequestDTO.username(), teacherRequestDTO.password(),
                        teacherRequestDTO.enabled(), teacherRequestDTO.accountNotExpired(), teacherRequestDTO.accountNotLocked(),
                        teacherRequestDTO.credentialNotExpired(), teacherRequestDTO.rolesListIds()),
                SubjectType.PROFESOR, Optional.of(newTeacher.getId())
        );
        return teacherMapper.toTeacherResponse(newTeacher);
    }
}
