package com.example.backend_Employee.Service;

import com.example.backend_Employee.DTO.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmployeeService {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    Page<EmployeeDTO> getAllEmployees(Pageable pageable);
    Optional<EmployeeDTO> getEmployeeById(Long id);
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
    void deleteEmployee(Long id);


    Page<EmployeeDTO> searchEmployees(String firstName, String lastName, String email, String immatriculation, Pageable pageable);

    boolean emailExists(String email);

    boolean immatriculationExists(String immatriculation);


    boolean emailExistsForUpdate(String email, Long id);
    boolean immatriculationExistsForUpdate(String immatriculation, Long id);
}