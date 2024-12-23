package com.example.pawpalclinic.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.Produit;
import com.example.pawpalclinic.service.CartService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyProduitRecyclerViewAdapter extends RecyclerView.Adapter<MyProduitRecyclerViewAdapter.ViewHolder> {

    private final OnItemClickListener listener;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final int userId;
    private final CartUpdateListener cartUpdateListener;
    private List<Produit> mValues;

    public MyProduitRecyclerViewAdapter(List<Produit> items, OnItemClickListener listener, int userId, CartUpdateListener cartUpdateListener) {
        mValues = items;
        this.listener = listener;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.userId = userId;
        this.cartUpdateListener = cartUpdateListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_produit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Produit produit = mValues.get(position);
        holder.productName.setText(produit.getNomProduit());
        holder.productDescription.setText(produit.getDescription());
        holder.productPrice.setText(String.format(Locale.getDefault(), "%.2f TND", produit.getPrix()));
        loadImage(produit.getImage(), holder.productImage);

        if (produit.getQuantiteStock() == 0) {
            holder.addToCartButton.setEnabled(false);
            holder.addToCartButton.setText("Out of Stock");
        } else {
            holder.addToCartButton.setEnabled(true);
            holder.addToCartButton.setText(R.string.add_to_cart);
        }

        holder.itemView.setOnClickListener(v -> showAddToCartDialog(holder.itemView.getContext(), produit));
        holder.addToCartButton.setOnClickListener(v -> showAddToCartDialog(holder.itemView.getContext(), produit));
    }

    private void showAddToCartDialog(Context context, Produit produit) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_add_to_cart, null);
        builder.setView(dialogView);

        TextView productName = dialogView.findViewById(R.id.dialog_product_name);
        ImageView productImage = dialogView.findViewById(R.id.dialog_product_image);
        TextView productDescription = dialogView.findViewById(R.id.dialog_product_description);
        EditText quantityEdit = dialogView.findViewById(R.id.edit_quantity);
        TextView totalText = dialogView.findViewById(R.id.text_total);
        Button decreaseButton = dialogView.findViewById(R.id.button_decrease_quantity);
        Button increaseButton = dialogView.findViewById(R.id.button_increase_quantity);
        Button confirmButton = dialogView.findViewById(R.id.button_confirm);


        productName.setText(produit.getNomProduit());
        productDescription.setText(produit.getDescription());
        loadImage(produit.getImage(), productImage);

        CartService cartService = new CartService(context, userId);
        Produit existingProduct = cartService.getProductById(produit.getId());
        final int[] quantity = {existingProduct != null ? existingProduct.getQuantity() : 1};
        quantityEdit.setText(String.valueOf(quantity[0]));
        totalText.setText(String.format(Locale.getDefault(), "Total: %.2f TND", produit.getPrix() * quantity[0]));

        decreaseButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(quantityEdit.getText().toString());
            if (currentQuantity > 1) {
                currentQuantity--;
                quantityEdit.setText(String.valueOf(currentQuantity));
                totalText.setText(String.format(Locale.getDefault(), "Total: %.2f TND", produit.getPrix() * currentQuantity));
            }
        });

        increaseButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(quantityEdit.getText().toString());
            currentQuantity++;
            quantityEdit.setText(String.valueOf(currentQuantity));
            totalText.setText(String.format(Locale.getDefault(), "Total: %.2f TND", produit.getPrix() * currentQuantity));
        });

        quantityEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int currentQuantity = Integer.parseInt(s.toString());
                    totalText.setText(String.format(Locale.getDefault(), "Total: %.2f TND", produit.getPrix() * currentQuantity));
                } catch (NumberFormatException e) {
                    // Handle invalid number format
                }
            }
        });

        AlertDialog dialog = builder.create();



        confirmButton.setOnClickListener(v -> {
            int finalQuantity = Integer.parseInt(quantityEdit.getText().toString());
            produit.setQuantity(finalQuantity);
            cartService.addToCart(produit);

            dialog.dismiss();
            Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();

            // Update cart count
            if (cartUpdateListener != null) {
                cartUpdateListener.onCartUpdated();
            }
        });


        // Disable confirm button if product is out of stock
        if (produit.getQuantiteStock() == 0) {
            confirmButton.setEnabled(false);
        }

        dialog.show();
    }

    public void updateList(List<Produit> newList) {
        mValues = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void loadImage(String url, ImageView imageView) {
        executorService.execute(() -> {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                mainHandler.post(() -> {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(Produit produit);
    }

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView productName;
        public final TextView productDescription;
        public final TextView productPrice;
        public final Button addToCartButton;
        public final ImageView productImage;

        public ViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.product_name);
            productDescription = view.findViewById(R.id.product_description);
            productPrice = view.findViewById(R.id.product_price);
            addToCartButton = view.findViewById(R.id.add_to_cart_button);
            productImage = view.findViewById(R.id.product_image);
        }
    }
}