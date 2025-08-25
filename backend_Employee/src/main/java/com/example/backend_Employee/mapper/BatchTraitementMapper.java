package com.example.backend_Employee.mapper;

import com.example.backend_Employee.DTO.BatchTraitementDTO;
import com.example.backend_Employee.Entities.BatchTraitement;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BatchTraitementMapper {

    BatchTraitementMapper INSTANCE = Mappers.getMapper(BatchTraitementMapper.class);

    BatchTraitementDTO toDTO(BatchTraitement entity);

    BatchTraitement toEntity(BatchTraitementDTO dto);
}