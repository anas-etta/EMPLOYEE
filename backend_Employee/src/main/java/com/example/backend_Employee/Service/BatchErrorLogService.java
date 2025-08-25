package com.example.backend_Employee.Service;

import com.example.backend_Employee.DTO.BatchErrorLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BatchErrorLogService {
    void logError(String errorMessage, String fileName, int ligne);
    Page<BatchErrorLogDTO> getBatchErrorLogs(Pageable pageable);
    List<BatchErrorLogDTO> getErrorLogsByFileName(String fileName);
}