package com.example.pawpalclinic.controller;

import android.content.Context;

import com.example.pawpalclinic.model.Service;
import com.example.pawpalclinic.service.ServiceService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(Context context) {
        this.serviceService = new ServiceService(context);
    }

    // Get all services
    public CompletableFuture<List<Service>> getAllServices() {
        return serviceService.getAllServices();
    }

    // Get service by ID
    public CompletableFuture<Service> getServiceById(int id) {
        return serviceService.getServiceById(id);
    }

    // Create new service
    public CompletableFuture<Service> createService(Service service) {
        return serviceService.createService(service);
    }

    // Update existing service
    public CompletableFuture<Service> updateService(int id, Service service) {
        return serviceService.updateService(id, service);
    }

    // Delete service by ID
    public CompletableFuture<Void> deleteService(int id) {
        return serviceService.deleteService(id);
    }
}