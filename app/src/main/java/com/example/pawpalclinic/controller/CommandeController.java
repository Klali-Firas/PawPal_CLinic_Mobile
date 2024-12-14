package com.example.pawpalclinic.controller;

import android.content.Context;

import com.example.pawpalclinic.model.Commande;
import com.example.pawpalclinic.service.CommandeService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(Context context) {
        this.commandeService = new CommandeService(context);
    }

    // Get all commandes
    public CompletableFuture<List<Commande>> getAllCommandes() {
        return commandeService.getAllCommandes();
    }

    // Get commande by ID
    public CompletableFuture<Commande> getCommandeById(int id) {
        return commandeService.getCommandeById(id);
    }

    // Create new commande
    public CompletableFuture<Commande> createCommande(Commande commande) {
        return commandeService.createCommande(commande);
    }

    // Update existing commande
    public CompletableFuture<Commande> updateCommande(int id, Commande commande) {
        return commandeService.updateCommande(id, commande);
    }

    // Delete commande by ID
    public CompletableFuture<Void> deleteCommande(int id) {
        return commandeService.deleteCommande(id);
    }

    // Get commandes by proprietaire ID
    public CompletableFuture<List<Commande>> getCommandesByProprietaireId(int proprietaireId) {
        return commandeService.getCommandesByProprietaireId(proprietaireId);
    }
}