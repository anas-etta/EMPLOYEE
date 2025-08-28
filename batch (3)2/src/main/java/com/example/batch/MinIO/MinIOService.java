package com.example.batch.MinIO;

import com.example.batch.Service.BatchErrorLogService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class MinIOService {

    private final MinioClient minioClient;
    private final BatchErrorLogService errorLogService;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${app.directory.processed}")
    private String processedDirectory;

    @Value("${app.directory.error}")
    private String errorDirectory;

    public MinIOService(@Value("${minio.url}") String minioUrl,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey,
                        BatchErrorLogService errorLogService) {
        this.minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
        this.errorLogService = errorLogService;
    }

    public void uploadFile(File file) {

        if (isInErrorDirectory(file)) {
            System.err.println("Fichier ignoré (est dans error): " + file.getName());
            return;
        }

        if (!isInProcessedDirectory(file)) {
            System.err.println("Fichier ignoré (n'est pas dans processed): " + file.getName());
            return;
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getName())
                            .stream(inputStream, file.length(), -1)
                            .contentType("application/json")
                            .build()
            );
            System.out.println("Fichier validé et uploadé: " + file.getName());

        } catch (Exception e) {
            // Only log your custom message, do not include the Java exception message
            handleError("Erreur d'upload", file);
        }
    }

    private boolean isInProcessedDirectory(File file) {
        return file.getAbsolutePath().contains(processedDirectory);
    }

    private boolean isInErrorDirectory(File file) {
        return file.getAbsolutePath().contains(errorDirectory);
    }

    // Updated: Only your custom message, no exception message
    private void handleError(String errorType, File file) {
        String errorMsg = String.format("%s pour %s", errorType, file.getName());
        System.err.println("erreur:  " + errorMsg);
        errorLogService.logMinioError(errorMsg, file.getName());
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}