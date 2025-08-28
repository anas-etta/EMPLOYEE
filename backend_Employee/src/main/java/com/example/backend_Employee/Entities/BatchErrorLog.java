package com.example.backend_Employee.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "batch_error_logs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ligne")
    @JsonProperty("ligne")
    private Integer ligne;

    @Column(name = "error_message", nullable = false)
    @JsonProperty("errorMessage")
    private String errorMessage;

    @Column(name = "file_name", nullable = false)
    @JsonProperty("fileName")
    private String fileName;

    public BatchErrorLog() {}

    public BatchErrorLog(String errorMessage, String fileName, Integer ligne) {
        this.errorMessage = errorMessage;
        this.fileName = fileName;
        this.ligne = ligne;
    }

    public BatchErrorLog(String errorMessage, String fileName) {
        this(errorMessage, fileName, null); // null if line number is unknown
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLigne() {
        return ligne;
    }

    public void setLigne(Integer ligne) {
        this.ligne = ligne;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}