package com.example.batch.Batch;

import com.example.batch.DTO.EmployeeDTO;
import com.example.batch.Entities.Employee;
import com.example.batch.Service.BatchErrorLogService;
import com.example.batch.Service.EmployeeService;
import com.example.batch.Mapper.EmployeeMapper;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class EmployeeProcessor implements ItemProcessor<EmployeeDTO, Employee> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern IMMAT_PATTERN = Pattern.compile("^RH\\d{4}$");
    private final BatchErrorLogService errorLogService;
    private final EmployeeService employeeService;
    private final Set<String> emailsInBatch = new HashSet<>();
    private final Set<String> immatriculationsInBatch = new HashSet<>();

    public EmployeeProcessor(BatchErrorLogService errorLogService, EmployeeService employeeService) {
        this.errorLogService = errorLogService;
        this.employeeService = employeeService;
    }

    @Override
    public Employee process(EmployeeDTO dto) {
        String email = dto.getEmail();
        String immat = dto.getImmatriculation();

        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            errorLogService.logError(
                    "Format d'email invalide: " + email,
                    "unknown",
                    -1
            );
            return null;
        }

        if (immat == null || !IMMAT_PATTERN.matcher(immat).matches()) {
            errorLogService.logError(
                    "Format immatriculation invalide: " + immat,
                    "unknown",
                    -1
            );
            return null;
        }

        // Check for duplicate in batch
        if (emailsInBatch.contains(email)) {
            errorLogService.logError(
                    "Email en double dans le fichier: " + email,
                    "unknown",
                    -1
            );
            return null;
        }
        if (immatriculationsInBatch.contains(immat)) {
            errorLogService.logError(
                    "Immatriculation en double dans le fichier: " + immat,
                    "unknown",
                    -1
            );
            return null;
        }

        // Check for duplicate in DB
        if (employeeService.existsByEmail(email)) {
            errorLogService.logError(
                    "Email déjà utilisé en base: " + email,
                    "unknown",
                    -1
            );
            return null;
        }
        if (employeeService.existsByImmatriculation(immat)) {
            errorLogService.logError(
                    "Immatriculation déjà utilisée en base: " + immat,
                    "unknown",
                    -1
            );
            return null;
        }

        emailsInBatch.add(email);
        immatriculationsInBatch.add(immat);
        return EmployeeMapper.toEntity(dto);
    }
}