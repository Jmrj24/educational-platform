package com.example.demo.integration.teacher;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.course.CourseTestDataFactory;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.TeacherRepository;
import com.example.demo.teacher.TeacherTestDataFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMINISTRADOR")
@ActiveProfiles("test")
@Transactional
public class AssignTeacherToCourseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Test
    @DisplayName("Asigna un profesor en un curso si todo sale bien")
    void assign_courseAndTeacherExits_teacherAssignSuccessful() throws Exception {
        Teacher teacher = teacherRepository.save(TeacherTestDataFactory.createTeacherFromIntegration());
        Course course = courseRepository.save(CourseTestDataFactory.createCourseFromIntegration());
        Long idTeacher = teacher.getId();
        Long idCourse = course.getId();

        mockMvc.perform(post("/teachers/assign/teacher/{idTeacher}/course/{idCourse}", idTeacher, idCourse))
                .andExpect(status().isNoContent());

        Teacher teacherSaved = teacherRepository.findById(idTeacher).orElseThrow();
        Course courseSaved = courseRepository.findById(idCourse).orElseThrow();

        assertTrue(
                teacherSaved.getListCourses().stream()
                    .map(Course::getId)
                    .anyMatch(c -> c.equals(idCourse))
        );
        assertEquals(courseSaved.getTeacherCourse().getId(), idTeacher);
        assertTrue(courseSaved.isStatus());
    }

    @Test
    @DisplayName("Lanza una excepcion si el profesor no existe")
    void assign_teacherNoExits_return404() throws Exception {
        Course course = courseRepository.save(CourseTestDataFactory.createCourseFromIntegration());
        Long idCourse = course.getId();
        Long idTeacher = 12L;

        mockMvc.perform(post("/teachers/assign/teacher/{idTeacher}/course/{idCourse}", idTeacher, idCourse))
                .andExpect(status().isNotFound());

        Course courseSaved = courseRepository.findById(idCourse).orElseThrow();

        assertNull(courseSaved.getTeacherCourse());
        assertFalse(courseSaved.isStatus());
    }

    @Test
    @DisplayName("Lanza una excepcion si el curso no existe")
    void assign_courseNoExits_return404() throws Exception {
        Teacher teacher = teacherRepository.save(TeacherTestDataFactory.createTeacherFromIntegration());
        Long idTeacher = teacher.getId();
        Long idCourse = 32L;

        mockMvc.perform(post("/teachers/assign/teacher/{idTeacher}/course/{idCourse}", idTeacher, idCourse))
                .andExpect(status().isNotFound());

        Teacher teacherSaved = teacherRepository.findById(idTeacher).orElseThrow();

        assertTrue(teacherSaved.getListCourses().isEmpty());
    }

    @Test
    @DisplayName("Lanza una excepcion si el curso, ya tiene un profesor asignado")
    void assign_courseContainsTeacher_return409() throws Exception {
        Teacher teacher = teacherRepository.save(TeacherTestDataFactory.createTeacherFromIntegration());
        Course course = courseRepository.save(CourseTestDataFactory.createCourseFromIntegration());
        teacher.getListCourses().add(course);
        course.setTeacherCourse(teacher);
        course.setStatus(true);
        teacherRepository.save(teacher);
        courseRepository.save(course);
        Teacher teacherAdd = teacherRepository.save(this.createTeacherRepeat());
        Long idTeacher = teacherAdd.getId();
        Long idCourse = course.getId();

        mockMvc.perform(post("/teachers/assign/teacher/{idTeacher}/course/{idCourse}", idTeacher, idCourse))
                .andExpect(status().isConflict());

        Teacher teacherAddSaved = teacherRepository.findById(idTeacher).orElseThrow();
        Course courseSaved = courseRepository.findById(idCourse).orElseThrow();

        assertNotNull(courseSaved.getTeacherCourse());
        assertNotEquals(courseSaved.getTeacherCourse().getId(), idTeacher);
        assertTrue(
                teacherAddSaved.getListCourses().stream()
                        .map(Course::getId)
                        .noneMatch(c -> c.equals(idCourse))
        );
    }

    private Teacher createTeacherRepeat() {
        Teacher teacher = new Teacher();
        teacher.setName("Juan");
        teacher.setMail("juan@mail.com");
        teacher.setSpecialty("Ciencias");
        return teacher;
    }
}