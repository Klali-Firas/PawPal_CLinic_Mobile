package com.example.pawpalclinic.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalclinic.databinding.FragmentAnimauxBinding;
import com.example.pawpalclinic.model.Animaux;
import com.google.gson.Gson;

import java.util.List;

public class MyAnimauxRecyclerViewAdapter extends RecyclerView.Adapter<MyAnimauxRecyclerViewAdapter.ViewHolder> {

    private List<Animaux> mValues;
    private Context context;
    private Gson gson = new Gson();

    public MyAnimauxRecyclerViewAdapter(Context context, List<Animaux> items) {
        this.context = context;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentAnimauxBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Animaux animaux = mValues.get(position);
        holder.mItem = animaux;
        holder.mNameView.setText(animaux.getNom());
        holder.mRaceView.setText(animaux.getRace());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AnimalDetails.class);
            String animauxJson = gson.toJson(animaux);
            intent.putExtra("animaux_json", animauxJson);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void updateList(List<Animaux> newList) {
        mValues = newList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mNameView;
        public final TextView mRaceView;
        public Animaux mItem;

        public ViewHolder(FragmentAnimauxBinding binding) {
            super(binding.getRoot());
            mNameView = binding.name;
            mRaceView = binding.race;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + " - " + mRaceView.getText() + "'";
        }
    }
}