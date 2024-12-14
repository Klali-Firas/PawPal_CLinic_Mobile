package com.example.pawpalclinic.controller;

import android.content.Context;

import com.example.pawpalclinic.model.Produit;
import com.example.pawpalclinic.service.ProduitService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(Context context) {
        this.produitService = new ProduitService(context);
    }

    // Get all products
    public CompletableFuture<List<Produit>> getAllProduits() {
        return produitService.getAllProduits();
    }

    // Get product by ID
    public CompletableFuture<Produit> getProduitById(int id) {
        return produitService.getProduitById(id);
    }

    // Create new product
    public CompletableFuture<Produit> createProduit(Produit produit) {
        return produitService.createProduit(produit);
    }

    // Update existing product
    public CompletableFuture<Produit> updateProduit(int id, Produit produit) {
        return produitService.updateProduit(id, produit);
    }

    // Delete product by ID
    public CompletableFuture<Void> deleteProduit(int id) {
        return produitService.deleteProduit(id);
    }
}