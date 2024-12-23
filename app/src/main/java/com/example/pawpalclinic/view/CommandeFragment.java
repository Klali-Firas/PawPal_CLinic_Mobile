// UserOrdersFragment.java
package com.example.pawpalclinic.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.controller.CommandeController;
import com.example.pawpalclinic.model.Commande;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommandeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MyCommandeRecyclerViewAdapter adapter;
    private List<Commande> commandesList = new ArrayList<>();
    private CommandeController commandeController;

    public CommandeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commandeController = new CommandeController(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commandes_list, container, false);

        recyclerView = view.findViewById(R.id.orders_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyCommandeRecyclerViewAdapter(commandesList, getContext());
        recyclerView.setAdapter(adapter);

        chargerCommandesUtilisateur();

        return view;
    }

    private void chargerCommandesUtilisateur() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userJsonString = sharedPreferences.getString("user", null);
        if (userJsonString != null) {
            try {
                JSONObject userJson = new JSONObject(userJsonString);
                int userId = userJson.getInt("id");

                commandeController.getCommandesByProprietaireId(userId).thenAccept(commandes -> {
                    Context context = getContext();
                    if (context != null) {
                        ((Activity) context).runOnUiThread(() -> {
                            commandesList.clear();
                            commandesList.addAll(commandes);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        });
                    }
                }).exceptionally(throwable -> {
                    Context context = getContext();
                    if (context != null) {
                        ((Activity) context).runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    }
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}