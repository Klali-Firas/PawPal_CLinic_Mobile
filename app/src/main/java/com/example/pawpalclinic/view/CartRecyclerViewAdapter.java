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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.Produit;
import com.example.pawpalclinic.service.CartService;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.ViewHolder> {

    private List<Produit> mValues;
    private final Context context;
    private final CartService cartService;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final CartUpdateListener cartUpdateListener;

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    public CartRecyclerViewAdapter(Context context, List<Produit> items, CartService cartService, CartUpdateListener cartUpdateListener) {
        this.context = context;
        mValues = items;
        this.cartService = cartService;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.cartUpdateListener = cartUpdateListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mValues.isEmpty()) {
            return;
        }

        Produit produit = mValues.get(position);
        holder.productName.setText(produit.getNomProduit());
        holder.productUnitPrice.setText(String.format("%s TND", produit.getPrix()));
        holder.editQuantity.setText(String.valueOf(produit.getQuantity()));
        holder.productTotalPrice.setText(String.format("Total: %s TND", produit.getPrix() * produit.getQuantity()));
        loadImage(produit.getImage(), holder.productImage);

        holder.editQuantity.addTextChangedListener(new TextWatcher() {
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
                    int quantity = Integer.parseInt(s.toString());
                    produit.setQuantity(quantity);
                    cartService.addToCart(produit);
                    holder.productTotalPrice.setText(String.format("Total: %s TND", produit.getPrix() * quantity));
                    cartUpdateListener.onCartUpdated();
                } catch (NumberFormatException e) {
                    // Handle invalid number format
                }
            }
        });

        holder.buttonDecreaseQuantity.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.editQuantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                holder.editQuantity.setText(String.valueOf(quantity));
                produit.setQuantity(quantity);
                cartService.addToCart(produit);
                holder.productTotalPrice.setText(String.format("Total: %s TND", produit.getPrix() * quantity));
                cartUpdateListener.onCartUpdated();
            }
        });

        holder.buttonIncreaseQuantity.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.editQuantity.getText().toString());
            quantity++;
            holder.editQuantity.setText(String.valueOf(quantity));
            produit.setQuantity(quantity);
            cartService.addToCart(produit);
            holder.productTotalPrice.setText(String.format("Total: %s TND", produit.getPrix() * quantity));
            cartUpdateListener.onCartUpdated();
        });

        holder.buttonRemove.setOnClickListener(v -> {
            cartService.removeFromCart(produit.getId());
            mValues.remove(position);
            notifyItemRemoved(position);
            cartUpdateListener.onCartUpdated();
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView productName;
        public final TextView productUnitPrice;
        public final EditText editQuantity;
        public final TextView productTotalPrice;
        public final ImageView productImage;
        public final ImageButton buttonDecreaseQuantity;
        public final ImageButton buttonIncreaseQuantity;
        public final ImageButton buttonRemove;

        public ViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.product_name);
            productUnitPrice = view.findViewById(R.id.product_unit_price);
            editQuantity = view.findViewById(R.id.edit_quantity);
            productTotalPrice = view.findViewById(R.id.product_total_price);
            productImage = view.findViewById(R.id.product_image);
            buttonDecreaseQuantity = view.findViewById(R.id.button_decrease_quantity);
            buttonIncreaseQuantity = view.findViewById(R.id.button_increase_quantity);
            buttonRemove = view.findViewById(R.id.button_remove);
        }
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
}