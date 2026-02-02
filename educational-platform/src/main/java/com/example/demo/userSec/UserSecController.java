package com.example.demo.userSec;

import com.example.demo.userSec.dto.UserSecRequestDTO;
import com.example.demo.userSec.dto.UserSecResponseDTO;
import com.example.demo.userSec.dto.UserSecUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("denyAll()")
public class UserSecController {
    private final IUserSecService userSecService;

    public UserSecController(IUserSecService userSecService) {
        this.userSecService = userSecService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<UserSecResponseDTO> saveUserSec(@Valid @RequestBody UserSecRequestDTO userSecRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.userSecService.saveUserSec(userSecRequestDTO, SubjectType.ADMINISTRADOR, Optional.empty()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Set<UserSecResponseDTO>> findAllUserSecs() {
        return ResponseEntity.ok(this.userSecService.findAllUserSecs());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<UserSecResponseDTO> findByIdUserSec(@PathVariable Long id) {
        return ResponseEntity.ok(this.userSecService.findByIdUserSec(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Void> deleteUserSec(@PathVariable Long id) {
        this.userSecService.deleteByIdUserSec(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<UserSecResponseDTO> updateUserSec(@PathVariable Long id, @RequestBody UserSecUpdateDTO userSecUpdateDTO) {
        return ResponseEntity.ok(this.userSecService.updateUserSec(id, userSecUpdateDTO));
    }
}
