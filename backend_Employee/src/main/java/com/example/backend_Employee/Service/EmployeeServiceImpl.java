package com.example.backend_Employee.Service;

import com.example.backend_Employee.DTO.EmployeeDTO;
import com.example.backend_Employee.Entities.Employee;
import com.example.backend_Employee.Repositories.EmployeeRepository;
import com.example.backend_Employee.mapper.EmployeeMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeMapper.toEntity(employeeDTO);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toDTO(saved);
    }

    @Override
    public Page<EmployeeDTO> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(employeeMapper::toDTO);
    }

    @Override
    public Optional<EmployeeDTO> getEmployeeById(Long id) {
        return employeeRepository.findById(id).map(employeeMapper::toDTO);
    }

    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        // Uniqueness check for immatriculation and email (excluding current employee)
        if (employeeRepository.existsByImmatriculationAndIdNot(employeeDTO.getImmatriculation(), id)) {
            throw new RuntimeException("Immatriculation déjà utilisée");
        }
        if (employeeRepository.existsByEmailAndIdNot(employeeDTO.getEmail(), id)) {
            throw new RuntimeException("Email déjà utilisé");
        }

        return employeeRepository.findById(id).map(employee -> {
            employee.setFirstName(employeeDTO.getFirstName());
            employee.setLastName(employeeDTO.getLastName());
            employee.setEmail(employeeDTO.getEmail());
            employee.setImmatriculation(employeeDTO.getImmatriculation());
            Employee saved = employeeRepository.save(employee);
            return employeeMapper.toDTO(saved);
        }).orElseThrow(() -> new RuntimeException("Employé non trouvé"));
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public Page<EmployeeDTO> searchEmployees(String query, Pageable pageable) {
        return employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query, query, pageable)
                .map(employeeMapper::toDTO);
    }

    @Override
    public boolean emailExists(String email) {
        return employeeRepository.findByEmail(email) != null;
    }

    @Override
    public boolean immatriculationExists(String immatriculation) {
        return employeeRepository.findByImmatriculation(immatriculation) != null;
    }

    @Override
    public boolean emailExistsForUpdate(String email, Long id) {
        return employeeRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public boolean immatriculationExistsForUpdate(String immatriculation, Long id) {
        return employeeRepository.existsByImmatriculationAndIdNot(immatriculation, id);
    }

    // New method for field-specific search
    @Override
    public Page<EmployeeDTO> searchEmployeesByField(String field, String value, Pageable pageable) {
        switch (field) {
            case "firstName":
                return employeeRepository.findByFirstNameContainingIgnoreCase(value, pageable).map(employeeMapper::toDTO);
            case "lastName":
                return employeeRepository.findByLastNameContainingIgnoreCase(value, pageable).map(employeeMapper::toDTO);
            case "email":
                return employeeRepository.findByEmailContainingIgnoreCase(value, pageable).map(employeeMapper::toDTO);
            case "immatriculation":
                return employeeRepository.findByImmatriculationContainingIgnoreCase(value, pageable).map(employeeMapper::toDTO);
            default:
                return Page.empty(pageable);
        }
    }
}