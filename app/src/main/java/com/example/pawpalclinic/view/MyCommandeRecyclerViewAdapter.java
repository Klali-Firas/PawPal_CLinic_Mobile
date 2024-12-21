package com.example.pawpalclinic.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.Commande;
import com.example.pawpalclinic.model.CommandeProduit;
import com.example.pawpalclinic.model.Produit;
import com.example.pawpalclinic.controller.CommandeProduitController;
import com.example.pawpalclinic.controller.ProduitController;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class MyCommandeRecyclerViewAdapter extends RecyclerView.Adapter<MyCommandeRecyclerViewAdapter.ViewHolder> {

    private final List<Commande> mValues;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.getDefault());
    private final CommandeProduitController commandeProduitController;
    private final ProduitController produitController;
    private final Context context;
    private final Gson gson = new Gson();

    public MyCommandeRecyclerViewAdapter(List<Commande> items, Context context) {
        mValues = items;
        this.context = context;
        commandeProduitController = new CommandeProduitController(context);
        produitController = new ProduitController(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_commandes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Commande commande = mValues.get(position);
        holder.mOrderDateView.setText(dateFormat.format(commande.getDateCommande()));
        holder.mOrderStatusView.setText(commande.getStatut());

        calculateTotalPrice(commande).thenAccept(totalPrice -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                holder.mOrderTotalPriceView.setText(String.format(Locale.getDefault(), "%.2f TND", totalPrice));
            });
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommandeDetails.class);
            String commandeJson = gson.toJson(commande);
            intent.putExtra("commande_json", commandeJson);
            context.startActivity(intent);
        });
    }

    private CompletableFuture<Double> calculateTotalPrice(Commande commande) {
        return commandeProduitController.getCommandeProduitsByCommandeId(commande.getId())
                .thenCompose(commandeProduits -> {
                    CompletableFuture<Double> totalPriceFuture = CompletableFuture.completedFuture(0.0);
                    for (CommandeProduit cp : commandeProduits) {
                        totalPriceFuture = totalPriceFuture.thenCombine(
                                produitController.getProduitById(cp.getProduitId()),
                                (totalPrice, produit) -> totalPrice + (cp.getQuantite() * produit.getPrix())
                        );
                    }
                    return totalPriceFuture;
                });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mOrderDateView;
        public final TextView mOrderStatusView;
        public final TextView mOrderTotalPriceView;

        public ViewHolder(View view) {
            super(view);
            mOrderDateView = view.findViewById(R.id.order_date);
            mOrderStatusView = view.findViewById(R.id.order_status);
            mOrderTotalPriceView = view.findViewById(R.id.order_total_price);
        }
    }
}