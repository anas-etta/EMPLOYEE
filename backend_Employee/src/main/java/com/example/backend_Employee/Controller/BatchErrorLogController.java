package com.example.backend_Employee.Controller;

import com.example.backend_Employee.DTO.BatchErrorLogDTO;
import com.example.backend_Employee.Service.BatchErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batch-error-logs")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
public class BatchErrorLogController {

    private final BatchErrorLogService batchErrorLogService;

    @Autowired
    public BatchErrorLogController(BatchErrorLogService batchErrorLogService) {
        this.batchErrorLogService = batchErrorLogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BatchErrorLogDTO>> getBatchErrorLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BatchErrorLogDTO> logs = batchErrorLogService.getBatchErrorLogs(PageRequest.of(page, size));
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/by-filename/{fileName}")
    public List<BatchErrorLogDTO> getErrorLogsByFileName(@PathVariable String fileName) {
        return batchErrorLogService.getErrorLogsByFileName(fileName);
    }
}