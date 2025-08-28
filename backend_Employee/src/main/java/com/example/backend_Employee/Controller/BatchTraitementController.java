package com.example.backend_Employee.Controller;

import com.example.backend_Employee.DTO.BatchTraitementDTO;
import com.example.backend_Employee.Service.BatchTraitementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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


    private Sort getSortOrder(String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "id");
        }
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            return Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
        } else {
            return Sort.by(Sort.Direction.ASC, parts[0]);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BatchTraitementDTO>> getBatchTraitements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, getSortOrder(sort));
        Page<BatchTraitementDTO> traitements = batchTraitementService.getBatchTraitements(pageable);
        return ResponseEntity.ok(traitements);
    }
}