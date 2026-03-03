package com.example.demo.course;

import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import com.example.demo.course.dto.CourseRequestDTO;
import com.example.demo.course.dto.CourseResponseDTO;
import com.example.demo.course.dto.CourseUpdateDTO;
import com.example.demo.course.mapper.CourseMapper;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.InvalidTokenException;
import com.example.demo.exception.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService implements ICourseService{
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    public CourseResponseDTO saveCourse(CourseRequestDTO courseRequestDTO) {
        Course courseNew = new Course();
        courseNew.setName(courseRequestDTO.name());
        courseNew.setDescription(courseRequestDTO.description());
        courseNew.setStatus(false);
        return this.courseMapper.toCourseResponse(this.courseRepository.save(courseNew));
    }

    @Override
    public List<CourseResponseDTO> findAllCourses() {
        return this.courseRepository.findAll().stream()
                .map(courseMapper::toCourseResponse)
                .toList();
    }

    @Override
    public CourseResponseDTO findByIdCourse(Long id) {
        return this.courseMapper.toCourseResponse(this.findByIdCourseEntity(id));
    }

    @Override
    public void deleteByIdCourse(Long id) {
        this.courseRepository.deleteById(id);
    }

    @Override
    public CourseResponseDTO updateCourse(Long id, CourseUpdateDTO courseUpdateDTO) {
        Course updateCourse = this.findByIdCourseEntity(id);

        if(courseUpdateDTO.getName()!=null&&!courseUpdateDTO.getName().isBlank()) {
            updateCourse.setName(courseUpdateDTO.getName());
        }
        if(courseUpdateDTO.getDescription()!=null&&!courseUpdateDTO.getDescription().isBlank()) {
            updateCourse.setDescription(courseUpdateDTO.getDescription());
        }
        return this.courseMapper.toCourseResponse(this.courseRepository.save(updateCourse));
    }

    private Course findByIdCourseEntity(Long id) {
        return this.courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Curso no encontrado"));
    }
}