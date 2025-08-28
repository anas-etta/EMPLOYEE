package com.example.batch.DTO;

public class BatchErrorLogDTO {
    private Long id;
    private int ligne;
    private String errorMessage;
    private String fileName;

    public BatchErrorLogDTO() {}

    public BatchErrorLogDTO(Long id, int ligne, String errorMessage, String fileName) {
        this.id = id;
        this.ligne = ligne;
        this.errorMessage = errorMessage;
        this.fileName = fileName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getLigne() { return ligne; }
    public void setLigne(int ligne) { this.ligne = ligne; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}