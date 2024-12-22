package com.example.pawpalclinic.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalclinic.controller.AnimauxController;
import com.example.pawpalclinic.controller.ServiceController;
import com.example.pawpalclinic.databinding.FragmentRendezVousBinding;
import com.example.pawpalclinic.model.Animaux;
import com.example.pawpalclinic.model.RendezVous;
import com.example.pawpalclinic.model.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class MyRendezVousRecyclerViewAdapter extends RecyclerView.Adapter<MyRendezVousRecyclerViewAdapter.ViewHolder> {

    private final List<RendezVous> mValues;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final AnimauxController animauxController;

    private final ServiceController serviceController;

    public MyRendezVousRecyclerViewAdapter(List<RendezVous> items, AnimauxController animauxController, ServiceController serviceController) {
        mValues = items;
        this.animauxController = animauxController;
        this.serviceController = serviceController;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentRendezVousBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    // Inside the onBindViewHolder method of MyRendezVousRecyclerViewAdapter
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mDateView.setText(dateFormat.format(mValues.get(position).getDateRendezVous()));
        holder.mStatutView.setText(mValues.get(position).getStatut());

        // Fetch service name using motif (service id)
        int serviceId = mValues.get(position).getMotif();
        CompletableFuture<Service> serviceFuture = serviceController.getServiceById(serviceId);
        serviceFuture.thenAccept(service -> {
            if (service != null) {
                // Update UI on the main thread
                holder.itemView.post(() -> holder.mMotifView.setText(service.getNomService()));
            } else {
                // Update UI on the main thread
                holder.itemView.post(() -> holder.mMotifView.setText("Unknown"));
            }
        }).exceptionally(throwable -> {
            // Update UI on the main thread
            holder.itemView.post(() -> holder.mMotifView.setText("Error"));
            return null;
        });

        // Fetch animal name using animal id
        int animalId = mValues.get(position).getAnimalId();
        CompletableFuture<Animaux> animalFuture = animauxController.getAnimauxById(animalId);
        animalFuture.thenAccept(animaux -> {
            if (animaux != null) {
                // Update UI on the main thread
                holder.itemView.post(() -> holder.mAnimalNameView.setText(animaux.getNom()));
            } else {
                // Update UI on the main thread
                holder.itemView.post(() -> holder.mAnimalNameView.setText("Unknown"));
            }
        }).exceptionally(throwable -> {
            // Update UI on the main thread
            holder.itemView.post(() -> holder.mAnimalNameView.setText("Error"));
            return null;
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mDateView;
        public final TextView mStatutView;
        public final TextView mMotifView;
        public final TextView mAnimalNameView;
        public RendezVous mItem;

        public ViewHolder(FragmentRendezVousBinding binding) {
            super(binding.getRoot());
            mDateView = binding.dateRendezVous;
            mStatutView = binding.statut;
            mMotifView = binding.motif;
            mAnimalNameView = binding.animalName;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mStatutView.getText() + "'";
        }
    }
}