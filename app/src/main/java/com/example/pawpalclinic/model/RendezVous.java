package com.example.pawpalclinic.model;

import java.util.Date;

public class RendezVous {
    private int id;
    private int animalId;
    private Integer veterinaireId;
    private Date dateRendezVous;
    private String statut;
    private int motif;
    private Date creeLe;
    private String remarques;

    // Constructor
    public RendezVous(int id, int animalId, Integer veterinaireId, Date dateRendezVous, String statut, int motif, Date creeLe, String remarques) {
        this.id = id;
        this.animalId = animalId;
        this.veterinaireId = veterinaireId;
        this.dateRendezVous = dateRendezVous;
        this.statut = statut;
        this.motif = motif;
        this.creeLe = creeLe;
        this.remarques = remarques;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnimalId() {
        return animalId;
    }

    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }

    public Integer getVeterinaireId() {
        return veterinaireId;
    }

    public void setVeterinaireId(Integer veterinaireId) {
        this.veterinaireId = veterinaireId;
    }

    public Date getDateRendezVous() {
        return dateRendezVous;
    }

    public void setDateRendezVous(Date dateRendezVous) {
        this.dateRendezVous = dateRendezVous;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getMotif() {
        return motif;
    }

    public void setMotif(int motif) {
        this.motif = motif;
    }

    public Date getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(Date creeLe) {
        this.creeLe = creeLe;
    }

    public String getRemarques() {
        return remarques;
    }

    public void setRemarques(String remarques) {
        this.remarques = remarques;
    }

    @Override
    public String toString() {
        return "RendezVous{" +
                "id=" + id +
                ", animalId=" + animalId +
                ", veterinaireId=" + veterinaireId +
                ", dateRendezVous=" + dateRendezVous +
                ", statut='" + statut + '\'' +
                ", motif=" + motif +
                ", creeLe=" + creeLe +
                ", remarques='" + remarques + '\'' +
                '}';
    }
}