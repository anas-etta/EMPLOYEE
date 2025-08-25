package com.example.backend_Employee.Controller;

import com.example.backend_Employee.DTO.EmployeeDTO;
import com.example.backend_Employee.Service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // 🔹 Ajouter un nouvel employé (ADMIN uniquement)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        EmployeeDTO savedEmployee = employeeService.createEmployee(employeeDTO);
        return ResponseEntity.ok(savedEmployee);
    }

    // 🔹 Récupérer tous les employés (ADMIN et USER)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<EmployeeDTO>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeDTO> employees = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(employees);
    }

    // 🔹 Récupérer un employé par son ID (ADMIN et USER)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        Optional<EmployeeDTO> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Mettre à jour un employé (ADMIN uniquement)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
            return ResponseEntity.ok(updatedEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Supprimer un employé (ADMIN uniquement)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Rechercher des employés par prénom, nom ou email avec pagination (ADMIN et USER)
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<EmployeeDTO>> searchEmployees(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeDTO> employees = employeeService.searchEmployees(query, pageable);
        return ResponseEntity.ok(employees);
    }

    // 🔹 Nouveau : Recherche par champ spécifique (ADMIN et USER)
    @GetMapping("/search-by-field")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<EmployeeDTO>> searchEmployeesByField(
            @RequestParam String field,
            @RequestParam String value,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeDTO> employees = employeeService.searchEmployeesByField(field, value, pageable);
        return ResponseEntity.ok(employees);
    }

    // 🔹 Vérifier si un email existe déjà (ADMIN et USER)
    @GetMapping("/check-email")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email, @RequestParam(required = false) Long id) {
        boolean exists;
        if (id != null) {
            exists = employeeService.emailExistsForUpdate(email, id);
        } else {
            exists = employeeService.emailExists(email);
        }
        return ResponseEntity.ok(exists);
    }

    // 🔹 Vérifier si une immatriculation existe déjà (ADMIN et USER)
    @GetMapping("/check-immatriculation")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Boolean> checkImmatriculationExists(@RequestParam String immatriculation, @RequestParam(required = false) Long id) {
        boolean exists;
        if (id != null) {
            exists = employeeService.immatriculationExistsForUpdate(immatriculation, id);
        } else {
            exists = employeeService.immatriculationExists(immatriculation);
        }
        return ResponseEntity.ok(exists);
    }
}