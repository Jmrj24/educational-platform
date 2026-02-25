package com.example.demo.application.student;

import com.example.demo.exception.NotFoundException;
import com.example.demo.student.IStudentService;
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

public class DeleteStudentAccountTest {
    private final IStudentService studentService = Mockito.mock(IStudentService.class);
    private final UserSecRepository userSecRepository = Mockito.mock(UserSecRepository.class);

    private final DeleteStudentAccount deleteStudentAccount = new DeleteStudentAccount(studentService, userSecRepository);

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

        verify(studentService, never()).deleteByIdStudent(idStudent);
        verify(userSecRepository, never()).delete(any());
    }
}
