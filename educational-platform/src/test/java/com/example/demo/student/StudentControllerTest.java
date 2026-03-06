package com.example.demo.student;

import com.example.demo.application.student.CreateStudentAccount;
import com.example.demo.application.student.DeleteStudentAccount;
import com.example.demo.application.student.StudentDisenrollmentService;
import com.example.demo.application.student.StudentEnrollmentService;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.NotFoundException;
import com.example.demo.student.dto.StudentRequestDTO;
import com.example.demo.student.dto.StudentResponseDTO;
import com.example.demo.student.dto.StudentUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudentControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    private final StudentService studentService = Mockito.mock(StudentService.class);
    private final StudentEnrollmentService studentEnrollment = Mockito.mock(StudentEnrollmentService.class);
    private final CreateStudentAccount createStudentAccount = Mockito.mock(CreateStudentAccount.class);
    private final DeleteStudentAccount deleteStudentAccount = Mockito.mock(DeleteStudentAccount.class);
    private final StudentDisenrollmentService studentDisenrollment = Mockito.mock(StudentDisenrollmentService.class);

    private final StudentController studentController = new StudentController(studentService, studentEnrollment,
            createStudentAccount, deleteStudentAccount, studentDisenrollment);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Debe retornar Http 201 y un estudiante, al crear el mismo y su cuenta")
    void saveStudent_createStudent_return201() throws Exception {
        StudentRequestDTO studentRequestDTO = StudentTestDataFactory.createValidRequest();
        studentRequestDTO.rolesListIds().add(5L);
        StudentResponseDTO studentResponse = StudentTestDataFactory.createStudentResponseDefault();

        when(createStudentAccount.createStudentAndAccount(studentRequestDTO)).thenReturn(studentResponse);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequestDTO)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(studentResponse.id()))
                .andExpect(jsonPath("$.name").value(studentResponse.name()))
                .andExpect(jsonPath("$.mail").value(studentResponse.mail()))
                .andExpect(jsonPath("$.listCourses").isArray())
                .andExpect(jsonPath("$.listCourses").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar Http 400 al ir un dato invalido (Lista de roles vacia) en la request")
    void saveStudent_requestNoValid_return400() throws Exception {
        StudentRequestDTO studentRequestDTO = StudentTestDataFactory.createValidRequest();

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequestDTO)))

                .andExpect(status().isBadRequest());
        verify(createStudentAccount, never()).createStudentAndAccount(studentRequestDTO);
    }

    @Test
    @DisplayName("Debe retornar Http 409 cuando el mail ya esta registrado")
    void saveStudent_mailDuplicated_return409() throws Exception {
        StudentRequestDTO studentRequestDTO = StudentTestDataFactory.createValidRequest();
        studentRequestDTO.rolesListIds().add(3L);

        when(createStudentAccount.createStudentAndAccount(studentRequestDTO)).thenThrow(new ConflictException("El email ya esta registrado"));

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequestDTO)))

                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Debe retornar Http 409 cuando el userSec ya esta registrado")
    void saveStudent_userSecDuplicated_return409() throws Exception {
        StudentRequestDTO studentRequestDTO = StudentTestDataFactory.createValidRequest();
        studentRequestDTO.rolesListIds().add(3L);

        when(createStudentAccount.createStudentAndAccount(studentRequestDTO)).thenThrow(new ConflictException("El Username ya esta registrado"));

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequestDTO)))

                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un array de estudiantes, si los mismos existen")
    void findAllStudents_studentsExist_return200() throws Exception {
        StudentResponseDTO student = StudentTestDataFactory.createStudentResponseDefault();

        when(studentService.findAllStudents()).thenReturn(List.of(student));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(student.id()))
                .andExpect(jsonPath("$[0].name").value(student.name()));
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un array vacio, si no hay estudiantes registrados")
    void findAllStudents_studentsNoExist_return200() throws Exception {
        when(studentService.findAllStudents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un estudiante si el Id existe")
    void findByIdStudent_studentExist_return200() throws Exception {
        Long id = 12L;
        StudentResponseDTO studentResponse = StudentTestDataFactory.createStudentResponseDefault();

        when(studentService.findByIdStudent(id)).thenReturn(studentResponse);

        mockMvc.perform(get("/students/{id}", id))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentResponse.id()))
                .andExpect(jsonPath("$.name").value(studentResponse.name()))
                .andExpect(jsonPath("$.mail").value(studentResponse.mail()))
                .andExpect(jsonPath("$.listCourses").isArray())
                .andExpect(jsonPath("$.listCourses").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id no existe")
    void findByIdStudent_studentNoExist_return404() throws Exception {
        Long id = 1L;

        when(studentService.findByIdStudent(id)).thenThrow(new NotFoundException("Estudiante no encontrado"));

        mockMvc.perform(get("/students/{id}", id))

                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id del estudiante no existe")
    void updateStudent_studentNoExist_return404() throws Exception {
        Long id = 1L;
        StudentUpdateDTO studentUpdate = new StudentUpdateDTO("Nuevo name", "Nuevo mail");

        when(studentService.updateStudent(eq(id), any(StudentUpdateDTO.class))).thenThrow(new NotFoundException("Estudiante no encontrado"));

        mockMvc.perform(patch("/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentUpdate)))

                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 409 si el nuevo mail del estudiante ya esta registrado")
    void updateStudent_mailDuplicated_return409() throws Exception {
        Long id = 12L;
        StudentUpdateDTO studentUpdate = new StudentUpdateDTO("Nuevo name", "Nuevo mail");

        when(studentService.updateStudent(eq(id), any(StudentUpdateDTO.class))).thenThrow(new ConflictException("El email ya esta registrado"));

        mockMvc.perform(patch("/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentUpdate)))

                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un studentResponse, si todo sale bien en la actualizacion")
    void updateStudent_updateValid_return200() throws Exception {
        Long id = 12L;
        StudentUpdateDTO studentUpdate = new StudentUpdateDTO("Maria", "maria@mail.com");
        StudentResponseDTO studentResponse = StudentTestDataFactory.createStudentResponseDefault();

        when(studentService.updateStudent(eq(id), any(StudentUpdateDTO.class))).thenReturn(studentResponse);

        mockMvc.perform(patch("/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentUpdate)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentResponse.id()))
                .andExpect(jsonPath("$.name").value(studentResponse.name()))
                .andExpect(jsonPath("$.mail").value(studentResponse.mail()))
                .andExpect(jsonPath("$.listCourses").isArray())
                .andExpect(jsonPath("$.listCourses").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el userSec del estudiante no existe")
    void deleteStudent_userSecNoExist_return404() throws Exception {
        Long id = 1L;

        doThrow((new NotFoundException("El ID ingresado no pertenece a una cuenta registrada."))).when(deleteStudentAccount).deleteStudentAndAccount(id);

        mockMvc.perform(delete("/students/{id}", id))

                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 204")
    void deleteStudent_deleteStudent_return204() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/students/{id}", id))

                .andExpect(status().isNoContent());

        verify(deleteStudentAccount).deleteStudentAndAccount(id);
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id de curso no existe")
    void studentInscription_courseNoExist_return404() throws Exception {
        Long idCourse = 25L;
        Long idStudent = 15L;

        doThrow(new NotFoundException("Curso no encontrado")).when(studentEnrollment).studentInscription(idCourse, idStudent);

        mockMvc.perform(post("/students/inscription/{idCourse}/{idStudent}", idCourse, idStudent))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 204 si inscribe al estudiante en el curso")
    void studentInscription_studentAndCourseExist_return204() throws Exception {
        Long idCourse = 25L;
        Long idStudent = 15L;

        mockMvc.perform(post("/students/inscription/{idCourse}/{idStudent}", idCourse, idStudent))
                .andExpect(status().isNoContent());

        verify(studentEnrollment).studentInscription(idCourse, idStudent);
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id del estudiante no existe")
    void studentUnsubscribe_studentNoExist_return404() throws Exception {
        Long idCourse = 25L;
        Long idStudent = 15L;

        doThrow(new NotFoundException("Estudiante no encontrado")).when(studentDisenrollment).studentUnsubscribe(idCourse, idStudent);

        mockMvc.perform(delete("/students/unsubscribe/{idCourse}/{idStudent}", idCourse, idStudent))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 204 si desuscribe al estudiante del curso")
    void studentUnsubscribe_studentAndCourseExist_return204() throws Exception {
        Long idCourse = 25L;
        Long idStudent = 15L;

        mockMvc.perform(delete("/students/unsubscribe/{idCourse}/{idStudent}", idCourse, idStudent))
                .andExpect(status().isNoContent());

        verify(studentDisenrollment).studentUnsubscribe(idCourse, idStudent);
    }
}