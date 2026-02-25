package com.example.demo.application.teacher;

import com.example.demo.exception.ConflictException;
import com.example.demo.teacher.ITeacherService;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.dto.TeacherRequestDTO;
import com.example.demo.teacher.dto.TeacherResponseDTO;
import com.example.demo.teacher.mapper.TeacherMapper;
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

public class CreateTeacherAccountTest {
    private final ITeacherService teacherService = Mockito.mock(ITeacherService.class);
    private final IUserSecService userSecService = Mockito.mock(IUserSecService.class);
    private final TeacherMapper teacherMapper = Mockito.mock(TeacherMapper.class);

    private final CreateTeacherAccount createTeacherAccount = new CreateTeacherAccount(teacherService, userSecService, teacherMapper);

    @Test
    @DisplayName("Registrar un nuevo profesor junto con su cuenta de acceso de seguridad")
    void createTeacherAndAccount_validTeacherRequest_returnSuccessfulResponse() {
        TeacherRequestDTO request = this.createValidRequest();
        UserSecRequestDTO userSecRequestDTO = this.createUserRequest(request);
        UserSecResponseDTO userSecResponseDTO = this.createUserResponse(request);
        Teacher teacher = this.createTeacher(request);
        TeacherResponseDTO teacherResponseExpected = this.createTeacherResponse(request);

        when(teacherService.saveTeacher(request.name(), request.mail(), request.specialty())).thenReturn(teacher);
        when(userSecService.saveUserSec(userSecRequestDTO, SubjectType.PROFESOR, Optional.of(teacher.getId()))).thenReturn(userSecResponseDTO);
        when(teacherMapper.toTeacherResponse(teacher)).thenReturn(teacherResponseExpected);

        TeacherResponseDTO teacherResponseResult = createTeacherAccount.createTeacherAndAccount(request);

        verify(teacherService).saveTeacher(request.name(), request.mail(), request.specialty());
        verify(userSecService).saveUserSec(userSecRequestDTO, SubjectType.PROFESOR, Optional.of(teacher.getId()));
        assertEquals(teacherResponseExpected, teacherResponseResult);
    }

    @Test
    @DisplayName("Debe fallar al no crear un profesor valido")
    void createTeacherAndAccount_failNewTeacher_returnSuccessfulResponse() {
        TeacherRequestDTO request = this.createValidRequest();

        doThrow(new ConflictException("El email ya esta registrado")).when(teacherService).saveTeacher(any(), any(), any());

        assertThrows(ConflictException.class, () -> {
            TeacherResponseDTO teacherResponseResult = createTeacherAccount.createTeacherAndAccount(request);
        });
        verify(userSecService, never()).saveUserSec(any(), any(), any());
        verify(teacherMapper,  never()).toTeacherResponse(any());
    }

    @Test
    @DisplayName("Debe fallar al no crear una cuenta profesor valida")
    void createTeacherAndAccount_failNewAccountTeacher_returnSuccessfulResponse() {
        TeacherRequestDTO request = this.createValidRequest();
        Teacher teacher = this.createTeacher(request);

        when(teacherService.saveTeacher(any(), any(), any())).thenReturn(teacher);
        doThrow(new ConflictException("El Username ya esta registrado")).when(userSecService).saveUserSec(any(), any(), any());

        assertThrows(ConflictException.class, () -> {
            TeacherResponseDTO teacherResponseResult = createTeacherAccount.createTeacherAndAccount(request);
        });
        verify(teacherService).saveTeacher(any(), any(), any());
        verify(teacherMapper,  never()).toTeacherResponse(any());
    }

    private TeacherRequestDTO createValidRequest() {
        return new TeacherRequestDTO("Mateo28", "1234", true, true, true, true, new HashSet<>(), "Mateo", "mateo@mail.com", "Matematicas");
    }

    private UserSecRequestDTO createUserRequest(TeacherRequestDTO request) {
        return new UserSecRequestDTO(request.username(), request.password(), request.enabled(),
                request.accountNotExpired(), request.accountNotLocked(),request.credentialNotExpired(), new HashSet<>());
    }

    private UserSecResponseDTO createUserResponse(TeacherRequestDTO request) {
        return new UserSecResponseDTO(25L, request.username(), request.password(), request.enabled(),
                request.accountNotExpired(), request.accountNotLocked(),request.credentialNotExpired(), new HashSet<>(), SubjectType.PROFESOR, 78L);
    }

    private Teacher createTeacher(TeacherRequestDTO request) {
        return new Teacher(78L, request.name(), request.mail(), request.specialty(), new ArrayList<>());
    }

    private TeacherResponseDTO createTeacherResponse(TeacherRequestDTO request) {
        return new TeacherResponseDTO(78L, request.name(), request.mail(), request.specialty(), new ArrayList<>());
    }
}