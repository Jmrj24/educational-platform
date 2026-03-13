package com.example.demo.application.student;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.course.CourseTestDataFactory;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import com.example.demo.student.StudentTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentEnrollmentServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private StudentRepository studentRepository;
    @InjectMocks
    private StudentEnrollmentService studentEnrollmentService;

    @Test
    @DisplayName("Debe lanzar una excepcion si el curso no existe")
    void studentInscription_courseNoExist_runException() {
        Long idCourse = 20L;
        Long idStudent = 45L;

        when(courseRepository.findById(idCourse)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            studentEnrollmentService.studentInscription(idCourse, idStudent);
        });

        verify(studentRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Debe lanzar una excepcion si el estudiante no existe")
    void studentInscription_studentNoExist_runException() {
        Long idCourse = 20L;
        Long idStudent = 45L;
        Course course = new Course();

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(studentRepository.findById(idStudent)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            studentEnrollmentService.studentInscription(idCourse, idStudent);
        });
    }

    @Test
    @DisplayName("Debe lanzar una excepcion si el estudiante ya esta inscrito en el curso")
    void studentInscription_studentDuplicated_runException() {
        Long idCourse = 20L;
        Long idStudent = 89L;
        Student student = StudentTestDataFactory.createStudent();
        Course course = CourseTestDataFactory.createCourse();
        course.getListStudents().add(student);
        student.getListCourses().add(course);

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(studentRepository.findById(idStudent)).thenReturn(Optional.of(student));

        assertThrows(ConflictException.class, () -> {
            studentEnrollmentService.studentInscription(idCourse, idStudent);
        });
    }

    @Test
    @DisplayName("Debe inscribir al estudiante en el curso")
    void studentInscription_courseAndStudentExist_inscriptionSuccess() {
        Long idCourse = 20L, idStudent = 45L;
        Student student = StudentTestDataFactory.createStudent();
        Course course = CourseTestDataFactory.createCourse();

        when(studentRepository.findById(idStudent)).thenReturn(Optional.of(student));
        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));

        studentEnrollmentService.studentInscription(idCourse, idStudent);

        assertTrue(student.getListCourses().contains(course));
        assertTrue(course.getListStudents().contains(student));
    }
}