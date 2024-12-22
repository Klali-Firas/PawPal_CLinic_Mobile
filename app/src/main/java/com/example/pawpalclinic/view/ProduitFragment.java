package com.example.pawpalclinic.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.controller.ProduitController;
import com.example.pawpalclinic.model.Produit;
import com.example.pawpalclinic.service.CartService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProduitFragment extends Fragment implements MyProduitRecyclerViewAdapter.CartUpdateListener {

    private ProduitController produitController;
    private RecyclerView recyclerView;
    private MyProduitRecyclerViewAdapter adapter;
    private List<Produit> allProduits;
    private int userId;
    private CartService cartService;
    private TextView cartCount;

    public ProduitFragment() {
    }

    public static ProduitFragment newInstance() {
        return new ProduitFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        produitController = new ProduitController(getContext());
        userId = getUserIdFromSharedPreferences();
        cartService = new CartService(getContext(), userId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produit_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        SearchView searchView = view.findViewById(R.id.search_view);
        ImageButton cartButton = view.findViewById(R.id.cart_button);
        cartCount = view.findViewById(R.id.cart_count);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProduits(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProduits(newText);
                return false;
            }
        });

        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), cart.class);
            startActivity(intent);
        });
        loadProduits();
        updateCartCount();
        return view;
    }

    private void loadProduits() {
        CompletableFuture<List<Produit>> future = produitController.getAllProduits();
        future.thenAccept(produits -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                allProduits = produits;
                adapter = new MyProduitRecyclerViewAdapter(produits, produit -> {
                    Toast.makeText(getContext(), produit.getNomProduit() + " clicked", Toast.LENGTH_SHORT).show();
                }, userId, this);
                recyclerView.setAdapter(adapter);
            });
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    private void filterProduits(String query) {
        Log.d("ProduitFragment", "Filtering produits with query: " + query);
        if (query.isEmpty()) {
            Log.d("ProduitFragment", "Query is empty, updating list with all produits");
            adapter.updateList(allProduits);
        } else {
            List<Produit> filteredList = new ArrayList<>();
            for (Produit produit : allProduits) {
                if (produit.getNomProduit().toLowerCase().contains(query.toLowerCase()) ||
                        produit.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(produit);
                }
            }
            adapter.updateList(filteredList);
        }
    }

    private int getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userJsonString = sharedPreferences.getString("user", null);
        if (userJsonString != null) {
            try {
                JSONObject userJson = new JSONObject(userJsonString);
                return userJson.getInt("id");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1; // Default value if user ID is not found
    }

    private void updateCartCount() {
        int count = cartService.getCart().size();
        if (count > 0) {
            cartCount.setText(String.valueOf(count));
            cartCount.setVisibility(View.VISIBLE);
        } else {
            cartCount.setText("0");
            cartCount.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartCount();
    }

    @Override
    public void onCartUpdated() {
        updateCartCount();
    }
}