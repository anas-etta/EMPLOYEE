package com.example.backend_Employee.DTO;

public class BatchErrorLogDTO {
    private Long id;
    private Integer ligne;
    private String errorMessage;
    private String fileName;

    public BatchErrorLogDTO() {}

    public BatchErrorLogDTO(Long id, Integer ligne, String errorMessage, String fileName) {
        this.id = id;
        this.ligne = ligne;
        this.errorMessage = errorMessage;
        this.fileName = fileName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getLigne() { return ligne; }
    public void setLigne(Integer ligne) { this.ligne = ligne; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}