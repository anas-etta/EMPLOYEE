package com.example.batch.Mapper;

import com.example.batch.DTO.BatchErrorLogDTO;
import com.example.batch.Entities.BatchErrorLog;

public class BatchErrorLogMapper {
    public static BatchErrorLog toEntity(BatchErrorLogDTO dto) {
        if (dto == null) return null;
        return new BatchErrorLog(dto.getErrorMessage(), dto.getFileName(), dto.getLigne());
    }
    public static BatchErrorLogDTO toDTO(BatchErrorLog entity) {
        if (entity == null) return null;
        return new BatchErrorLogDTO(entity.getId(), entity.getLigne(), entity.getErrorMessage(), entity.getFileName());
    }
}