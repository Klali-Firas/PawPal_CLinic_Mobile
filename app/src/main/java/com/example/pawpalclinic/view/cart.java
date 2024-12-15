package com.example.pawpalclinic.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.Produit;
import com.example.pawpalclinic.service.CartService;

import org.json.JSONObject;

import java.util.List;

public class cart extends AppCompatActivity implements CartRecyclerViewAdapter.CartUpdateListener {

    private CartService cartService;
    private CartRecyclerViewAdapter adapter;
    private TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartService = new CartService(this, getUserIdFromSharedPreferences());
        List<Produit> cartItems = cartService.getCart();

        RecyclerView recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartRecyclerViewAdapter(this, cartItems, cartService, this);
        recyclerView.setAdapter(adapter);

        totalPriceTextView = findViewById(R.id.total_price);
        updateTotalPrice();

        Button checkoutButton = findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(v -> Toast.makeText(this, "Checkout clicked", Toast.LENGTH_SHORT).show());
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
        return -1; // Default value if user ID is not found
    }

    private void updateTotalPrice() {
        List<Produit> cartItems = cartService.getCart();
        double totalPrice = 0;
        for (Produit produit : cartItems) {
            totalPrice += produit.getPrix() * produit.getQuantity();
        }
        totalPriceTextView.setText(String.format("Total: %s TND", totalPrice));
        if (cartItems.isEmpty()) {
            totalPriceTextView.setText("Total: 0 TND");
        }
    }

    @Override
    public void onCartUpdated() {
        updateTotalPrice();
    }
}