package com.example.demo.application.teacher;

import com.example.demo.exception.ConflictException;
import com.example.demo.teacher.ITeacherService;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.TeacherTestDataFactory;
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
        Long idTeacher = 78L, idUserSec = 25L;
        TeacherRequestDTO request = TeacherTestDataFactory.createValidRequest();
        UserSecRequestDTO userSecRequestDTO = this.createUserRequest(request);
        UserSecResponseDTO userSecResponseDTO = this.createUserResponse(request, idTeacher, idUserSec);
        Teacher teacher = TeacherTestDataFactory.createTeacherFromRequest(request, idTeacher);
        TeacherResponseDTO teacherResponseExpected = this.createTeacherResponse(request, idTeacher);

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
        TeacherRequestDTO request = TeacherTestDataFactory.createValidRequest();

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
        Long idTeacher = 78L;
        TeacherRequestDTO request = TeacherTestDataFactory.createValidRequest();
        Teacher teacher = TeacherTestDataFactory.createTeacherFromRequest(request, idTeacher);

        when(teacherService.saveTeacher(any(), any(), any())).thenReturn(teacher);
        doThrow(new ConflictException("El Username ya esta registrado")).when(userSecService).saveUserSec(any(), any(), any());

        assertThrows(ConflictException.class, () -> {
            TeacherResponseDTO teacherResponseResult = createTeacherAccount.createTeacherAndAccount(request);
        });
        verify(teacherService).saveTeacher(any(), any(), any());
        verify(teacherMapper,  never()).toTeacherResponse(any());
    }

    private UserSecRequestDTO createUserRequest(TeacherRequestDTO request) {
        return new UserSecRequestDTO(
                request.username(),
                request.password(),
                request.enabled(),
                request.accountNotExpired(),
                request.accountNotLocked(),
                request.credentialNotExpired(),
                new HashSet<>());
    }

    private UserSecResponseDTO createUserResponse(TeacherRequestDTO request, Long idTeacher, Long idUserSec) {
        return new UserSecResponseDTO(
                idUserSec, request.username(),
                request.password(),
                request.enabled(),
                request.accountNotExpired(),
                request.accountNotLocked(),
                request.credentialNotExpired(),
                new HashSet<>(),
                SubjectType.PROFESOR,
                idTeacher);
    }

    private TeacherResponseDTO createTeacherResponse(TeacherRequestDTO request, Long idTeacher) {
        return new TeacherResponseDTO(
                idTeacher, request.name(),
                request.mail(),
                request.specialty(),
                new ArrayList<>());
    }
}