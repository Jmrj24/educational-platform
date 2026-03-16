package com.example.demo.integration.teacher;

import com.example.demo.role.Role;
import com.example.demo.teacher.Teacher;
import com.example.demo.teacher.TeacherRepository;
import com.example.demo.teacher.TeacherTestDataFactory;
import com.example.demo.teacher.dto.TeacherRequestDTO;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import com.example.demo.userSec.UserSecTestDataFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMINISTRADOR")
@ActiveProfiles("test")
public class CreateTeacherAccountIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserSecRepository userSecRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Crea el profesor y su cuenta asociada, si todo sale bien")
    @Transactional
    void saveTeacher_teacherRequestValid_return201() throws Exception {
        TeacherRequestDTO teacherRequest = TeacherTestDataFactory.createValidRequest();
        teacherRequest.rolesListIds().add(1L); // Rol usado, es el creado por el bootstrap

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherRequest)))
                .andExpect(status().isCreated());

        Teacher teacherSaved = teacherRepository.findTeacherEntityByMail(teacherRequest.mail()).orElseThrow();
        UserSec userSecSaved = userSecRepository.findUserEntityByUsername(teacherRequest.username()).orElseThrow();

        assertEquals(teacherRequest.name(), teacherSaved.getName());
        assertEquals(teacherRequest.mail(), teacherSaved.getMail());
        assertEquals(teacherRequest.specialty(), teacherSaved.getSpecialty());
        assertEquals(SubjectType.PROFESOR, userSecSaved.getSubjectType());
        assertEquals(teacherSaved.getId(), userSecSaved.getIdSubject());
        assertTrue(
                userSecSaved.getRolesList().stream()
                        .map(Role::getId)
                        .anyMatch(r -> r.equals(1L))
        );
    }

    @Test
    @DisplayName("Lanza una excepcion al ingresar un mail de profesor ya registrado")
    @Transactional
    void saveTeacher_teacherMailRepeat_return409() throws Exception {
        teacherRepository.save(TeacherTestDataFactory.createTeacherFromIntegration());
        Long teacherCountBefore = teacherRepository.count();
        Long userCountBefore = userSecRepository.count();
        TeacherRequestDTO teacherRequest = TeacherTestDataFactory.createValidRequest();
        teacherRequest.rolesListIds().add(1L);

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherRequest)))
                .andExpect(status().isConflict());

        assertEquals(teacherCountBefore, teacherRepository.count());
        assertEquals(userCountBefore, userSecRepository.count());
    }

    @Test
    @DisplayName("Lanza una excepcion al ingresar un username de profesor ya registrado")
    void saveTeacher_usernameRepeat_return409() throws Exception {
        TeacherRequestDTO teacherRequest = TeacherTestDataFactory.createValidRequest();
        teacherRequest.rolesListIds().add(1L);
        UserSec userSec = UserSecTestDataFactory.createUserSecFromIntegration();
        userSec.setUsername(teacherRequest.username());
        userSecRepository.save(userSec);
        Long teacherCountBefore = teacherRepository.count();
        Long userCountBefore = userSecRepository.count();

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherRequest)))
                .andExpect(status().isConflict());

        assertEquals(teacherCountBefore, teacherRepository.count());
        assertEquals(userCountBefore, userSecRepository.count());

        teacherRepository.deleteAll();
        userSecRepository.deleteAll();
    }
}