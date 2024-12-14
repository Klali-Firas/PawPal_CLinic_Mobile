package com.example.pawpalclinic.controller;

import android.content.Context;

import com.example.pawpalclinic.model.Avi;
import com.example.pawpalclinic.service.AviService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AviController {

    private final AviService aviService;

    public AviController(Context context) {
        this.aviService = new AviService(context);
    }

    // Get all avis
    public CompletableFuture<List<Avi>> getAllAvis() {
        return aviService.getAllAvis();
    }

    // Get avi by ID
    public CompletableFuture<Avi> getAviById(int id) {
        return aviService.getAviById(id);
    }

    // Create new avi
    public CompletableFuture<Avi> createAvi(Avi avi) {
        return aviService.createAvi(avi);
    }

    // Update existing avi
    public CompletableFuture<Avi> updateAvi(int id, Avi avi) {
        return aviService.updateAvi(id, avi);
    }

    // Delete avi by ID
    public CompletableFuture<Void> deleteAvi(int id) {
        return aviService.deleteAvi(id);
    }

    // Get avis by rendezvous ID
    public CompletableFuture<List<Avi>> getAvisByRendezVousId(int rendezVousId) {
        return aviService.getAvisByRendezVousId(rendezVousId);
    }

    // Get avi by rendezvous ID and proprietaire ID
    public CompletableFuture<Avi> getAviByRendezVousIdAndProprietaireId(int rendezVousId, int proprietaireId) {
        return aviService.getAviByRendezVousIdAndProprietaireId(rendezVousId, proprietaireId);
    }
}