package com.example.backend_Employee.Controller;

import com.example.backend_Employee.DTO.BatchTraitementDTO;
import com.example.backend_Employee.Service.BatchTraitementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batch-traitements")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
public class BatchTraitementController {

    private final BatchTraitementService batchTraitementService;

    @Autowired
    public BatchTraitementController(BatchTraitementService batchTraitementService) {
        this.batchTraitementService = batchTraitementService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BatchTraitementDTO>> getBatchTraitements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BatchTraitementDTO> traitements = batchTraitementService.getBatchTraitements(PageRequest.of(page, size));
        return ResponseEntity.ok(traitements);
    }
}