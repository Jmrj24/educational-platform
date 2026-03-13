package com.example.demo.student;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.student.dto.StudentResponseDTO;
import com.example.demo.student.dto.StudentUpdateDTO;
import com.example.demo.student.mapper.StudentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentMapper studentMapper;
    @InjectMocks
    private StudentService studentService;
    @Captor
    private ArgumentCaptor<Student> studentCaptor;

    @Test
    @DisplayName("Debe lanzar error si el mail ya existe (Validación Repetido)")
    void saveStudent_mailExist_runException() {
        String name = "Jose";
        String mail = "prueba@mail.com";
        Student student = new Student();

        when(studentRepository.findStudentEntityByMail(mail)).thenReturn(Optional.of(student));

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

        when(studentRepository.findStudentEntityByMail(mail)).thenReturn(Optional.empty());

        studentService.saveStudent(name, mail);

        verify(studentRepository).save(studentCaptor.capture());

        Student studentSave = studentCaptor.getValue();

        assertEquals(name, studentSave.getName());
        assertEquals(mail, studentSave.getMail());
    }

    @Test
    @DisplayName("Debe devolver un lista de estudiantes DTO si existen")
    void findAllStudents_studentsExist_returnStudents() {
        Student student = StudentTestDataFactory.createStudent();
        List<Student> listStudents = new ArrayList<>(List.of(student));
        StudentResponseDTO studentResponseDTO = StudentTestDataFactory.createStudentResponseDTO(student, Collections.emptyList());
        List<StudentResponseDTO> listStudentsResponseExpect = new ArrayList<>(List.of(studentResponseDTO));

        when(studentRepository.findAll()).thenReturn(listStudents);
        when(studentMapper.toStudentResponse(student)).thenReturn(studentResponseDTO);

        List<StudentResponseDTO> listStudentsResponseResult = studentService.findAllStudents();

        assertEquals(listStudentsResponseExpect, listStudentsResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un lista vacia si no hay registro de estudiantes")
    void findAllStudents_studentsNoExist_returnListEmpty() {
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());

        List<StudentResponseDTO> listStudentsResponseResult = studentService.findAllStudents();

        assertNotNull(listStudentsResponseResult);
        assertTrue(listStudentsResponseResult.isEmpty());
    }

    @Test
    @DisplayName("Debe devolver un estudiante si el Id existe")
    void findByIdStudent_studentExist_returnStudentDTO() {
        Long id = 89L;
        Student student = StudentTestDataFactory.createStudent();
        StudentResponseDTO studentExpected = StudentTestDataFactory.createStudentResponseDTO(student, Collections.emptyList());

        when(studentRepository.findById(id)).thenReturn(Optional.of(student));
        when(studentMapper.toStudentResponse(student)).thenReturn(studentExpected);

        StudentResponseDTO studentResult = studentService.findByIdStudent(id);

        assertEquals(studentExpected, studentResult);
        verify(studentRepository).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar una excepcion al no existir el id del estudiante")
    void findByIdStudent_studentNoExist_runException() {
        Long id = 1L;

        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            studentService.findByIdStudent(id);
        });
    }

    @Test
    @DisplayName("Debe actualizar el estudiante si todo sale bien")
    void updateStudent_studentExistAndMailValid_studentUpdateSuccessful() {
        Long idStudent = 89L;
        Student student = StudentTestDataFactory.createStudent();
        StudentUpdateDTO studentUpdate = new StudentUpdateDTO("Nombre nuevo", "Nuevo mail");

        when(studentRepository.findById(idStudent)).thenReturn(Optional.of(student));
        when(studentRepository.findStudentEntityByMail(studentUpdate.getMail())).thenReturn(Optional.empty());
        when(studentRepository.save(student)).thenReturn(student);
        StudentResponseDTO studentResponseExpected = StudentTestDataFactory.createStudentResponseDTO(student, Collections.emptyList());
        when(studentMapper.toStudentResponse(student)).thenReturn(studentResponseExpected);

        StudentResponseDTO studentResponseResult = studentService.updateStudent(idStudent, studentUpdate);

        assertEquals(student.getName(), studentUpdate.getName());
        assertEquals(student.getMail(), studentUpdate.getMail());
        verify(studentRepository).save(student);
        verify(studentMapper).toStudentResponse(student);
        assertEquals(studentResponseExpected, studentResponseResult);
    }

    @Test
    @DisplayName("Debe lanzar una exception si el mail nuevo, ya existe")
    void updateStudent_studentExistAndMailNoValid_runException() {
        Long idStudent = 89L;
        Student student = new Student();
        StudentUpdateDTO studentUpdate = new StudentUpdateDTO("Nombre nuevo", "Nuevo mail");

        when(studentRepository.findById(idStudent)).thenReturn(Optional.of(student));
        when(studentRepository.findStudentEntityByMail(studentUpdate.getMail())).thenReturn(Optional.of(new Student()));

        assertThrows(ConflictException.class, () -> {
            studentService.updateStudent(idStudent, studentUpdate);
        });

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar una exception si no existe el ID")
    void updateStudent_studentNoExist_runException() {
        Long idStudent = 89L;
        StudentUpdateDTO studentUpdate = new StudentUpdateDTO("Nombre nuevo", "Nuevo mail");

        when(studentRepository.findById(idStudent)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            studentService.updateStudent(idStudent, studentUpdate);
        });

        verify(studentRepository, never()).save(any());
    }
}