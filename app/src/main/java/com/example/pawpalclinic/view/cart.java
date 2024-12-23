package com.example.pawpalclinic.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.controller.CommandeController;
import com.example.pawpalclinic.controller.CommandeProduitController;
import com.example.pawpalclinic.controller.ProduitController;
import com.example.pawpalclinic.model.Commande;
import com.example.pawpalclinic.model.CommandeProduit;
import com.example.pawpalclinic.model.Produit;
import com.example.pawpalclinic.service.CartService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class cart extends AppCompatActivity implements CartRecyclerViewAdapter.CartUpdateListener {

    private CartService cartService;
    private CartRecyclerViewAdapter adapter;
    private TextView totalPriceTextView;
    private CommandeController commandeController;
    private CommandeProduitController commandeProduitController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartService = new CartService(this, getUserIdFromSharedPreferences());
        commandeController = new CommandeController(this);
        commandeProduitController = new CommandeProduitController(this);

        List<Produit> cartItems = cartService.getCart();

        RecyclerView recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartRecyclerViewAdapter(this, cartItems, cartService, this);
        recyclerView.setAdapter(adapter);

        totalPriceTextView = findViewById(R.id.total_price);
        updateTotalPrice();

        Button checkoutButton = findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(v -> showConfirmationDialog());
    }

    private int getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userJsonString = sharedPreferences.getString("user", null);
        if (userJsonString != null) {
            try {
                JSONObject userJson = new JSONObject(userJsonString);
                return userJson.getInt("id");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1; // Valeur par défaut si l'ID utilisateur n'est pas trouvé
    }

    private void updateTotalPrice() {
        List<Produit> cartItems = cartService.getCart();
        double totalPrice = 0;
        for (Produit produit : cartItems) {
            totalPrice += produit.getPrix() * produit.getQuantity();
        }
        totalPriceTextView.setText(String.format(Locale.getDefault(), "Total: %.2f TND", totalPrice));

        Button checkoutButton = findViewById(R.id.checkout_button);
        if (cartItems.isEmpty()) {
            totalPriceTextView.setText("Total: 0.00 TND");
            checkoutButton.setEnabled(false);
        } else {
            checkoutButton.setEnabled(true);
        }
    }

    @Override
    public void onCartUpdated() {
        updateTotalPrice();
    }

    private void confirmOrder() {
        List<Produit> cartItems = cartService.getCart();
        List<String> outOfStockProducts = new ArrayList<>();
        ProduitController produitController = new ProduitController(this);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Produit produit : cartItems) {
            CompletableFuture<Void> future = produitController.getProduitById(produit.getId())
                    .thenAccept(fetchedProduit -> {
                        if (produit.getQuantity() > fetchedProduit.getQuantiteStock()) {
                            outOfStockProducts.add(fetchedProduit.getNomProduit());
                            Log.d("cart", "Pas assez de stock pour : " + fetchedProduit);
                        }
                    });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            if (outOfStockProducts.isEmpty()) {
                placeOrder();
            } else {
                String message = "Stock insuffisant pour : " + String.join(", ", outOfStockProducts);
                runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void placeOrder() {
        Commande commande = new Commande(0, getUserIdFromSharedPreferences(), new Date(), "en_attente");
        CompletableFuture<Commande> createdCommandeFuture = commandeController.createCommande(commande);

        createdCommandeFuture.thenAccept(createdCommande -> {
            List<CommandeProduit> order = new ArrayList<>();
            for (Produit product : cartService.getCart()) {
                order.add(new CommandeProduit(0, createdCommande.getId(), product.getId(), product.getQuantity()));
            }

            for (CommandeProduit commandeProduit : order) {
                commandeProduitController.createCommandeProduit(commandeProduit).thenAccept(result -> {
                    System.out.println("Article de commande confirmé !");
                }).exceptionally(error -> {
                    System.err.println("Erreur lors de l'enregistrement de l'article de commande : " + error.getMessage());
                    return null;
                });
            }

            runOnUiThread(() -> {
                clearCart(); // Vider le panier et mettre à jour l'interface utilisateur
                Toast.makeText(this, "Commande passée avec succès !", Toast.LENGTH_SHORT).show();
            });
        }).exceptionally(error -> {
            System.err.println("Erreur lors de la création de la commande : " + error.getMessage());
            return null;
        });
    }

    private void showConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmer la commande")
                .setMessage("Êtes-vous sûr de vouloir passer cette commande ?")
                .setIcon(R.drawable.ic_confirmation) // Utiliser une icône appropriée
                .setPositiveButton("Oui", (dialog, whichButton) -> confirmOrder())
                .setNegativeButton("Non", null)
                .show();
    }

    private void clearCart() {
        cartService.clearCart();
        adapter.mValues.clear(); // Vider les données de l'adaptateur
        adapter.notifyDataSetChanged(); // Notifier l'adaptateur pour rafraîchir l'interface utilisateur
        updateTotalPrice(); // Mettre à jour le prix total
    }
}