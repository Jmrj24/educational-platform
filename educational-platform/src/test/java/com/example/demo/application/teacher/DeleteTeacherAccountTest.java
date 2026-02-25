package com.example.demo.application.teacher;

import com.example.demo.exception.NotFoundException;
import com.example.demo.teacher.ITeacherService;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DeleteTeacherAccountTest {
    private final ITeacherService teacherService = Mockito.mock(ITeacherService.class);
    private final UserSecRepository userSecRepository = Mockito.mock(UserSecRepository.class);

    private final DeleteTeacherAccount deleteTeacherAccount = new DeleteTeacherAccount(teacherService, userSecRepository);

    @Test
    @DisplayName("Elimina el profesor y su cuenta si todo sale bien")
    void deleteTeacherAndAccount_teacherAndAccountExist_deleteExit() {
        Long idTeacher = 54L;
        UserSec userSec = new UserSec();

        when(userSecRepository.findUserEntityByIdSubject(idTeacher, SubjectType.PROFESOR)).thenReturn(Optional.of(userSec));

        deleteTeacherAccount.deleteTeacherAndAccount(idTeacher);

        verify(teacherService).deleteByIdTeacher(idTeacher);
        verify(userSecRepository).delete(userSec);
    }

    @Test
    @DisplayName("Falla si el ID del profesor no existe")
    void deleteTeacherAndAccount_idTeacherNoExist_deleteFail() {
        Long idTeacher = 54L;

        doThrow(new NotFoundException("El ID ingresado no pertenece a una cuenta registrada.")).when(userSecRepository).findUserEntityByIdSubject(any(), any());

        assertThrows(NotFoundException.class, () -> {
            deleteTeacherAccount.deleteTeacherAndAccount(idTeacher);
        });

        verify(teacherService, never()).deleteByIdTeacher(idTeacher);
        verify(userSecRepository, never()).delete(any());
    }
}