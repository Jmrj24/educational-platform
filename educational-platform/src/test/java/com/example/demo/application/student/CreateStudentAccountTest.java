package com.example.demo.application.student;

import com.example.demo.exception.ConflictException;
import com.example.demo.student.IStudentService;
import com.example.demo.student.Student;
import com.example.demo.student.dto.StudentRequestDTO;
import com.example.demo.student.dto.StudentResponseDTO;
import com.example.demo.student.mapper.StudentMapper;
import com.example.demo.userSec.IUserSecService;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.dto.UserSecRequestDTO;
import com.example.demo.userSec.dto.UserSecResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateStudentAccountTest {
    private final IStudentService studentService = Mockito.mock(IStudentService.class);
    private final IUserSecService userSecService = Mockito.mock(IUserSecService.class);
    private final StudentMapper studentMapper = Mockito.mock(StudentMapper.class);

    private final CreateStudentAccount createStudentAccount = new CreateStudentAccount(studentService, userSecService, studentMapper);

    @Test
    @DisplayName("Registrar un nuevo estudiante junto con su cuenta de acceso de seguridad")
    void createStudentAndAccount_validStudentRequest_returnSuccessfulResponse() {
        StudentRequestDTO request = this.createValidRequest();
        Student student = this.createStudent(request);
        StudentResponseDTO studentResponseExpected = this.createStudentResponse(request);
        UserSecResponseDTO userSecResponseDTO = this.createUserSecResponse(request);
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
        StudentRequestDTO request = this.createValidRequest();

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
        StudentRequestDTO request = this.createValidRequest();
        Student student = this.createStudent(request);

        when(studentService.saveStudent(any(), any())).thenReturn(student);
        doThrow(new ConflictException("El Username ya esta registrado")).when(userSecService).saveUserSec(any(), any(), any());

        assertThrows(ConflictException.class, () -> {
            StudentResponseDTO studentResponseResult = createStudentAccount.createStudentAndAccount(request);
        });
        verify(studentService).saveStudent(any(), any());
        verify(studentMapper,  never()).toStudentResponse(any());
    }

    private StudentRequestDTO createValidRequest() {
        return new StudentRequestDTO("Maria85", "1234", true, true, true, true, new HashSet<>(), "Maria", "maria@mail.com");
    }

    private UserSecRequestDTO createUserSecRequest(StudentRequestDTO request) {
        return new UserSecRequestDTO(request.username(), request.password(), request.enabled(),
                request.accountNotExpired(), request.accountNotLocked(),request.credentialNotExpired(), new HashSet<>());
    }

    private UserSecResponseDTO createUserSecResponse(StudentRequestDTO request) {
        return new UserSecResponseDTO(52L, request.username(), request.password(), request.enabled(), request.accountNotExpired(),
                request.accountNotLocked(),request.credentialNotExpired(), new HashSet<>(), SubjectType.ESTUDIANTE, 87L);
    }

    private Student createStudent(StudentRequestDTO request) {
        return new Student(87L, request.name(), request.mail(), new ArrayList<>());
    }

    private StudentResponseDTO createStudentResponse(StudentRequestDTO request) {
        return new StudentResponseDTO(87L, request.name(), request.mail(), new ArrayList<>());
    }
}