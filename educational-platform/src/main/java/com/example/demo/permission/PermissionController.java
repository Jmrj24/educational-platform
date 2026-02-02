package com.example.demo.permission;

import com.example.demo.permission.dto.PermissionRequestDTO;
import com.example.demo.permission.dto.PermissionResponseDTO;
import com.example.demo.permission.dto.PermissionUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/permissions")
@PreAuthorize("denyAll()")
public class PermissionController {
    private final IPermissionService permissionService;

    public PermissionController(IPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<PermissionResponseDTO> savePermission(@Valid @RequestBody PermissionRequestDTO permissionRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.savePermission(permissionRequestDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Set<PermissionResponseDTO>> findAllPermissions() {
        return ResponseEntity.ok(this.permissionService.findAllPermissions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<PermissionResponseDTO> findByIdPermission(@PathVariable Long id) {
        return ResponseEntity.ok(this.permissionService.findByIdPermission(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<Void> deleteByIdPermission(@PathVariable Long id) {
        this.permissionService.deleteByIdPermission(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<PermissionResponseDTO> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionUpdateDTO permissionUpdateDTO) {
        return ResponseEntity.ok(this.permissionService.updatePermission(id, permissionUpdateDTO));
    }

}
