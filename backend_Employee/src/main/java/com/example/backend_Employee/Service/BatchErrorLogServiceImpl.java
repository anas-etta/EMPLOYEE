package com.example.backend_Employee.Service;

import com.example.backend_Employee.DTO.BatchErrorLogDTO;
import com.example.backend_Employee.Entities.BatchErrorLog;
import com.example.backend_Employee.Repositories.BatchErrorLogRepository;
import com.example.backend_Employee.mapper.BatchErrorLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchErrorLogServiceImpl implements BatchErrorLogService {

    private final BatchErrorLogRepository errorLogRepository;
    private final BatchErrorLogMapper batchErrorLogMapper;

    @Autowired
    public BatchErrorLogServiceImpl(BatchErrorLogRepository errorLogRepository, BatchErrorLogMapper batchErrorLogMapper) {
        this.errorLogRepository = errorLogRepository;
        this.batchErrorLogMapper = batchErrorLogMapper;
    }

    @Override
    public void logError(String errorMessage, String fileName, int ligne) {
        BatchErrorLog errorLog = new BatchErrorLog(errorMessage, fileName, ligne);
        errorLogRepository.save(errorLog);
    }

    @Override
    public Page<BatchErrorLogDTO> getBatchErrorLogs(Pageable pageable) {
        return errorLogRepository.findAll(pageable).map(batchErrorLogMapper::toDTO);
    }

    @Override
    public List<BatchErrorLogDTO> getErrorLogsByFileName(String fileName) {
        return errorLogRepository.findByFileName(fileName)
                .stream()
                .map(batchErrorLogMapper::toDTO)
                .collect(Collectors.toList());
    }
}