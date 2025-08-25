package com.example.backend_Employee.Service;

import com.example.backend_Employee.DTO.BatchTraitementDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BatchTraitementService {
    Page<BatchTraitementDTO> getBatchTraitements(Pageable pageable);
}