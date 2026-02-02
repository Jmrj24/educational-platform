package com.example.demo.course;

import com.example.demo.course.dto.CourseRequestDTO;
import com.example.demo.course.dto.CourseResponseDTO;
import com.example.demo.course.dto.CourseUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@PreAuthorize("denyAll()")
public class CourseController {
    private final ICourseService courseService;

    public CourseController(ICourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<CourseResponseDTO> saveCourse(@Valid @RequestBody CourseRequestDTO courseRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.courseService.saveCourse(courseRequestDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PROFESOR','ROLE_ESTUDIANTE')")
    public ResponseEntity<List<CourseResponseDTO>> findAllCourses() {
        return ResponseEntity.ok(this.courseService.findAllCourses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PROFESOR','ROLE_ESTUDIANTE')")
    public ResponseEntity<CourseResponseDTO> findByIdCourse(@PathVariable Long id) {
        return ResponseEntity.ok(this.courseService.findByIdCourse(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PROFESOR')")
    public ResponseEntity<CourseResponseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseUpdateDTO courseUpdateDTO) {
        return ResponseEntity.ok(this.courseService.updateCourse(id, courseUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        this.courseService.deleteByIdCourse(id);
        return ResponseEntity.noContent().build();
    }
}
