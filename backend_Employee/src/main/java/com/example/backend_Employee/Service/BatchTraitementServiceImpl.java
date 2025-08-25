package com.example.backend_Employee.Service;

import com.example.backend_Employee.DTO.BatchTraitementDTO;
import com.example.backend_Employee.Entities.BatchTraitement;
import com.example.backend_Employee.Repositories.BatchTraitementRepository;
import com.example.backend_Employee.mapper.BatchTraitementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BatchTraitementServiceImpl implements BatchTraitementService {

    private final BatchTraitementRepository batchTraitementRepository;
    private final BatchTraitementMapper batchTraitementMapper;

    @Autowired
    public BatchTraitementServiceImpl(BatchTraitementRepository batchTraitementRepository, BatchTraitementMapper batchTraitementMapper) {
        this.batchTraitementRepository = batchTraitementRepository;
        this.batchTraitementMapper = batchTraitementMapper;
    }

    @Override
    public Page<BatchTraitementDTO> getBatchTraitements(Pageable pageable) {
        return batchTraitementRepository.findAll(pageable).map(batchTraitementMapper::toDTO);
    }
}