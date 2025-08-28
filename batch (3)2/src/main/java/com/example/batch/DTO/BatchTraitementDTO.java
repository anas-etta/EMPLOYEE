package com.example.batch.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class BatchTraitementDTO {
    private Long id;
    private String nomFichier;
    private int nbrLigne;
    private int nbrLigneValide;
    private int nbrLigneInvalide;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime stopTime;

    public BatchTraitementDTO() {}

    public BatchTraitementDTO(
            Long id,
            String nomFichier,
            int nbrLigne,
            int nbrLigneValide,
            int nbrLigneInvalide,
            LocalDateTime startTime,
            LocalDateTime stopTime
    ) {
        this.id = id;
        this.nomFichier = nomFichier;
        this.nbrLigne = nbrLigne;
        this.nbrLigneValide = nbrLigneValide;
        this.nbrLigneInvalide = nbrLigneInvalide;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }

    public int getNbrLigne() { return nbrLigne; }
    public void setNbrLigne(int nbrLigne) { this.nbrLigne = nbrLigne; }

    public int getNbrLigneValide() { return nbrLigneValide; }
    public void setNbrLigneValide(int nbrLigneValide) { this.nbrLigneValide = nbrLigneValide; }

    public int getNbrLigneInvalide() { return nbrLigneInvalide; }
    public void setNbrLigneInvalide(int nbrLigneInvalide) { this.nbrLigneInvalide = nbrLigneInvalide; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getStopTime() { return stopTime; }
    public void setStopTime(LocalDateTime stopTime) { this.stopTime = stopTime; }
}