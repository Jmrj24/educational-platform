package com.example.demo.application.bootstrap;

import com.example.demo.permission.Permission;
import com.example.demo.permission.PermissionService;
import com.example.demo.role.NameRole;
import com.example.demo.role.Role;
import com.example.demo.role.RoleService;
import com.example.demo.userSec.SubjectType;
import com.example.demo.userSec.UserSec;
import com.example.demo.userSec.UserSecRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Transactional
@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserSecRepository userSecRepository;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminBootstrap(UserSecRepository userSecRepository, RoleService roleService,
                          PermissionService permissionService, PasswordEncoder passwordEncoder,
                          @Value("${app.admin.username}") String adminUsername, @Value("${app.admin.password}") String adminPassword) {
        this.userSecRepository = userSecRepository;
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    @Override
    public void run(String... args) throws Exception {
        if (this.userSecRepository.findUserEntityByUsername(adminUsername).isPresent()) {
            log.info("Admin already exists, skipping bootstrap");
            return;
        }

        Permission p1 = this.permissionService.findOrCreatePermission("CREATE");
        Permission p2 = this.permissionService.findOrCreatePermission("READ");
        Permission p3 = this.permissionService.findOrCreatePermission("UPDATE");
        Permission p4 = this.permissionService.findOrCreatePermission("DELETE");

        Role adminRole = roleService.findOrCreateRole(new HashSet<>(Set.of(p1, p2, p3 ,p4)), NameRole.ADMINISTRADOR);

        UserSec admin = new UserSec();
        admin.setUsername(adminUsername);
        admin.setPassword(this.passwordEncoder.encode(adminPassword));
        admin.setEnabled(true);
        admin.setAccountNotExpired(true);
        admin.setAccountNotLocked(true);
        admin.setCredentialNotExpired(true);
        admin.setRolesList(Set.of(adminRole));
        admin.setSubjectType(SubjectType.ADMINISTRADOR);
        this.userSecRepository.save(admin);
        log.info("Admin user created successfully");
    }
}
