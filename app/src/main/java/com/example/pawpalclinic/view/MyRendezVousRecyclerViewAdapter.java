package com.example.pawpalclinic.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.controller.AnimauxController;
import com.example.pawpalclinic.controller.AviController;
import com.example.pawpalclinic.controller.RendezVousController;
import com.example.pawpalclinic.controller.ServiceController;
import com.example.pawpalclinic.databinding.FragmentRendezVousBinding;
import com.example.pawpalclinic.model.Animaux;
import com.example.pawpalclinic.model.Avi;
import com.example.pawpalclinic.model.RendezVous;
import com.example.pawpalclinic.model.Service;
import com.example.pawpalclinic.service.SignInService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class MyRendezVousRecyclerViewAdapter extends RecyclerView.Adapter<MyRendezVousRecyclerViewAdapter.ViewHolder> {

    private final List<RendezVous> mValues;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy hh:mm", Locale.getDefault());
    private final AnimauxController animauxController;
    private final ServiceController serviceController;
    private final AviController aviController;
    private final Context context;
    private int currentUserID;

    public MyRendezVousRecyclerViewAdapter(List<RendezVous> items, Context context) {
        mValues = items;
        animauxController = new AnimauxController(context);
        serviceController = new ServiceController(context);
        aviController = new AviController(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentRendezVousBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mDateView.setText(dateFormat.format(mValues.get(position).getDateRendezVous()));
        holder.mStatutView.setText(mValues.get(position).getStatut());

        JSONObject user = new SignInService(context).getSignedInUser();
        try {
            currentUserID = user.getInt("id");
        } catch (Exception e) {
            currentUserID = -1;
        }

        loadDataFromController(holder.mItem, holder);

        holder.itemView.setOnClickListener(v -> {
        });

        // Gérer l'événement de clic pour le statut "Terminé"
        if ("termine".equals(mValues.get(position).getStatut())) {
            holder.itemView.setOnClickListener(v -> showAviDialog(holder.mItem, holder));
        } else if ("en_attente".equals(mValues.get(position).getStatut())) {
            holder.itemView.setOnClickListener(v -> showCancelDialog(holder.mItem, holder));
        }
    }

    //afficher un dialog pour annuler un rendez-vous
    private void showCancelDialog(RendezVous rendezVous, ViewHolder holder) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Annuler Rendez-vous")
                .setMessage("Voulez-vous annuler ce rendez-vous?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    rendezVous.setStatut("annule");
                    new RendezVousController(context).updateRendezVous(rendezVous.getId(), rendezVous)
                            .thenAccept(updatedRendezVous -> {
                                ((Activity) context).runOnUiThread(() -> {
                                    Toast.makeText(context, "Rendez-vous annulé", Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                });
                            }).exceptionally(throwable -> {
                                ((Activity) context).runOnUiThread(() -> {
                                    Toast.makeText(context, "Erreur lors de l'annulation du rendez-vous: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                                return null;
                            });
                })
                .setNegativeButton("Non", (dialog, which) -> dialog.dismiss())
                .show();
    }

    //charger les données du controller
    private void loadDataFromController(RendezVous rendezVous, ViewHolder holder) {

        // Récupérer le nom du service en utilisant le motif (id du service)
        int serviceId = rendezVous.getMotif();
        CompletableFuture<Service> serviceFuture = serviceController.getServiceById(serviceId);
        serviceFuture.thenAccept(service -> {
            if (service != null) {
                ((Activity) context).runOnUiThread(() -> holder.mMotifView.setText(service.getNomService()));
            } else {
                ((Activity) context).runOnUiThread(() -> holder.mMotifView.setText("Inconnu"));
            }
        }).exceptionally(throwable -> {
            ((Activity) context).runOnUiThread(() -> holder.mMotifView.setText("Erreur"));
            return null;
        });

        // Récupérer le nom de l'animal en utilisant l'id de l'animal
        int animalId = rendezVous.getAnimalId();
        CompletableFuture<Animaux> animalFuture = animauxController.getAnimauxById(animalId);
        animalFuture.thenAccept(animaux -> {
            if (animaux != null) {
                ((Activity) context).runOnUiThread(() -> holder.mAnimalNameView.setText(animaux.getNom()));
            } else {
                ((Activity) context).runOnUiThread(() -> holder.mAnimalNameView.setText("Inconnu"));
            }
        }).exceptionally(throwable -> {
            ((Activity) context).runOnUiThread(() -> holder.mAnimalNameView.setText("Erreur"));
            return null;
        });

        // Récupérer et afficher l'avis si disponible
        if ("termine".equals(rendezVous.getStatut())) {
            ((Activity) context).runOnUiThread(() -> {
                holder.mAviView.setVisibility(View.VISIBLE);
                holder.mRatingBar.setVisibility(View.VISIBLE);
            });
        } else {
            ((Activity) context).runOnUiThread(() -> {
                holder.mAviView.setVisibility(View.GONE);
                holder.mRatingBar.setVisibility(View.GONE);
            });
        }

        CompletableFuture<List<Avi>> aviFuture = aviController.getAvisByRendezVousId(rendezVous.getId());
        aviFuture.thenAccept(avis -> {
            if (avis != null && !avis.isEmpty()) {
                Avi avi = avis.get(0);
                ((Activity) context).runOnUiThread(() -> {
                    holder.mAviView.setText(avi.getCommentaire());
                    holder.mRatingBar.setRating(avi.getNote());
                });
            } else {
                ((Activity) context).runOnUiThread(() -> {
                    holder.mAviView.setText("Pas d'avis");
                    holder.mRatingBar.setRating(0);
                });
            }
        }).exceptionally(throwable -> {
            ((Activity) context).runOnUiThread(() -> {
                holder.mAviView.setText("Erreur");
                holder.mRatingBar.setRating(0);
            });
            return null;
        });
    }

    //afficher un dialog pour l'avis
    private void showAviDialog(RendezVous rendezVous, ViewHolder holder) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_avi, null);
        builder.setView(dialogView);

        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        TextView commentInput = dialogView.findViewById(R.id.comment_input);
        final Avi[] aviHolder = new Avi[1];

        CompletableFuture<List<Avi>> aviFuture = aviController.getAvisByRendezVousId(rendezVous.getId());
        aviFuture.thenAccept(avis -> {
            if (avis != null && !avis.isEmpty()) {
                aviHolder[0] = avis.get(0);
                ((Activity) context).runOnUiThread(() -> {
                    ratingBar.setRating(aviHolder[0].getNote());
                    commentInput.setText(aviHolder[0].getCommentaire());
                });
            }
        }).exceptionally(throwable -> {
            ((Activity) context).runOnUiThread(() -> {
                ratingBar.setRating(0);
                commentInput.setText("");
            });
            return null;
        });

        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            int rating = (int) ratingBar.getRating();
            String comment = commentInput.getText().toString();

            Avi newAvi = new Avi(0, rendezVous.getId(), rating, comment, new Date(), currentUserID);
            CompletableFuture<Avi> future;
            if (aviHolder[0] == null) {
                Log.d("AVI", "Création d'un nouvel avis");
                future = aviController.createAvi(newAvi);
            } else {
                Log.d("AVI", "Mise à jour de l'avis existant");
                newAvi.setId(aviHolder[0].getId());
                future = aviController.updateAvi(aviHolder[0].getId(), newAvi);
            }

            future.thenAccept(savedAvi -> {
                ((Activity) context).runOnUiThread(() -> {
                    notifyDataSetChanged();
                    Toast.makeText(context, "Avis enregistré", Toast.LENGTH_SHORT).show();
                    loadDataFromController(rendezVous, holder);
                    dialog.dismiss();
                });
            }).exceptionally(throwable -> {
                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Erreur lors de l'enregistrement de l'avis: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
                return null;
            });
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());
        builder.show();
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
        public final TextView mAviView;
        public final RatingBar mRatingBar;
        public RendezVous mItem;

        public ViewHolder(FragmentRendezVousBinding binding) {
            super(binding.getRoot());
            mDateView = binding.dateRendezVous;
            mStatutView = binding.statut;
            mMotifView = binding.motif;
            mAnimalNameView = binding.animalName;
            mAviView = binding.avi;
            mRatingBar = binding.ratingBar;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mStatutView.getText() + "'";
        }
    }
}