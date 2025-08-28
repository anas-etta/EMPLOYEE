package com.example.batch.Repository;

import com.example.batch.Entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);
    boolean existsByImmatriculation(String immatriculation);
}