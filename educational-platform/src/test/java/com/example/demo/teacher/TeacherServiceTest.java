package com.example.demo.teacher;

import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.teacher.dto.TeacherResponseDTO;
import com.example.demo.teacher.dto.TeacherUpdateDTO;
import com.example.demo.teacher.mapper.TeacherMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private TeacherMapper teacherMapper;
    @InjectMocks
    private TeacherService teacherService;
    @Captor
    private ArgumentCaptor<Teacher> teacherCaptor;

    @Test
    @DisplayName("Debe lanzar error si el mail ya existe (Validación Repetido)")
    void saveTeacher_mailExist_runException() {
        String name = "Jose";
        String mail = "prueba@mail.com";
        String speciality = "Matematicas";
        Teacher teacher = new Teacher();

        when(teacherRepository.findTeacherEntityByMail(mail)).thenReturn(Optional.of(teacher));

        assertThrows(ConflictException.class, () -> {
            teacherService.saveTeacher(name, mail, speciality);
        });

        verify(teacherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe guardar el profesor, si el mail no pertenece a otro profesor guardado")
    void saveTeacher_mailNoExist_saveSuccess() {
        String name = "Jose";
        String mail = "prueba@mail.com";
        String speciality = "Matematicas";

        when(teacherRepository.findTeacherEntityByMail(mail)).thenReturn(Optional.empty());

        teacherService.saveTeacher(name, mail, speciality);

        verify(teacherRepository).save(teacherCaptor.capture());

        Teacher teacherSave = teacherCaptor.getValue();

        assertEquals(name, teacherSave.getName());
        assertEquals(mail, teacherSave.getMail());
        assertEquals(speciality, teacherSave.getSpecialty());
    }

    @Test
    @DisplayName("Debe devolver un lista de profesores DTO si existen")
    void findAllTeachers_teachersExist_returnTeachers() {
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        List<Teacher> listTeachers = new ArrayList<>(List.of(teacher));
        TeacherResponseDTO teacherResponseDTO = TeacherTestDataFactory.createTeacherResponseDTO(teacher, Collections.emptyList());
        List<TeacherResponseDTO> listTeachersResponseExpect = new ArrayList<>(List.of(teacherResponseDTO));

        when(teacherRepository.findAll()).thenReturn(listTeachers);
        when(teacherMapper.toTeacherResponse(teacher)).thenReturn(teacherResponseDTO);

        List<TeacherResponseDTO> listTeachersResponseResult = teacherService.findAllTeachers();

        assertEquals(listTeachersResponseExpect, listTeachersResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un lista vacia si no hay registro de profesores")
    void findAllTeachers_teachersNoExist_returnListEmpty() {
        when(teacherRepository.findAll()).thenReturn(Collections.emptyList());

        List<TeacherResponseDTO> listTeachersResponseResult = teacherService.findAllTeachers();

        assertNotNull(listTeachersResponseResult);
        assertTrue(listTeachersResponseResult.isEmpty());
    }

    @Test
    @DisplayName("Debe devolver un profesor si el Id existe")
    void findByIdTeacher_teacherExist_returnTeacherDTO() {
        Long id = 45L;
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        TeacherResponseDTO teacherExpected = TeacherTestDataFactory.createTeacherResponseDTO(teacher, Collections.emptyList());

        when(teacherRepository.findById(id)).thenReturn(Optional.of(teacher));
        when(teacherMapper.toTeacherResponse(teacher)).thenReturn(teacherExpected);

        TeacherResponseDTO teacherResult = teacherService.findByIdTeacher(id);

        assertEquals(teacherExpected, teacherResult);
        verify(teacherRepository).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar una excepcion al no existir el id del profesor")
    void findByIdTeacher_teacherNoExist_runException() {
        Long id = 1L;

        when(teacherRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            teacherService.findByIdTeacher(id);
        });
    }

    @Test
    @DisplayName("Debe actualizar el profesor si todo sale bien")
    void updateTeacher_teacherExistAndMailValid_teacherUpdateSuccessful() {
        Long idTeacher = 45L;
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        TeacherUpdateDTO teacherUpdate = new TeacherUpdateDTO("Nombre nuevo", "Nuevo mail", "Especialidad Nueva");

        when(teacherRepository.findById(idTeacher)).thenReturn(Optional.of(teacher));
        when(teacherRepository.findTeacherEntityByMail(teacherUpdate.getMail())).thenReturn(Optional.empty());
        when(teacherRepository.save(teacher)).thenReturn(teacher);
        TeacherResponseDTO teacherResponseExpected = TeacherTestDataFactory.createTeacherResponseDTO(teacher, Collections.emptyList());
        when(teacherMapper.toTeacherResponse(teacher)).thenReturn(teacherResponseExpected);

        TeacherResponseDTO teacherResponseResult = teacherService.updateTeacher(idTeacher, teacherUpdate);

        assertEquals(teacher.getName(), teacherUpdate.getName());
        assertEquals(teacher.getMail(), teacherUpdate.getMail());
        verify(teacherRepository).save(teacher);
        assertEquals(teacherResponseExpected, teacherResponseResult);
    }

    @Test
    @DisplayName("Debe lanzar una exception si el mail nuevo, ya existe")
    void updateTeacher_teacherExistAndMailNoValid_runException() {
        Long idTeacher = 45L;
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        TeacherUpdateDTO teacherUpdate = new TeacherUpdateDTO("Nombre nuevo", "Nuevo mail", "Especialidad Nueva");

        when(teacherRepository.findById(idTeacher)).thenReturn(Optional.of(teacher));
        when(teacherRepository.findTeacherEntityByMail(teacherUpdate.getMail())).thenReturn(Optional.of(new Teacher()));

        assertThrows(ConflictException.class, () -> {
            teacherService.updateTeacher(idTeacher, teacherUpdate);
        });

        verify(teacherRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar una exception si no existe el ID")
    void updateTeacher_teacherNoExist_runException() {
        Long idTeacher = 45L;
        TeacherUpdateDTO teacherUpdate = new TeacherUpdateDTO("Nombre nuevo", "Nuevo mail", "Especialidad Nueva");

        when(teacherRepository.findById(idTeacher)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            teacherService.updateTeacher(idTeacher, teacherUpdate);
        });

        verify(teacherRepository, never()).save(any());
    }
}