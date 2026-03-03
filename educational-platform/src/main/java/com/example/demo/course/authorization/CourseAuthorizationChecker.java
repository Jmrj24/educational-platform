package com.example.demo.course.authorization;

import com.example.demo.course.Course;
import com.example.demo.course.CourseRepository;
import com.example.demo.course.CourseService;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.InvalidTokenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CourseAuthorizationChecker {
    private final UserSecRepository userSecRepository;
    private final CourseRepository courseRepository;

    public CourseAuthorizationChecker(UserSecRepository userSecRepository,
                                      CourseRepository courseRepository,
                                      CourseService courseService) {
        this.userSecRepository = userSecRepository;
        this.courseRepository = courseRepository;
    }

    public void authorizationUpdate(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null) {
            throw new InvalidTokenException("No estas correctamente Autenticado");
        }

        String username = authentication.getName();
        UserSec userSec = userSecRepository.findUserEntityByUsername(username).orElseThrow(() ->
                new NotFoundException("Username no se ha encontrado"));

        if(userSec.getSubjectType().equals(SubjectType.PROFESOR)) {
            Course updateCourse = courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Curso no encontrado"));
            if(!userSec.getIdSubject().equals(updateCourse.getTeacherCourse().getId())) {
                throw new ForbiddenException("No tienes permisos para modificar este recurso");
            }
        }
    }
}