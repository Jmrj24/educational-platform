package com.example.demo.course;

import com.example.demo.course.dto.CourseRequestDTO;
import com.example.demo.course.dto.CourseResponseDTO;
import com.example.demo.course.dto.CourseUpdateDTO;
import com.example.demo.course.mapper.CourseMapper;
import com.example.demo.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseMapper courseMapper;
    @InjectMocks
    private CourseService  courseService;
    @Captor
    private ArgumentCaptor<Course> courseCaptor;

    @Test
    @DisplayName("Debe guardar un curso si todo sale bien")
    void saveCourse_requestCourseValid_courseSaved() {
        CourseRequestDTO courseRequestDTO = new CourseRequestDTO("Logica de programacion","aspectos basicos");
        Course courseNew = CourseTestDataFactory.createCourse();
        CourseResponseDTO courseResponseDTO = CourseTestDataFactory.createCourseResponse(courseNew, null, Collections.emptyList());

        when(courseRepository.save(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
        when(courseMapper.toCourseResponse(any())).thenReturn(courseResponseDTO);

        CourseResponseDTO courseResponseResult = courseService.saveCourse(courseRequestDTO);

        verify(courseRepository).save(courseCaptor.capture());
        Course courseResult = courseCaptor.getValue();
        verify(courseMapper).toCourseResponse(courseResult);

        assertNotNull(courseResponseResult);
        assertEquals(courseResponseResult, courseResponseDTO);
        assertEquals(courseResult.getName(), courseRequestDTO.name());
        assertEquals(courseResult.getDescription(), courseRequestDTO.description());
        assertFalse(courseResult.isStatus());
    }

    @Test
    @DisplayName("Debe devolver un lista de cursos DTO si existen")
    void findAllCourses_coursesExist_returnCourses() {
        Course course = CourseTestDataFactory.createCourse();
        List<Course> listCourses = new ArrayList<>(List.of(course));
        CourseResponseDTO courseResponseDTO = CourseTestDataFactory.createCourseResponse(course, null, Collections.emptyList());
        List<CourseResponseDTO> listCoursesResponseExpect = new ArrayList<>(List.of(courseResponseDTO));

        when(courseRepository.findAll()).thenReturn(listCourses);
        when(courseMapper.toCourseResponse(course)).thenReturn(courseResponseDTO);

        List<CourseResponseDTO> listCoursesResponseResult = courseService.findAllCourses();

        assertEquals(listCoursesResponseExpect, listCoursesResponseResult);
    }

    @Test
    @DisplayName("Debe devolver un lista vacia si no hay registro de cursos")
    void findAllCourses_coursesNoExist_returnListEmpty() {
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        List<CourseResponseDTO> listCoursesResponseResult = courseService.findAllCourses();

        assertNotNull(listCoursesResponseResult);
        assertTrue(listCoursesResponseResult.isEmpty());
    }

    @Test
    @DisplayName("Debe lanzar una excepcion si el ID no pertenece a un curso")
    void findByIdCourse_coursesNoExist_runException() {
        Long idCourse = 15L;

        when(courseRepository.findById(idCourse)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            courseService.findByIdCourse(idCourse);
        });
    }

    @Test
    @DisplayName("Debe devolver un curso si el ID existe")
    void findByIdCourse_coursesExist_returnCourse() {
        Long idCourse = 20L;
        Course course = CourseTestDataFactory.createCourse();
        CourseResponseDTO courseResponseExpect = CourseTestDataFactory.createCourseResponse(course, null, Collections.emptyList());

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(courseMapper.toCourseResponse(course)).thenReturn(courseResponseExpect);

        CourseResponseDTO courseResponseResult = courseService.findByIdCourse(idCourse);

        verify(courseRepository).findById(idCourse);
        assertEquals(courseResponseExpect, courseResponseResult);
    }

    @Test
    @DisplayName("Debe actualizar el curso si todo sale bien")
    void updateCourse_courseExist_updateSuccessful() {
        Long idCourse = 20L;
        Course course = CourseTestDataFactory.createCourse();
        CourseUpdateDTO courseUpdate = new CourseUpdateDTO("Nombre nuevo", "Nueva descripcion");

        when(courseRepository.findById(idCourse)).thenReturn(Optional.of(course));
        when(courseRepository.save(course)).thenReturn(course);
        CourseResponseDTO courseResponseExpected = CourseTestDataFactory.createCourseResponse(course, null, Collections.emptyList());
        when(courseMapper.toCourseResponse(course)).thenReturn(courseResponseExpected);

        CourseResponseDTO courseResponseResult = courseService.updateCourse(idCourse, courseUpdate);

        assertEquals(course.getName(), courseUpdate.getName());
        assertEquals(course.getDescription(), courseUpdate.getDescription());
        verify(courseRepository).save(course);
        verify(courseMapper).toCourseResponse(course);
        assertEquals(courseResponseExpected, courseResponseResult);
    }

    @Test
    @DisplayName("Debe lanzar una exception si no existe el ID")
    void updateCourse_courseNoExist_runException() {
        Long idCourse = 20L;
        CourseUpdateDTO courseUpdate = new CourseUpdateDTO("Nombre nuevo", "Nueva descripcion");

        when(courseRepository.findById(idCourse)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            courseService.updateCourse(idCourse, courseUpdate);
        });

        verify(courseRepository, never()).save(any());
    }
}