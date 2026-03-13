package com.example.demo.course;

import com.example.demo.course.authorization.CourseAuthorizationChecker;
import com.example.demo.course.dto.CourseRequestDTO;
import com.example.demo.course.dto.CourseResponseDTO;
import com.example.demo.course.dto.CourseUpdateDTO;
import com.example.demo.exception.NotFoundException;
import com.example.demo.security.SecurityService;
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

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICourseService courseService;
    @MockitoBean
    private CourseAuthorizationChecker courseAuthorizationChecker;
    @MockitoBean
    private SecurityService securityService;

    @Test
    @DisplayName("Debe retornar Http 201 y un curso, al crear el mismo")
    void saveCourse_createCourse_return201() throws Exception {
        CourseRequestDTO courseRequest = new CourseRequestDTO("Logica de programacion", "aspectos basicos");
        CourseResponseDTO courseResponse = CourseTestDataFactory.createCourseResponseDefault();

        when(courseService.saveCourse(courseRequest)).thenReturn(courseResponse);

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequest)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(courseResponse.id()))
                .andExpect(jsonPath("$.name").value(courseResponse.name()))
                .andExpect(jsonPath("$.description").value(courseResponse.description()))
                .andExpect(jsonPath("$.status").value(courseResponse.status()))
                .andExpect(jsonPath("$.teacherCourse").value(courseResponse.teacherCourse()))
                .andExpect(jsonPath("$.listStudents").isArray())
                .andExpect(jsonPath("$.listStudents").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar Http 400 al ir un dato invalido en la request")
    void saveCourse_requestNoValid_return400() throws Exception {
        CourseRequestDTO courseRequest = new CourseRequestDTO("", "aspectos basicos");

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequest)))

                .andExpect(status().isBadRequest());
        verify(courseService, never()).saveCourse(courseRequest);
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un array de cursos, si los mismos existen")
    void findAllCourses_coursesExist_return200() throws Exception {
        CourseResponseDTO courseResponse = CourseTestDataFactory.createCourseResponseDefault();

        when(courseService.findAllCourses()).thenReturn(List.of(courseResponse));

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(courseResponse.id()))
                .andExpect(jsonPath("$[0].name").value(courseResponse.name()));
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un array vacio, si no hay cursos registrados")
    void findAllCourses_CoursesNoExist_return200() throws Exception {
        when(courseService.findAllCourses()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un curso si el Id existe")
    void findByIdCourse_courseExist_return200() throws Exception {
        Long id = 8L;
        CourseResponseDTO courseResponse = CourseTestDataFactory.createCourseResponseDefault();

        when(courseService.findByIdCourse(id)).thenReturn(courseResponse);

        mockMvc.perform(get("/courses/{id}", id))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseResponse.id()))
                .andExpect(jsonPath("$.name").value(courseResponse.name()))
                .andExpect(jsonPath("$.description").value(courseResponse.description()))
                .andExpect(jsonPath("$.status").value(courseResponse.status()))
                .andExpect(jsonPath("$.teacherCourse").value(courseResponse.teacherCourse()))
                .andExpect(jsonPath("$.listStudents").isArray())
                .andExpect(jsonPath("$.listStudents").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id no existe")
    void findByIdCourse_courseNoExist_return404() throws Exception {
        Long id = 1L;

        when(courseService.findByIdCourse(id)).thenThrow(new NotFoundException("Curso no encontrado"));

        mockMvc.perform(get("/courses/{id}", id))

                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 404 si el Id del curso no existe")
    void updateCourse_courseNoExist_return404() throws Exception {
        Long id = 1L;
        CourseUpdateDTO courseUpdate = new CourseUpdateDTO("Nuevo name", "Nueva description");

        when(courseService.updateCourse(eq(id), any(CourseUpdateDTO.class))).thenThrow(new NotFoundException("Curso no encontrado"));

        mockMvc.perform(patch("/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseUpdate)))

                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe retornar un Http 200 y un courseResponse, si todo sale bien en la actualizacion")
    void updateCourse_updateValid_return200() throws Exception {
        Long id = 8L;
        CourseUpdateDTO courseUpdate = new CourseUpdateDTO("Logica de programacion", "aspectos basicos");
        CourseResponseDTO courseResponse = CourseTestDataFactory.createCourseResponseDefault();

        when(courseService.updateCourse(eq(id), any(CourseUpdateDTO.class))).thenReturn(courseResponse);

        mockMvc.perform(patch("/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseUpdate)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseResponse.id()))
                .andExpect(jsonPath("$.name").value(courseResponse.name()))
                .andExpect(jsonPath("$.description").value(courseResponse.description()))
                .andExpect(jsonPath("$.status").value(courseResponse.status()))
                .andExpect(jsonPath("$.teacherCourse").value(courseResponse.teacherCourse()))
                .andExpect(jsonPath("$.listStudents").isArray())
                .andExpect(jsonPath("$.listStudents").isEmpty());
    }

    @Test
    @DisplayName("Debe retornar un Http 204")
    void deleteStudent_deleteStudent_return204() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/courses/{id}", id))

                .andExpect(status().isNoContent());

        verify(courseService).deleteByIdCourse(id);
    }
}