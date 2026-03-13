package com.example.demo.application.student;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.course.CourseTestDataFactory;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentDisenrollmentServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private StudentRepository studentRepository;
    @InjectMocks
    private StudentDisenrollmentService studentDisenrollmentService;

    @Test
    @DisplayName("Elimina la suscripcion del estudiante en el curso")
    void studentUnsubscribe_studentAndCourseExist_unsubscribeExit() {
        Long idCourse = 20L;
        Long idStudent = 45L;
        Student student = StudentTestDataFactory.createStudent();
        Course course = CourseTestDataFactory.createCourse();
        student.getListCourses().add(course);
        course.getListStudents().add(student);

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(studentRepository.findById(idStudent)).thenReturn(Optional.of(student));

        studentDisenrollmentService.studentUnsubscribe(idCourse, idStudent);

        assertFalse(student.getListCourses().contains(course));
        assertFalse(course.getListStudents().contains(student));
    }

    @Test
    @DisplayName("Falla cuando el estudiante no existe")
    void studentUnsubscribe_studentNoExist_unsubscribeFail() {
        Long idCourse = 20L;
        Long idStudent = 45L;
        Course course = new Course();

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(studentRepository.findById(idStudent)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
                studentDisenrollmentService.studentUnsubscribe(idCourse, idStudent);
        });
    }

    @Test
    @DisplayName("Falla cuando el curso no existe")
    void studentUnsubscribe_courseNoExist_unsubscribeFail() {
        Long idCourse = 20L;
        Long idStudent = 45L;

        when(courseRepository.findById(idCourse)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            studentDisenrollmentService.studentUnsubscribe(idCourse, idStudent);
        });

        verify(studentRepository, never()).findById(any());
    }
}