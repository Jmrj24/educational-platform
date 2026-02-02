package com.example.demo.application.teacher;

import com.example.demo.exception.NotFoundException;
import com.example.demo.teacher.ITeacherService;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeleteTeacherAccount {
    private final ITeacherService teacherService;
    private final UserSecRepository userSecRepository;

    public DeleteTeacherAccount(ITeacherService teacherService, UserSecRepository userSecRepository) {
        this.teacherService = teacherService;
        this.userSecRepository = userSecRepository;
    }

    public void deleteTeacherAndAccount(Long idTeacher) {
        UserSec userSec = this.userSecRepository.findUserEntityByIdSubject(idTeacher, SubjectType.PROFESOR).orElseThrow(
                () -> new NotFoundException("El ID ingresado no pertenece a una cuenta registrada.")
        );
        this.teacherService.deleteByIdTeacher(idTeacher);
        this.userSecRepository.delete(userSec);
    }
}
