package com.example.backend_Employee.mapper;

import com.example.backend_Employee.DTO.BatchErrorLogDTO;
import com.example.backend_Employee.Entities.BatchErrorLog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BatchErrorLogMapper {
    BatchErrorLogDTO toDTO(BatchErrorLog entity);
    BatchErrorLog toEntity(BatchErrorLogDTO dto);
}