package com.example.batch.Entities;

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

    @JsonProperty("ligne")
    @Column(name = "ligne")
    private int ligne; // <-- Use int for line number

    @JsonProperty("error_message")
    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @JsonProperty("file_name")
    @Column(name = "file_name", nullable = false)
    private String fileName;

    public BatchErrorLog() {}

    public BatchErrorLog(String errorMessage, String fileName, int ligne) {
        this.errorMessage = errorMessage;
        this.fileName = fileName;
        this.ligne = ligne;
    }

    public BatchErrorLog(String errorMessage, String fileName) {
        this(errorMessage, fileName, -1); // -1 or 0 if line number is unknown
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
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