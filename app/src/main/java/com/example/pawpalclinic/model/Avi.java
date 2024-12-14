// Avi.java
package com.example.pawpalclinic.model;

import java.util.Date;

public class Avi {
    private int id;
    private int rendezVousId;
    private Integer note;
    private String commentaire;
    private Date creeLe;
    private int proprietaireId;

    // Constructor
    public Avi(int id, int rendezVousId, Integer note, String commentaire, Date creeLe, int proprietaireId) {
        this.id = id;
        this.rendezVousId = rendezVousId;
        this.note = note;
        this.commentaire = commentaire;
        this.creeLe = creeLe;
        this.proprietaireId = proprietaireId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRendezVousId() {
        return rendezVousId;
    }

    public void setRendezVousId(int rendezVousId) {
        this.rendezVousId = rendezVousId;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Date getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(Date creeLe) {
        this.creeLe = creeLe;
    }

    public int getProprietaireId() {
        return proprietaireId;
    }

    public void setProprietaireId(int proprietaireId) {
        this.proprietaireId = proprietaireId;
    }

    @Override
    public String toString() {
        return "Avi{" +
                "id=" + id +
                ", rendezVousId=" + rendezVousId +
                ", note=" + note +
                ", commentaire='" + commentaire + '\'' +
                ", creeLe=" + creeLe +
                ", proprietaireId=" + proprietaireId +
                '}';
    }
}