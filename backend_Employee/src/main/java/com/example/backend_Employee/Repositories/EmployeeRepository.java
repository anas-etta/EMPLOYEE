package com.example.backend_Employee.Repositories;

import com.example.backend_Employee.Entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    Page<Employee> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    Page<Employee> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<Employee> findByImmatriculationContainingIgnoreCase(String immatriculation, Pageable pageable);

    Employee findByEmail(String email);

    Employee findByImmatriculation(String immatriculation);

    // These methods are used for uniqueness checks during update (exclude current employee by id)
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByImmatriculationAndIdNot(String immatriculation, Long id);

    Page<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, Pageable pageable);

}