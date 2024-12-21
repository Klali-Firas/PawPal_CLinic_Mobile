package com.example.pawpalclinic.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.CommandeProduit;
import com.example.pawpalclinic.model.Produit;
import java.util.List;
import java.util.Locale;

public class MyCommandeProduitRecyclerViewAdapter extends RecyclerView.Adapter<MyCommandeProduitRecyclerViewAdapter.ViewHolder> {

    private final List<CommandeProduit> mValues;
    private final List<Produit> mProduits;

    public MyCommandeProduitRecyclerViewAdapter(List<CommandeProduit> items, List<Produit> produits) {
        mValues = items;
        mProduits = produits;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.commande_produit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        CommandeProduit commandeProduit = mValues.get(position);
        Produit produit = mProduits.get(position);
        holder.mProductNameView.setText(produit.getNomProduit());
        holder.mUnitPriceView.setText(String.format(Locale.getDefault(), "%.2f TND", produit.getPrix()));
        holder.mQuantityView.setText(String.valueOf(commandeProduit.getQuantite()));
        holder.mTotalPriceView.setText(String.format(Locale.getDefault(), "%.2f TND", commandeProduit.getQuantite() * produit.getPrix()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mProductNameView;
        public final TextView mUnitPriceView;
        public final TextView mQuantityView;
        public final TextView mTotalPriceView;

        public ViewHolder(View view) {
            super(view);
            mProductNameView = view.findViewById(R.id.product_name);
            mUnitPriceView = view.findViewById(R.id.unit_price);
            mQuantityView = view.findViewById(R.id.quantity);
            mTotalPriceView = view.findViewById(R.id.total_price);
        }
    }
}