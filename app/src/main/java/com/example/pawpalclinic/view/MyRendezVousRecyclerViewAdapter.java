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
        holder.itemView.setOnClickListener(v-> {});
        // Handle click event for "Termine" status
        if ("termine".equals(mValues.get(position).getStatut())) {
            holder.itemView.setOnClickListener(v -> showAviDialog(holder.mItem, holder));
        }
    }

    private void loadDataFromController(RendezVous rendezVous, ViewHolder holder) {
        // Fetch service name using motif (service id)
        int serviceId = rendezVous.getMotif();
        CompletableFuture<Service> serviceFuture = serviceController.getServiceById(serviceId);
        serviceFuture.thenAccept(service -> {
            if (service != null) {
                ((Activity) context).runOnUiThread(() -> holder.mMotifView.setText(service.getNomService()));
            } else {
                ((Activity) context).runOnUiThread(() -> holder.mMotifView.setText("Unknown"));
            }
        }).exceptionally(throwable -> {
            ((Activity) context).runOnUiThread(() -> holder.mMotifView.setText("Error"));
            return null;
        });

        // Fetch animal name using animal id
        int animalId = rendezVous.getAnimalId();
        CompletableFuture<Animaux> animalFuture = animauxController.getAnimauxById(animalId);
        animalFuture.thenAccept(animaux -> {
            if (animaux != null) {
                ((Activity) context).runOnUiThread(() -> holder.mAnimalNameView.setText(animaux.getNom()));
            } else {
                ((Activity) context).runOnUiThread(() -> holder.mAnimalNameView.setText("Unknown"));
            }
        }).exceptionally(throwable -> {
            ((Activity) context).runOnUiThread(() -> holder.mAnimalNameView.setText("Error"));
            return null;
        });

        // Fetch and display Avi if available
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
                    holder.mAviView.setText("No review");
                    holder.mRatingBar.setRating(0);
                });
            }
        }).exceptionally(throwable -> {
            ((Activity) context).runOnUiThread(() -> {
                holder.mAviView.setText("Error");
                holder.mRatingBar.setRating(0);
            });
            return null;
        });
    }

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

        builder.setPositiveButton("Save", (dialog, which) -> {
            int rating = (int) ratingBar.getRating();
            String comment = commentInput.getText().toString();

            Avi newAvi = new Avi(0, rendezVous.getId(), rating, comment, new Date(), currentUserID);
            CompletableFuture<Avi> future;
            if (aviHolder[0] == null) {
                Log.d("AVI", "Creating new avi");
                future = aviController.createAvi(newAvi);
            } else {
                Log.d("AVI", "Updating existing avi");
                newAvi.setId(aviHolder[0].getId());
                future = aviController.updateAvi(aviHolder[0].getId(), newAvi);
            }

            future.thenAccept(savedAvi -> {
                ((Activity) context).runOnUiThread(() -> {
                    notifyDataSetChanged();
                    Toast.makeText(context, "Review saved", Toast.LENGTH_SHORT).show();
                    loadDataFromController(rendezVous, holder);
                    dialog.dismiss();
                });
            }).exceptionally(throwable -> {
                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Error saving review: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
                return null;
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
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