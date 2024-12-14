package com.example.pawpalclinic.model;

import java.util.Date;

public class Animaux {
    private int id;
    private int proprietaireId;
    private String nom;
    private String race;
    private Integer age;
    private Date creeLe;

    // Constructor
    public Animaux(int id, int proprietaireId, String nom, String race, Integer age, Date creeLe) {
        this.id = id;
        this.proprietaireId = proprietaireId;
        this.nom = nom;
        this.race = race;
        this.age = age;
        this.creeLe = creeLe;
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(Date creeLe) {
        this.creeLe = creeLe;
    }

    @Override
    public String toString() {
        return "Animaux{" +
                "id=" + id +
                ", proprietaireId=" + proprietaireId +
                ", nom='" + nom + '\'' +
                ", race='" + race + '\'' +
                ", age=" + age +
                ", creeLe=" + creeLe +
                '}';
    }
}