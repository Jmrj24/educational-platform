package com.example.demo.application.student;

import com.example.demo.exception.NotFoundException;
import com.example.demo.student.IStudentService;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeleteStudentAccount {
    private final IStudentService studentService;
    private final UserSecRepository userSecRepository;

    public DeleteStudentAccount(IStudentService studentService, UserSecRepository userSecRepository) {
        this.studentService = studentService;
        this.userSecRepository = userSecRepository;
    }

    public void deleteStudentAndAccount(Long idStudent) {
        UserSec userSec = this.userSecRepository.findUserEntityByIdSubject(idStudent, SubjectType.ESTUDIANTE).orElseThrow(
                () -> new NotFoundException("El ID ingresado no pertenece a una cuenta registrada.")
        );
        this.studentService.deleteByIdStudent(idStudent);
        this.userSecRepository.delete(userSec);
    }
}
