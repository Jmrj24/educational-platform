package com.example.demo.integration.student;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.course.CourseTestDataFactory;
import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import com.example.demo.student.StudentTestDataFactory;
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
public class StudentEnrollmentIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Test
    @DisplayName("Inscribe un estudiante en un curso si todo sale bien")
    void studentInscription_courseAndStudentExits_studentInscriptionSuccessful() throws Exception {
        Student student = studentRepository.save(StudentTestDataFactory.createStudentFromIntegration());
        Course course = courseRepository.save(CourseTestDataFactory.createCourseFromIntegration());
        Long idCourse = course.getId();
        Long idStudent = student.getId();

        mockMvc.perform(post("/students/inscription/student/{idStudent}/course/{idCourse}", idStudent, idCourse))
                .andExpect(status().isNoContent());

        Course courseSaved = courseRepository.findById(idCourse).orElseThrow();
        Student studentSaved = studentRepository.findById(idStudent).orElseThrow();

        assertTrue(
                studentSaved.getListCourses()
                        .stream()
                        .anyMatch(c -> c.getId().equals(idCourse))
        );

        assertTrue(
                courseSaved.getListStudents()
                        .stream()
                        .anyMatch(s -> s.getId().equals(idStudent))
        );
    }

    @Test
    @DisplayName("Debe devolver un 404 al no existir el estudiante")
    void studentInscription_studentNoExits_return404() throws Exception {
        Course course = courseRepository.save(CourseTestDataFactory.createCourseFromIntegration());
        Long idCourse = course.getId();
        Long idStudent = 1950L;

        mockMvc.perform(post("/students/inscription/student/{idStudent}/course/{idCourse}", idStudent, idCourse))
                .andExpect(status().isNotFound());

        Course courseSaved = courseRepository.findById(idCourse).orElseThrow();

        assertTrue(courseSaved.getListStudents().isEmpty());
    }

    @Test
    @DisplayName("Debe devolver un 404 al no existir el curso")
    void studentInscription_courseNoExits_return404() throws Exception {
        Student student = studentRepository.save(StudentTestDataFactory.createStudentFromIntegration());
        Long idStudent = student.getId();
        Long idCourse = 1950L;

        mockMvc.perform(post("/students/inscription/student/{idStudent}/course/{idCourse}", idStudent, idCourse))
                .andExpect(status().isNotFound());

        Student studentSaved = studentRepository.findById(idStudent).orElseThrow();

        assertTrue(studentSaved.getListCourses().isEmpty());
    }

    @Test
    @DisplayName("Lanza una excepcion si el estudiante ya esta inscrito")
    void studentInscription_studentDuplicated_runException() throws Exception {
        Student student = studentRepository.save(StudentTestDataFactory.createStudentFromIntegration());
        Course course = courseRepository.save(CourseTestDataFactory.createCourseFromIntegration());
        student.getListCourses().add(course);
        course.getListStudents().add(student);
        studentRepository.save(student);
        courseRepository.save(course);
        Long idCourse = course.getId();
        Long idStudent = student.getId();

        mockMvc.perform(post("/students/inscription/student/{idStudent}/course/{idCourse}", idStudent, idCourse))
                .andExpect(status().isConflict());

        Course courseSaved = courseRepository.findById(idCourse).orElseThrow();
        Student studentSaved = studentRepository.findById(idStudent).orElseThrow();

        assertEquals(1, studentSaved.getListCourses().stream()
                        .filter(c -> c.getId().equals(idCourse))
                        .count());
        assertEquals(1, courseSaved.getListStudents().stream()
                .filter(s -> s.getId().equals(idStudent))
                .count());
    }
}