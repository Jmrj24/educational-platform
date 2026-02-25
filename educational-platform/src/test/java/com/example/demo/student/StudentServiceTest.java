package com.example.demo.student;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.student.dto.StudentResponseDTO;
import com.example.demo.student.mapper.StudentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceTest {
    private final StudentRepository studentRepository = Mockito.mock(StudentRepository.class);
    private final StudentMapper studentMapper = Mockito.mock(StudentMapper.class);

    private final StudentService studentService = new StudentService(studentRepository, studentMapper);

    private final ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);

    @Test
    @DisplayName("Debe lanzar error si el mail ya existe (Validación Repetido)")
    void saveStudent_mailExist_runException() {
        String name = "Jose";
        String mail = "prueba@mail.com";
        Student student = new Student();

        when(this.studentRepository.findStudentEntityByMail(mail)).thenReturn(Optional.of(student));

        assertThrows(ConflictException.class, () -> {
            studentService.saveStudent(name, mail);
        });

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe guardar el estudiante, si el mail no pertenece a otro estudiante guardado")
    void saveStudent_mailNoExist_saveSuccess() {
        String name = "Jose";
        String mail = "prueba@mail.com";

        when(this.studentRepository.findStudentEntityByMail(mail)).thenReturn(Optional.empty());

        studentService.saveStudent(name, mail);

        verify(studentRepository).save(studentCaptor.capture());

        Student studentSave = studentCaptor.getValue();

        assertEquals(name, studentSave.getName());
        assertEquals(mail, studentSave.getMail());
    }

    @Test
    @DisplayName("Debe devolver un estudiante si el Id existe")
    void findByIdStudent_studentExist_returnStudentDTO() {
        Long id = 1L;
        Student student = new Student(1L, "Jose", "jose@mail.com", new ArrayList<>());
        StudentResponseDTO studentExpected = new StudentResponseDTO(1L, "Jose", "jose@mail.com", new ArrayList<>());

        when(this.studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(this.studentMapper.toStudentResponse(student)).thenReturn(studentExpected);

        StudentResponseDTO studentResult = studentService.findByIdStudent(id);

        assertEquals(studentExpected, studentResult);
        verify(studentRepository).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar una excepcion al no existir el id del estudiante")
    void findByIdStudent_studentNoExist_runException() {
        Long id = 1L;

        when(this.studentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            studentService.findByIdStudent(id);
        });
    }
}