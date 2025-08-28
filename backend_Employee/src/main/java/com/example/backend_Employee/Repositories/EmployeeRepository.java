package com.example.backend_Employee.Repositories;

import com.example.backend_Employee.Entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Employee findByEmail(String email);

    Employee findByImmatriculation(String immatriculation);


    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByImmatriculationAndIdNot(String immatriculation, Long id);


    @Query("SELECT e FROM Employee e " +
            "WHERE (:firstName IS NULL OR :firstName = '' OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
            "AND (:lastName IS NULL OR :lastName = '' OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
            "AND (:email IS NULL OR :email = '' OR LOWER(e.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "AND (:immatriculation IS NULL OR :immatriculation = '' OR LOWER(e.immatriculation) LIKE LOWER(CONCAT('%', :immatriculation, '%')))")
    Page<Employee> searchByMultipleFields(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("immatriculation") String immatriculation,
            Pageable pageable
    );
}