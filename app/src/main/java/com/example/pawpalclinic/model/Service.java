// Service.java
package com.example.pawpalclinic.model;

public class Service {
    private int id;
    private String nomService;
    private String description;
    private Double prix;

    // Constructor
    public Service(int id, String nomService, String description, Double prix) {
        this.id = id;
        this.nomService = nomService;
        this.description = description;
        this.prix = prix;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomService() {
        return nomService;
    }

    public void setNomService(String nomService) {
        this.nomService = nomService;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", nomService='" + nomService + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                '}';
    }
}