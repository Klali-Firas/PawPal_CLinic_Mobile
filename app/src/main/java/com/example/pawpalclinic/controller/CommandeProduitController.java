package com.example.pawpalclinic.controller;

import android.content.Context;

import com.example.pawpalclinic.model.CommandeProduit;
import com.example.pawpalclinic.service.CommandeProduitService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandeProduitController {

    private final CommandeProduitService commandeProduitService;

    public CommandeProduitController(Context context) {
        this.commandeProduitService = new CommandeProduitService(context);
    }

    // Get all commande produits
    public CompletableFuture<List<CommandeProduit>> getAllCommandeProduits() {
        return commandeProduitService.getAllCommandeProduits();
    }

    // Get commande produit by ID
    public CompletableFuture<CommandeProduit> getCommandeProduitById(int id) {
        return commandeProduitService.getCommandeProduitById(id);
    }

    // Create new commande produit
    public CompletableFuture<CommandeProduit> createCommandeProduit(CommandeProduit commandeProduit) {
        return commandeProduitService.createCommandeProduit(commandeProduit);
    }

    // Update existing commande produit
    public CompletableFuture<CommandeProduit> updateCommandeProduit(int id, CommandeProduit commandeProduit) {
        return commandeProduitService.updateCommandeProduit(id, commandeProduit);
    }

    // Delete commande produit by ID
    public CompletableFuture<Void> deleteCommandeProduit(int id) {
        return commandeProduitService.deleteCommandeProduit(id);
    }

    // Get commande produits by commande ID
    public CompletableFuture<List<CommandeProduit>> getCommandeProduitsByCommandeId(int commandeId) {
        return commandeProduitService.getCommandeProduitsByCommandeId(commandeId);
    }
}