package com.example.demo.course.authorization;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.course.CourseTestDataFactory;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.TeacherTestDataFactory;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseAuthorizationCheckerTest {
    @Mock
    private UserSecRepository userSecRepository;
    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private CourseAuthorizationChecker courseAuthorizationChecker;

    @Test
    @DisplayName("Debe lanzar una exception si no existe el usuario")
    void authorizationUpdate_userSecNoExist_runException() {
        Long idCourse = 54L;
        String username = "UsuarioPrueba";

        when(userSecRepository.findUserEntityByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            courseAuthorizationChecker.authorizationUpdate(idCourse, username);
        });

        verify(courseRepository, never()).findById(idCourse);
    }

    @Test
    @DisplayName("Debe autorizar si el usuario es un administrador")
    void authorizationUpdate_UserSecIsAdmin_authorizationExit() {
        Long idCourse = 54L;
        String username = "Maria85";
        UserSec userSec = this.createUserSec(username, SubjectType.ADMINISTRADOR, null);

        when(userSecRepository.findUserEntityByUsername(username)).thenReturn(Optional.of(userSec));

        assertDoesNotThrow(() ->
                courseAuthorizationChecker.authorizationUpdate(idCourse, username)
        );

        verify(courseRepository, never()).findById(idCourse);
    }

    @Test
    @DisplayName("Debe lanzar una exception si no existe el curso")
    void authorizationUpdate_CourseNoExist_runException() {
        Long idCourse = 54L;
        String username = "Maria85";
        UserSec userSec = this.createUserSec(username, SubjectType.PROFESOR, 145L);

        when(userSecRepository.findUserEntityByUsername(username)).thenReturn(Optional.of(userSec));
        when(courseRepository.findById(idCourse)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            courseAuthorizationChecker.authorizationUpdate(idCourse, username);
        });
    }

    @Test
    @DisplayName("Debe lanzar una exception si el profesor no esta asociado con el curso")
    void authorizationUpdate_UserSecNoMatchCourse_runException() {
        Long idCourse = 54L;
        String username = "Maria85";
        UserSec userSec = this.createUserSec(username, SubjectType.PROFESOR, 145L);
        Course course = CourseTestDataFactory.createCourse();
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        course.setTeacherCourse(teacher);

        when(userSecRepository.findUserEntityByUsername(username)).thenReturn(Optional.of(userSec));
        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));

        assertThrows(ForbiddenException.class, () -> {
            courseAuthorizationChecker.authorizationUpdate(idCourse, username);
        });
    }

    @Test
    @DisplayName("Debe autorizar si el usuario es un profesor y todo sale bien")
    void authorizationUpdate_UserSecMatchCourse_authorizationExit() {
        Long idCourse = 54L;
        String username = "Maria85";
        UserSec userSec = this.createUserSec(username, SubjectType.PROFESOR, 45L);
        Course course = CourseTestDataFactory.createCourse();
        Teacher teacher = TeacherTestDataFactory.createTeacher();
        course.setTeacherCourse(teacher);

        when(userSecRepository.findUserEntityByUsername(username)).thenReturn(Optional.of(userSec));
        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));

        assertDoesNotThrow(() ->
                courseAuthorizationChecker.authorizationUpdate(idCourse, username)
        );
    }

    private UserSec createUserSec(String username, SubjectType subjectType, Long idSubject) {
        return new UserSec(
                15L,
                username,
                "1234",
                true,
                true,
                true,
                true,
                new HashSet<>(),
                subjectType,
                idSubject);
    }
}