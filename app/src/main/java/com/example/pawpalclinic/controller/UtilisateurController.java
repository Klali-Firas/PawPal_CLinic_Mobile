package com.example.pawpalclinic.controller;

import android.content.Context;

import com.example.pawpalclinic.model.Utilisateur;
import com.example.pawpalclinic.service.UtilisateurService;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(Context c) {
        this.utilisateurService = new UtilisateurService(c);
    }

    // Get veterinaire by ID
    public CompletableFuture<Utilisateur> getVeterinaireById(int id) {
        return utilisateurService.getVeterinaireById(id);
    }

    // Get utilisateur by ID
    public CompletableFuture<Utilisateur> getUtilisateurById(int id) {
        return utilisateurService.getUtilisateurById(id);
    }

    // Get proprietaire by animal ID
    public CompletableFuture<Utilisateur> getProprietaireByAnimalId(int animalId) {
        return utilisateurService.getProprietaireByAnimalId(animalId);
    }

    // Get all veterinaires
    public CompletableFuture<List<Utilisateur>> getAllVeterinaires() {
        return utilisateurService.getAllVeterinaires();
    }

    // Get all utilisateurs
    public CompletableFuture<List<Utilisateur>> getAllUtilisateurs() {
        return utilisateurService.getAllUtilisateurs();
    }

    // Get logged in user
    public CompletableFuture<Utilisateur> getLoggedInUser() {
        return utilisateurService.getLoggedInUser();
    }
}