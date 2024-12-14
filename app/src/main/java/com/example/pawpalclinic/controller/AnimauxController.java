package com.example.pawpalclinic.controller;

import android.content.Context;

import com.example.pawpalclinic.model.Animaux;
import com.example.pawpalclinic.service.AnimauxService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AnimauxController {

    private final AnimauxService animauxService;

    public AnimauxController(Context c) {
        this.animauxService = new AnimauxService(c);
    }

    // Get all animals
    public CompletableFuture<List<Animaux>> getAllAnimaux() {
        return animauxService.getAllAnimaux();
    }

    // Get animal by ID
    public CompletableFuture<Animaux> getAnimauxById(int id) {
        return animauxService.getAnimauxById(id);
    }

    // Create new animal
    public CompletableFuture<Animaux> createAnimaux(Animaux animaux) {
        return animauxService.createAnimaux(animaux);
    }

    // Update existing animal
    public CompletableFuture<Animaux> updateAnimaux(int id, Animaux animaux) {
        return animauxService.updateAnimaux(id, animaux);
    }

    // Delete animal by ID
    public CompletableFuture<Void> deleteAnimaux(int id) {
        return animauxService.deleteAnimaux(id);
    }
    public CompletableFuture<List<Animaux>> getAnimauxByProprietaireId(int proprietaireId) {
        return animauxService.getAnimauxByProprietaireId(proprietaireId);
    }
}