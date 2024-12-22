package com.example.pawpalclinic.controller;

import android.content.Context;

import com.example.pawpalclinic.model.RendezVous;
import com.example.pawpalclinic.service.RendezVousService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RendezVousController {

    private final RendezVousService rendezVousService;

    public RendezVousController(Context c) {
        this.rendezVousService = new RendezVousService(c);
    }

    // Get all rendezvous
    public CompletableFuture<List<RendezVous>> getAllRendezVous() {
        return rendezVousService.getAllRendezVous();
    }

    // Assign veterinaire
    public CompletableFuture<RendezVous> assignVeterinaire(int rendezVousId, int veterinaireId) {
        return rendezVousService.assignVeterinaire(rendezVousId, veterinaireId);
    }

    // Create new rendezvous
    public CompletableFuture<RendezVous> createRendezVous(RendezVous rendezVous) {
        return rendezVousService.createRendezVous(rendezVous);
    }

    // Get rendezvous by veterinaire ID
    public CompletableFuture<List<RendezVous>> getRendezVousByVeterinaireId(int veterinaireId) {
        return rendezVousService.getRendezVousByVeterinaireId(veterinaireId);
    }

    // Update existing rendezvous
    public CompletableFuture<RendezVous> updateRendezVous(int id, RendezVous rendezVous) {
        return rendezVousService.updateRendezVous(id, rendezVous);
    }

    // Get rendezvous by user ID
    public CompletableFuture<List<RendezVous>> getRendezVousByUserId(int userId) {
        return rendezVousService.getRendezVousByUserId(userId);
    }

    // Export rendezvous to CSV
    public CompletableFuture<byte[]> exportRendezVousToCsv() {
        return rendezVousService.exportRendezVousToCsv();
    }
}