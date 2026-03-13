package com.example.demo.application.student;

import com.example.demo.exception.NotFoundException;
import com.example.demo.student.IStudentService;
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
public class DeleteStudentAccountTest {
    @Mock
    private IStudentService studentService;
    @Mock
    private UserSecRepository userSecRepository;
    @InjectMocks
    private DeleteStudentAccount deleteStudentAccount;

    @Test
    @DisplayName("Elimina el estudiante y su cuenta si todo sale bien")
    void deleteStudentAndAccount_studentAndAccountExist_deleteExit() {
        Long idStudent = 45L;
        UserSec userSec = new UserSec();

        when(userSecRepository.findUserEntityByIdSubject(idStudent, SubjectType.ESTUDIANTE)).thenReturn(Optional.of(userSec));

        deleteStudentAccount.deleteStudentAndAccount(idStudent);

        verify(studentService).deleteByIdStudent(idStudent);
        verify(userSecRepository).delete(userSec);
    }

    @Test
    @DisplayName("Falla si el ID del estudiante no existe")
    void deleteStudentAndAccount_idStudentNoExist_deleteFail() {
        Long idStudent = 45L;

        doThrow(new NotFoundException("El ID ingresado no pertenece a una cuenta registrada.")).when(userSecRepository).findUserEntityByIdSubject(any(), any());

        assertThrows(NotFoundException.class, () -> {
            deleteStudentAccount.deleteStudentAndAccount(idStudent);
        });

        verify(studentService, never()).deleteByIdStudent(any());
        verify(userSecRepository, never()).delete(any());
    }
}