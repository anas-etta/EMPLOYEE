package com.example.batch.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "batch_traitement")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchTraitement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("nom_fichier")
    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @JsonProperty("nbr_ligne")
    @Column(name = "nbr_ligne", nullable = false)
    private int nbrLigne;

    @JsonProperty("nbr_ligne_valide")
    @Column(name = "nbr_ligne_valide", nullable = false)
    private int nbrLigneValide;

    @JsonProperty("nbr_ligne_invalide")
    @Column(name = "nbr_ligne_invalide", nullable = false)
    private int nbrLigneInvalide;

    @JsonProperty("start_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @JsonProperty("stop_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "stop_time", nullable = false)
    private LocalDateTime stopTime;

    public BatchTraitement() {
        this.startTime = LocalDateTime.now();
        this.stopTime = LocalDateTime.now();
    }

    public BatchTraitement(String nomFichier, int nbrLigne, int nbrLigneValide, int nbrLigneInvalide, LocalDateTime startTime, LocalDateTime stopTime) {
        this.nomFichier = nomFichier;
        this.nbrLigne = nbrLigne;
        this.nbrLigneValide = nbrLigneValide;
        this.nbrLigneInvalide = nbrLigneInvalide;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public int getNbrLigne() {
        return nbrLigne;
    }

    public void setNbrLigne(int nbrLigne) {
        this.nbrLigne = nbrLigne;
    }

    public int getNbrLigneValide() {
        return nbrLigneValide;
    }

    public void setNbrLigneValide(int nbrLigneValide) {
        this.nbrLigneValide = nbrLigneValide;
    }

    public int getNbrLigneInvalide() {
        return nbrLigneInvalide;
    }

    public void setNbrLigneInvalide(int nbrLigneInvalide) {
        this.nbrLigneInvalide = nbrLigneInvalide;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
        this.stopTime = stopTime;
    }
}