package com.example.demo.student;

import com.example.demo.application.student.CreateStudentAccount;
import com.example.demo.application.student.DeleteStudentAccount;
import com.example.demo.application.student.StudentDisenrollmentService;
import com.example.demo.application.student.StudentEnrollmentService;
import com.example.demo.student.dto.StudentRequestDTO;
import com.example.demo.student.dto.StudentResponseDTO;
import com.example.demo.student.dto.StudentUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@PreAuthorize("denyAll()")
public class StudentController {
    private final IStudentService studentService;
    private final StudentEnrollmentService studentEnrollment;
    private final CreateStudentAccount createStudentAccount;
    private final DeleteStudentAccount deleteStudentAccount;
    private final StudentDisenrollmentService studentDisenrollment;

    public StudentController(IStudentService studentService, StudentEnrollmentService studentEnrollment,
                             CreateStudentAccount createStudentAccount, DeleteStudentAccount deleteStudentAccount,
                             StudentDisenrollmentService studentDisenrollment) {
        this.studentService = studentService;
        this.studentEnrollment = studentEnrollment;
        this.createStudentAccount = createStudentAccount;
        this.deleteStudentAccount = deleteStudentAccount;
        this.studentDisenrollment = studentDisenrollment;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<StudentResponseDTO> saveStudent(@Valid @RequestBody StudentRequestDTO studentRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.createStudentAccount.createStudentAndAccount(studentRequestDTO));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PROFESOR','ROLE_ESTUDIANTE')")
    public ResponseEntity<List<StudentResponseDTO>> findAllStudents() {
        return ResponseEntity.ok(this.studentService.findAllStudents());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR','ROLE_PROFESOR','ROLE_ESTUDIANTE')")
    public ResponseEntity<StudentResponseDTO> findByIdStudent(@PathVariable Long id) {
        return ResponseEntity.ok(this.studentService.findByIdStudent(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<StudentResponseDTO> updateStudent(@PathVariable Long id, @RequestBody StudentUpdateDTO studentUpdateDTO) {
        return ResponseEntity.ok(this.studentService.updateStudent(id, studentUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        this.deleteStudentAccount.deleteStudentAndAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inscription/{idCourse}/{idStudent}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<StudentResponseDTO> studentInscription(@PathVariable Long idCourse, @PathVariable Long idStudent) {
        this.studentEnrollment.studentInscription(idCourse, idStudent);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/unsubscribe/{idCourse}/{idStudent}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<StudentResponseDTO> studentUnsubscribe(@PathVariable Long idCourse, @PathVariable Long idStudent) {
        this.studentDisenrollment.studentUnsubscribe(idCourse, idStudent);
        return ResponseEntity.noContent().build();
    }
}
