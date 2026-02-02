package com.example.demo.teacher;

import com.example.demo.application.teacher.AssignTeacherToCourseService;
import com.example.demo.application.teacher.CreateTeacherAccount;
import com.example.demo.application.teacher.DeleteTeacherAccount;
import com.example.demo.application.teacher.UnassignTeacherToCourseService;
import com.example.demo.teacher.dto.TeacherRequestDTO;
import com.example.demo.teacher.dto.TeacherResponseDTO;
import com.example.demo.teacher.dto.TeacherUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
@PreAuthorize("denyAll()")
public class TeacherController {
    private final ITeacherService teacherService;
    private final AssignTeacherToCourseService assignTeacherToCourse;
    private final CreateTeacherAccount createTeacherAccount;
    private final DeleteTeacherAccount deleteTeacherAccount;
    private final UnassignTeacherToCourseService unassignTeacherToCourse;

    public TeacherController(ITeacherService teacherService, AssignTeacherToCourseService assignTeacherToCourse,
                             CreateTeacherAccount createTeacherAccount, DeleteTeacherAccount deleteTeacherAccount,
                             UnassignTeacherToCourseService unassignTeacherToCourse) {
        this.teacherService = teacherService;
        this.assignTeacherToCourse = assignTeacherToCourse;
        this.createTeacherAccount = createTeacherAccount;
        this.deleteTeacherAccount = deleteTeacherAccount;
        this.unassignTeacherToCourse = unassignTeacherToCourse;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<TeacherResponseDTO> saveTeacher(@Valid @RequestBody TeacherRequestDTO teacherRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.createTeacherAccount.createTeacherAndAccount(teacherRequestDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PROFESOR')")
    public ResponseEntity<List<TeacherResponseDTO>> findAllTeachers() {
        return ResponseEntity.ok(this.teacherService.findAllTeachers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PROFESOR')")
    public ResponseEntity<TeacherResponseDTO> findByIdTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(this.teacherService.findByIdTeacher(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<TeacherResponseDTO> updateTeacher(@PathVariable Long id, @RequestBody TeacherUpdateDTO teacherUpdateDTO) {
        return ResponseEntity.ok(this.teacherService.updateTeacher(id, teacherUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        this.deleteTeacherAccount.deleteTeacherAndAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign/{idCourse}/{idTeacher}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<TeacherResponseDTO> teacherAssign(@PathVariable Long idCourse, @PathVariable Long idTeacher) {
        this.assignTeacherToCourse.assign(idCourse, idTeacher);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/unassign/{idCourse}/{idTeacher}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<TeacherResponseDTO> teacherUnassign(@PathVariable Long idCourse, @PathVariable Long idTeacher) {
        this.unassignTeacherToCourse.unassign(idCourse, idTeacher);
        return ResponseEntity.noContent().build();
    }
}
