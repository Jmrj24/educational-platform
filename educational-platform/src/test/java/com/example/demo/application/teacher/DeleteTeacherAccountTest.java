package com.example.demo.application.teacher;

import com.example.demo.exception.NotFoundException;
import com.example.demo.teacher.ITeacherService;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteTeacherAccountTest {
    @Mock
    private ITeacherService teacherService;
    @Mock
    private UserSecRepository userSecRepository;
    @InjectMocks
    private DeleteTeacherAccount deleteTeacherAccount;

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

        verify(teacherService, never()).deleteByIdTeacher(any());
        verify(userSecRepository, never()).delete(any());
    }
}