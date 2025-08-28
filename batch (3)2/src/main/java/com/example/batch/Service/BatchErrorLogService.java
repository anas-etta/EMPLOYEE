package com.example.batch.Service;

import com.example.batch.DTO.BatchErrorLogDTO;
import com.example.batch.Mapper.BatchErrorLogMapper;
import com.example.batch.Repository.BatchErrorLogRepository;
import org.springframework.stereotype.Service;

@Service
public class BatchErrorLogService {

    private final BatchErrorLogRepository errorLogRepository;

    public BatchErrorLogService(BatchErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    public void logError(BatchErrorLogDTO dto) {
        errorLogRepository.save(BatchErrorLogMapper.toEntity(dto));
    }

    public void logError(String errorMessage, String fileName, int ligne) {
        BatchErrorLogDTO dto = new BatchErrorLogDTO(null, ligne, errorMessage, fileName);
        logError(dto);
    }


    public void logMinioError(String errorMessage, String fileName) {
        BatchErrorLogDTO dto = new BatchErrorLogDTO(null, -1, errorMessage, fileName);
        logError(dto);
    }
}