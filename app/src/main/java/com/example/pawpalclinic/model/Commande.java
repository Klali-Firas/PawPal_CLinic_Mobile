package com.example.pawpalclinic.model;

import java.util.Date;

public class Commande {
    private int id;
    private int proprietaireId;
    private Date dateCommande;
    private String statut;

    // Constructor
    public Commande(int id, int proprietaireId, Date dateCommande, String statut) {
        this.id = id;
        this.proprietaireId = proprietaireId;
        this.dateCommande = dateCommande;
        this.statut = statut;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProprietaireId() {
        return proprietaireId;
    }

    public void setProprietaireId(int proprietaireId) {
        this.proprietaireId = proprietaireId;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", proprietaireId=" + proprietaireId +
                ", dateCommande=" + dateCommande +
                ", statut='" + statut + '\'' +
                '}';
    }
}