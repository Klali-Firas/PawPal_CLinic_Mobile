package com.example.pawpalclinic.view;

import android.os.Bundle;
import android.util.Pair;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pawpalclinic.R;
import com.example.pawpalclinic.controller.CommandeProduitController;
import com.example.pawpalclinic.controller.ProduitController;
import com.example.pawpalclinic.model.Commande;
import com.example.pawpalclinic.model.CommandeProduit;
import com.example.pawpalclinic.model.Produit;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommandeDetails extends AppCompatActivity {

    private Gson gson = new Gson();
    private CommandeProduitController commandeProduitController;
    private ProduitController produitController;
    private RecyclerView recyclerView;
    private MyCommandeProduitRecyclerViewAdapter adapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy hh:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_commande_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        commandeProduitController = new CommandeProduitController(this);
        produitController = new ProduitController(this);

        // Get the commande details from the intent
        String commandeJson = getIntent().getStringExtra("commande_json");
        Commande commande = gson.fromJson(commandeJson, Commande.class);

        // Set the commande details to the views
        TextView orderDateTextView = findViewById(R.id.order_date);
        TextView orderStatusTextView = findViewById(R.id.order_status);
        TextView orderTotalPriceTextView = findViewById(R.id.order_total_price);

        orderDateTextView.setText(dateFormat.format(commande.getDateCommande()));
        orderStatusTextView.setText(commande.getStatut());
        calculateTotalPrice(commande).thenAccept(totalPrice -> {
            orderTotalPriceTextView.setText(String.format(Locale.getDefault(), "%.2f TND", totalPrice));
        });

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load and display CommandeProduits
        loadCommandeProduits(commande.getId());
    }

    private void loadCommandeProduits(int commandeId) {
        commandeProduitController.getCommandeProduitsByCommandeId(commandeId)
                .thenCompose(commandeProduits -> {
                    List<CompletableFuture<Produit>> produitFutures = commandeProduits.stream()
                            .map(cp -> produitController.getProduitById(cp.getProduitId()))
                            .collect(Collectors.toList());
                    CompletableFuture<Void> allProductsFuture = CompletableFuture.allOf(
                            produitFutures.toArray(new CompletableFuture[0])
                    );
                    return allProductsFuture.thenApply(v -> {
                        List<Produit> produits = produitFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList());
                        return new Pair<>(commandeProduits, produits);
                    });
                })
                .thenAccept(pair -> {
                    List<CommandeProduit> commandeProduits = pair.first;
                    List<Produit> produits = pair.second;
                    adapter = new MyCommandeProduitRecyclerViewAdapter(commandeProduits, produits);
                    recyclerView.setAdapter(adapter);
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
}