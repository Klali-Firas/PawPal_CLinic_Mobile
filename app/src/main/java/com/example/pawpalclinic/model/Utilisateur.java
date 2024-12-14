package com.example.pawpalclinic.model;

import java.util.Date;

public class Utilisateur {
    private int id;
    private String email;
    private String role;
    private String prenom;
    private String nom;
    private String telephone;
    private Date creeLe;

    // Constructor
    public Utilisateur(int id, String email, String role, String prenom, String nom, String telephone, Date creeLe) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
        this.creeLe = creeLe;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Date getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(Date creeLe) {
        this.creeLe = creeLe;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", prenom='" + prenom + '\'' +
                ", nom='" + nom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", creeLe=" + creeLe +
                '}';
    }
}