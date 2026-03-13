package com.example.demo.teacher;

import com.example.demo.application.teacher.*;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.teacher.dto.TeacherRequestDTO;
import com.example.demo.teacher.dto.TeacherResponseDTO;
import com.example.demo.teacher.dto.TeacherUpdateDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeacherController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TeacherControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ITeacherService teacherService;
    @MockitoBean
    private AssignTeacherToCourseService assignTeacherToCourse;
    @MockitoBean
    private CreateTeacherAccount createTeacherAccount;
    @MockitoBean
    private DeleteTeacherAccount deleteTeacherAccount;
    @MockitoBean
    private UnassignTeacherToCourseService unassignTeacherToCourse;

    @Test
    @DisplayName("Debe retornar Http 201 y el profesor, al crear el mismo y su cuenta")
    void saveTeacher_createTeacher_return201() throws Exception {
        TeacherRequestDTO teacherRequestDTO = TeacherTestDataFactory.createValidRequest();
        teacherRequestDTO.rolesListIds().add(5L);
        TeacherResponseDTO teacherResponse = TeacherTestDataFactory.createTeacherResponseDefault();

        when(createTeacherAccount.createTeacherAndAccount(teacherRequestDTO)).thenReturn(teacherResponse);

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherRequestDTO)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(teacherResponse.id()))
                .andExpect(jsonPath("$.name").value(teacherResponse.name()))
                .andExpect(jsonPath("$.mail").value(teacherResponse.mail()))
                .andExpect(jsonPath("$.specialty").value(teacherResponse.specialty()))
                .andExpect(jsonPath("$.listCourses").isArray())
                .andExpect(jsonPath("$.listCourses").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar Http 400 al ir un dato invalido (Lista de roles vacia) en la request")
    void saveTeacher_requestNoValid_return400() throws Exception {
        TeacherRequestDTO teacherRequestDTO = TeacherTestDataFactory.createValidRequest();

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherRequestDTO)))

                .andExpect(status().isBadRequest());
        verify(createTeacherAccount, never()).createTeacherAndAccount(teacherRequestDTO);
    }

    @Test
    @DisplayName("Debe retornar Http 409 cuando el mail ya esta registrado")
    void saveTeacher_mailDuplicated_return409() throws Exception {
        TeacherRequestDTO teacherRequestDTO = TeacherTestDataFactory.createValidRequest();
        teacherRequestDTO.rolesListIds().add(5L);

        when(createTeacherAccount.createTeacherAndAccount(teacherRequestDTO)).thenThrow(new ConflictException("El email ya esta registrado"));

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherRequestDTO)))

                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Debe retornar Http 409 cuando el userSec ya esta registrado")
    void saveTeacher_userSecDuplicated_return409() throws Exception {
        TeacherRequestDTO teacherRequestDTO = TeacherTestDataFactory.createValidRequest();
        teacherRequestDTO.rolesListIds().add(5L);

        when(createTeacherAccount.createTeacherAndAccount(teacherRequestDTO)).thenThrow(new ConflictException("El Username ya esta registrado"));

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherRequestDTO)))

                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un array de profesores, si los mismos existen")
    void findAllTeachers_teachersExist_return200() throws Exception {
        TeacherResponseDTO teacherResponse = TeacherTestDataFactory.createTeacherResponseDefault();

        when(teacherService.findAllTeachers()).thenReturn(List.of(teacherResponse));

        mockMvc.perform(get("/teachers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(teacherResponse.id()))
                .andExpect(jsonPath("$[0].name").value(teacherResponse.name()));
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un array vacio, si no hay profesores registrados")
    void findAllTeachers_teachersNoExist_return200() throws Exception {
        when(teacherService.findAllTeachers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/teachers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un profesor si el Id existe")
    void findByIdTeacher_teacherExist_return200() throws Exception {
        Long id = 37L;
        TeacherResponseDTO teacherResponse = TeacherTestDataFactory.createTeacherResponseDefault();

        when(teacherService.findByIdTeacher(id)).thenReturn(teacherResponse);

        mockMvc.perform(get("/teachers/{id}", id))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teacherResponse.id()))
                .andExpect(jsonPath("$.name").value(teacherResponse.name()))
                .andExpect(jsonPath("$.mail").value(teacherResponse.mail()))
                .andExpect(jsonPath("$.specialty").value(teacherResponse.specialty()))
                .andExpect(jsonPath("$.listCourses").isArray())
                .andExpect(jsonPath("$.listCourses").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id no existe")
    void findByIdTeacher_teacherNoExist_return404() throws Exception {
        Long id = 1L;

        when(teacherService.findByIdTeacher(id)).thenThrow(new NotFoundException("Profesor no encontrado"));

        mockMvc.perform(get("/teachers/{id}", id))

                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id del profesor no existe")
    void updateTeacher_teacherNoExist_return404() throws Exception {
        Long id = 1L;
        TeacherUpdateDTO teacherUpdate = new TeacherUpdateDTO("Nuevo name", "Nuevo mail", "Nueva especialidad");

        when(teacherService.updateTeacher(eq(id), any(TeacherUpdateDTO.class))).thenThrow(new NotFoundException("Profesor no encontrado"));

        mockMvc.perform(patch("/teachers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherUpdate)))

                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 409 si el nuevo mail del profesor ya esta registrado")
    void updateTeacher_mailDuplicated_return409() throws Exception {
        Long id = 37L;
        TeacherUpdateDTO teacherUpdate = new TeacherUpdateDTO("Nuevo name", "Nuevo mail", "Nueva especialidad");

        when(teacherService.updateTeacher(eq(id), any(TeacherUpdateDTO.class))).thenThrow(new ConflictException("El email ya esta registrado"));

        mockMvc.perform(patch("/teachers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherUpdate)))

                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un teacherResponse, si todo sale bien en la actualizacion")
    void updateTeacher_updateValid_return200() throws Exception {
        Long id = 37L;
        TeacherUpdateDTO teacherUpdate = new TeacherUpdateDTO("Mateo", "mateo@mail.com", "Ciencias");
        TeacherResponseDTO teacherResponse = TeacherTestDataFactory.createTeacherResponseDefault();

        when(teacherService.updateTeacher(eq(id), any(TeacherUpdateDTO.class))).thenReturn(teacherResponse);

        mockMvc.perform(patch("/teachers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherUpdate)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teacherResponse.id()))
                .andExpect(jsonPath("$.name").value(teacherResponse.name()))
                .andExpect(jsonPath("$.mail").value(teacherResponse.mail()))
                .andExpect(jsonPath("$.specialty").value(teacherResponse.specialty()))
                .andExpect(jsonPath("$.listCourses").isArray())
                .andExpect(jsonPath("$.listCourses").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el userSec del profesor no existe")
    void deleteTeacher_userSecNoExist_return404() throws Exception {
        Long id = 1L;

        doThrow((new NotFoundException("El ID ingresado no pertenece a una cuenta registrada."))).when(deleteTeacherAccount).deleteTeacherAndAccount(id);

        mockMvc.perform(delete("/teachers/{id}", id))

                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 204")
    void deleteTeacher_deleteTeacher_return204() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/teachers/{id}", id))

                .andExpect(status().isNoContent());

        verify(deleteTeacherAccount).deleteTeacherAndAccount(id);
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id de curso no existe")
    void teacherAssign_courseNoExist_return404() throws Exception {
        Long idCourse = 25L;
        Long idTeacher = 15L;

        doThrow(new NotFoundException("Curso no encontrado")).when(assignTeacherToCourse).assign(idCourse, idTeacher);

        mockMvc.perform(post("/teachers/assign/teacher/{idTeacher}/course/{idCourse}", idTeacher, idCourse))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 204 si asigna el profesor en el curso")
    void teacherAssign_teacherAndCourseExist_return204() throws Exception {
        Long idCourse = 25L;
        Long idTeacher = 15L;

        mockMvc.perform(post("/teachers/assign/teacher/{idTeacher}/course/{idCourse}", idTeacher, idCourse))
                .andExpect(status().isNoContent());

        verify(assignTeacherToCourse).assign(idCourse, idTeacher);
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id del profesor no existe")
    void teacherUnassign_teacherNoExist_return404() throws Exception {
        Long idCourse = 25L;
        Long idTeacher = 15L;

        doThrow(new NotFoundException("Profesor no encontrado")).when(unassignTeacherToCourse).unassign(idCourse, idTeacher);

        mockMvc.perform(delete("/teachers/unassign/teacher/{idTeacher}/course/{idCourse}", idTeacher, idCourse))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 204 si elimina la asignacion del profesor en el curso")
    void teacherUnassign_teacherAndCourseExist_return204() throws Exception {
        Long idCourse = 25L;
        Long idTeacher = 15L;

        mockMvc.perform(delete("/teachers/unassign/teacher/{idTeacher}/course/{idCourse}", idTeacher, idCourse))
                .andExpect(status().isNoContent());

        verify(unassignTeacherToCourse).unassign(idCourse, idTeacher);
    }
}