package com.example.backend_Employee.Controller;

import com.example.backend_Employee.DTO.KeycloakUserDTO;
import com.example.backend_Employee.Service.KeycloakAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/keycloak-users")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
public class KeycloakUserController {

    private final KeycloakAdminService keycloakAdminService;

    public KeycloakUserController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> getUsers() {
        return keycloakAdminService.getUsers();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody KeycloakUserDTO userDTO) {
        keycloakAdminService.createUser(userDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        keycloakAdminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // --------- NEW: Check if email exists in Keycloak ---------
    @GetMapping("/check-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        boolean exists = keycloakAdminService.emailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}