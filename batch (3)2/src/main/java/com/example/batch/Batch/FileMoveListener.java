package com.example.batch.Batch;

import com.example.batch.DTO.EmployeeDTO;
import com.example.batch.MinIO.MinIOService;
import com.example.batch.Repository.EmployeeRepository;
import com.example.batch.Service.BatchErrorLogService;
import com.example.batch.Repository.BatchTraitementRepository;
import com.example.batch.Entities.BatchTraitement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Component
public class FileMoveListener extends JobExecutionListenerSupport {

    private final NDJsonItemReader reader;
    private final MinIOService minioService;
    private final BatchErrorLogService errorLogService;
    private final EmployeeRepository employeeRepository;
    private final BatchTraitementRepository batchTraitementRepository;
    private final ObjectMapper objectMapper;

    private static final String SOURCE_DIRECTORY = "C:\\Users\\anase\\Downloads\\batch (3)2\\batch (3)\\batch (3)\\batch (3)\\batch\\src\\dossier_ndjson";
    private static final String PROCESSED_DIRECTORY = "C:\\Users\\anase\\Downloads\\batch (3)2\\batch (3)\\batch (3)\\batch (3)\\batch\\src\\main\\processed";
    private static final String ERROR_DIRECTORY = "C:\\Users\\anase\\Downloads\\batch (3)2\\batch (3)\\batch (3)\\batch (3)\\batch\\src\\main\\error";

    public FileMoveListener(
            NDJsonItemReader reader,
            MinIOService minioService,
            BatchErrorLogService errorLogService,
            EmployeeRepository employeeRepository,
            BatchTraitementRepository batchTraitementRepository
    ) {
        this.reader = reader;
        this.minioService = minioService;
        this.errorLogService = errorLogService;
        this.employeeRepository = employeeRepository;
        this.batchTraitementRepository = batchTraitementRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (reader == null) return;

        List<File> filesToMove = reader.getNdjsonFiles();

        for (File file : filesToMove) {
            boolean isValidFile = false;

            try {
                Path sourcePath = file.toPath();
                Files.createDirectories(Path.of(PROCESSED_DIRECTORY));
                Files.createDirectories(Path.of(ERROR_DIRECTORY));


                Optional<BatchTraitement> traitementOpt = batchTraitementRepository
                        .findAll()
                        .stream()
                        .filter(bt -> bt.getNomFichier().equals(file.getName()))
                        .findFirst();

                if (traitementOpt.isPresent() && traitementOpt.get().getNbrLigneValide() > 0) {
                    isValidFile = true;
                }

                Path targetPath;
                if (isValidFile) {
                    targetPath = Path.of(PROCESSED_DIRECTORY, file.getName());
                } else {
                    targetPath = Path.of(ERROR_DIRECTORY, file.getName());
                }

                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Fichier déplacé : " + sourcePath + " → " + targetPath);

                if (Files.exists(targetPath)) {
                    minioService.uploadFile(targetPath.toFile());
                    Files.deleteIfExists(sourcePath);
                }

            } catch (Exception e) {

                System.err.println("Erreur d'upload pour " + file.getName());
                e.printStackTrace();
                errorLogService.logError("Erreur d'upload pour " + file.getName(), file.getName(), -1);
            }
        }
    }
}