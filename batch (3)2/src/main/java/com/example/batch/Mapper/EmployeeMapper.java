package com.example.batch.Mapper;

import com.example.batch.DTO.EmployeeDTO;
import com.example.batch.Entities.Employee;

public class EmployeeMapper {
    public static Employee toEntity(EmployeeDTO dto) {
        if (dto == null) return null;
        return new Employee(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getImmatriculation()
        );
    }
}