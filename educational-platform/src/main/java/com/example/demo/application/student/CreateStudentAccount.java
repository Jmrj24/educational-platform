package com.example.demo.application.student;

import com.example.demo.userSec.IUserSecService;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.dto.UserSecRequestDTO;
import com.example.demo.student.IStudentService;
import com.example.demo.student.Student;
import com.example.demo.student.dto.StudentRequestDTO;
import com.example.demo.student.dto.StudentResponseDTO;
import com.example.demo.student.mapper.StudentMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class CreateStudentAccount {
    private final IStudentService studentService;
    private final IUserSecService userSecService;
    private final StudentMapper studentMapper;

    public CreateStudentAccount(IStudentService studentService, IUserSecService userSecService, StudentMapper studentMapper) {
        this.studentService = studentService;
        this.userSecService = userSecService;
        this.studentMapper = studentMapper;
    }

    public StudentResponseDTO createStudentAndAccount(StudentRequestDTO studentRequestDTO) {
        Student newStudent = this.studentService.saveStudent(studentRequestDTO.name(), studentRequestDTO.mail());
        this.userSecService.saveUserSec(
                new UserSecRequestDTO(studentRequestDTO.username(), studentRequestDTO.password(),
                studentRequestDTO.enabled(), studentRequestDTO.accountNotExpired(), studentRequestDTO.accountNotLocked(),
                studentRequestDTO.credentialNotExpired(), studentRequestDTO.rolesListIds()),
                SubjectType.ESTUDIANTE, Optional.of(newStudent.getId())
        );
        return studentMapper.toStudentResponse(newStudent);
    }
}
