package com.example.demo.course;

import com.example.demo.course.dto.CourseRequestDTO;
import com.example.demo.course.dto.CourseResponseDTO;
import com.example.demo.course.dto.CourseUpdateDTO;

import java.util.List;

public interface ICourseService {
    CourseResponseDTO saveCourse(CourseRequestDTO courseRequestDTO);
    List<CourseResponseDTO> findAllCourses();
    CourseResponseDTO findByIdCourse(Long id);
    void deleteByIdCourse(Long id);
    CourseResponseDTO updateCourse(Long id, CourseUpdateDTO courseUpdateDTO);
}
