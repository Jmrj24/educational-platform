package com.example.demo.application.student;

import com.example.demo.exception.ConflictException;
import com.example.demo.student.IStudentService;
import com.example.demo.student.Student;
import com.example.demo.student.StudentTestDataFactory;
import com.example.demo.student.dto.StudentRequestDTO;
import com.example.demo.student.dto.StudentResponseDTO;
import com.example.demo.student.mapper.StudentMapper;
import com.example.demo.userSec.IUserSecService;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.dto.UserSecRequestDTO;
import com.example.demo.userSec.dto.UserSecResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateStudentAccountTest {
    @Mock
    private IStudentService studentService;
    @Mock
    private IUserSecService userSecService;
    @Mock
    private StudentMapper studentMapper;
    @InjectMocks
    private CreateStudentAccount createStudentAccount;

    @Test
    @DisplayName("Registrar un nuevo estudiante junto con su cuenta de acceso de seguridad")
    void createStudentAndAccount_validStudentRequest_returnSuccessfulResponse() {
        Long idStudent = 87L, idUserSec = 52L;
        StudentRequestDTO request = StudentTestDataFactory.createValidRequest();
        Student student = StudentTestDataFactory.createStudentFromRequest(request, idStudent);
        StudentResponseDTO studentResponseExpected = StudentTestDataFactory.createStudentResponseDTO(student, Collections.emptyList());
        UserSecResponseDTO userSecResponseDTO = this.createUserSecResponse(request, idStudent, idUserSec);
        UserSecRequestDTO userSecRequestDTO = this.createUserSecRequest(request);

        when(studentService.saveStudent(request.name(), request.mail())).thenReturn(student);
        when(userSecService.saveUserSec(userSecRequestDTO, SubjectType.ESTUDIANTE, Optional.of(student.getId()))).thenReturn(userSecResponseDTO);
        when(studentMapper.toStudentResponse(student)).thenReturn(studentResponseExpected);

        StudentResponseDTO studentResponseResult = createStudentAccount.createStudentAndAccount(request);

        verify(studentService).saveStudent(request.name(), request.mail());
        verify(userSecService).saveUserSec(userSecRequestDTO, SubjectType.ESTUDIANTE, Optional.of(student.getId()));
        assertEquals(studentResponseExpected, studentResponseResult);
    }

    @Test
    @DisplayName("Debe fallar al no crear un estudiante valido")
    void createStudentAndAccount_failNewStudent_returnSuccessfulResponse() {
        StudentRequestDTO request = StudentTestDataFactory.createValidRequest();

        doThrow(new ConflictException("El email ya esta registrado")).when(studentService).saveStudent(any(), any());

        assertThrows(ConflictException.class, () -> {
            StudentResponseDTO studentResponseResult = createStudentAccount.createStudentAndAccount(request);
        });

        verify(userSecService, never()).saveUserSec(any(), any(), any());
        verify(studentMapper,  never()).toStudentResponse(any());
    }

    @Test
    @DisplayName("Debe fallar al no crear una cuenta estudiante valida")
    void createStudentAndAccount_failNewAccountStudent_returnSuccessfulResponse() {
        Long idStudent = 87L;
        StudentRequestDTO request = StudentTestDataFactory.createValidRequest();
        Student student = StudentTestDataFactory.createStudentFromRequest(request, idStudent);

        when(studentService.saveStudent(any(), any())).thenReturn(student);
        doThrow(new ConflictException("El Username ya esta registrado")).when(userSecService).saveUserSec(any(), any(), any());

        assertThrows(ConflictException.class, () -> {
            StudentResponseDTO studentResponseResult = createStudentAccount.createStudentAndAccount(request);
        });
        verify(studentService).saveStudent(any(), any());
        verify(studentMapper,  never()).toStudentResponse(any());
    }

    private UserSecRequestDTO createUserSecRequest(StudentRequestDTO request) {
        return new UserSecRequestDTO(
                request.username(),
                request.password(),
                request.enabled(),
                request.accountNotExpired(),
                request.accountNotLocked(),
                request.credentialNotExpired(),
                new HashSet<>());
    }

    private UserSecResponseDTO createUserSecResponse(StudentRequestDTO request, Long idStudent, Long idUserSec) {
        return new UserSecResponseDTO(
                idUserSec, request.username(),
                request.password(),
                request.enabled(),
                request.accountNotExpired(),
                request.accountNotLocked(),
                request.credentialNotExpired(),
                new HashSet<>(),
                SubjectType.ESTUDIANTE,
                idStudent);
    }
}