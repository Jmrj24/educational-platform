package com.example.demo.student;

import com.example.demo.application.student.CreateStudentAccount;
import com.example.demo.application.student.DeleteStudentAccount;
import com.example.demo.application.student.StudentDisenrollmentService;
import com.example.demo.application.student.StudentEnrollmentService;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.NotFoundException;
import com.example.demo.student.dto.StudentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;
    @Mock
    private CreateStudentAccount createStudentAccount;
    @Mock
    private StudentEnrollmentService studentEnrollment;
    @Mock
    private DeleteStudentAccount deleteStudentAccount;
    @Mock
    private StudentDisenrollmentService studentDisenrollment;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Debe retornar Http 201 al crear al estudiante")
    void saveStudent_createStudent_return201() throws Exception {
        String jsonBody = """
                {
                    "username": "juanDiaz",
                    "password": "1234",
                    "enabled": true,
                    "accountNotExpired": true,
                    "accountNotLocked": true,
                    "credentialNotExpired": true,
                    "rolesListIds": [3],
                    "name": "Juan Diaz",
                    "mail": "juan.diaz@example.com"
                }
                """;

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))

                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un estudiante si el Id existe")
    void findByIdStudent_studentExist_returnOk() throws Exception {
        Long id = 1L;
        StudentResponseDTO studentFalseDTO = new StudentResponseDTO(1L, "Juan", "juan@mail.com", new ArrayList<>());

        when(studentService.findByIdStudent(id)).thenReturn(studentFalseDTO);

        mockMvc.perform(get("/students/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan"))
                .andExpect(jsonPath("$.mail").value("juan@mail.com"));
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id no existe")
    void findByIdStudent_studentNoExist_returnNotFound() throws Exception {
        Long id = 1L;

        when(studentService.findByIdStudent(id)).thenThrow(new NotFoundException("Estudiante no encontrado"));

        mockMvc.perform(get("/students/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id de curso no existe")
    void studentInscription_courseNoExist_returnNotFound() throws Exception {
        Long idCourse = 25L;
        Long idStudent = 15L;

        doThrow(new NotFoundException("Curso no encontrado")).when(studentEnrollment).studentInscription(idCourse, idStudent);

        mockMvc.perform(post("/students/inscription/{idCourse}/{idStudent}", idCourse, idStudent))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 204 si el Id de curso y el estudiante existen")
    void studentInscription_studentAndCourseExist_returnNoContent() throws Exception {
        Long idCourse = 25L;
        Long idStudent = 15L;

        mockMvc.perform(post("/students/inscription/{idCourse}/{idStudent}", idCourse, idStudent))
                .andExpect(status().isNoContent());

        verify(studentEnrollment).studentInscription(idCourse, idStudent);
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un array de estudiantes")
    void findAllStudents_studentsExist_returnOK() throws Exception {
        StudentResponseDTO studentFalseDTO = new StudentResponseDTO(15L, "Jose", "jose@mail.com", new ArrayList<>());

        when(studentService.findAllStudents()).thenReturn(List.of(studentFalseDTO));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Jose"));

    }
}