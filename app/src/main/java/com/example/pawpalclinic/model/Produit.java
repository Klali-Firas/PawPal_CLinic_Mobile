package com.example.pawpalclinic.model;

import java.util.Date;

public class Produit {
    private int id;
    private String nomProduit;
    private String description;
    private double prix;
    private int quantiteStock;
    private Date creeLe;
    private String image;
    private Integer quantity;

    // Constructor
    public Produit(int id, String nomProduit, String description, double prix, int quantiteStock, Date creeLe, String image, Integer quantity) {
        this.id = id;
        this.nomProduit = nomProduit;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.creeLe = creeLe;
        this.image = image;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public Date getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(Date creeLe) {
        this.creeLe = creeLe;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nomProduit='" + nomProduit + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", quantiteStock=" + quantiteStock +
                ", creeLe=" + creeLe +
                ", image='" + image + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}