package com.example.pawpalclinic.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.controller.AnimauxController;
import com.example.pawpalclinic.controller.RendezVousController;
import com.example.pawpalclinic.controller.ServiceController;
import com.example.pawpalclinic.model.Animaux;
import com.example.pawpalclinic.model.RendezVous;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RendezVousFragment extends Fragment {

    private static final String TAG = "RendezVousFragment";
    private static final String SHARED_PREFS_NAME = "user_prefs";
    private static final String USER_KEY = "user";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MyRendezVousRecyclerViewAdapter adapter;
    private final List<RendezVous> rendezVousList = new ArrayList<>();
    private AnimauxController animauxController;
    private RendezVousController rendezVousController;
    private ServiceController serviceController;

    public RendezVousFragment() {
    }

    public static RendezVousFragment newInstance(int columnCount) {
        RendezVousFragment fragment = new RendezVousFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        animauxController = new AnimauxController(getContext());
        rendezVousController = new RendezVousController(getContext());
        serviceController = new ServiceController(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rendez_vous_list, container, false);

        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list_rendez_vous);
        progressBar = view.findViewById(R.id.progress_bar);

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        adapter = new MyRendezVousRecyclerViewAdapter(rendezVousList, getContext());
        recyclerView.setAdapter(adapter);

        loadUserRendezVous(context);

        return view;
    }

    private void loadUserRendezVous(Context context) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String userJsonString = sharedPreferences.getString(USER_KEY, null);
        if (userJsonString != null) {
            try {
                JSONObject userJson = new JSONObject(userJsonString);
                int userId = userJson.getInt("id");

                CompletableFuture<List<Animaux>> animauxFuture = animauxController.getAnimauxByProprietaireId(userId);
                animauxFuture.thenCompose(animauxList -> {
                    List<Integer> animalIds = new ArrayList<>();
                    for (Animaux animaux : animauxList) {
                        animalIds.add(animaux.getId());
                    }

                    return rendezVousController.getAllRendezVous().thenApply(rendezVousList -> {
                        List<RendezVous> userRendezVous = new ArrayList<>();
                        for (RendezVous rendezVous : rendezVousList) {
                            if (animalIds.contains(rendezVous.getAnimalId())) {
                                userRendezVous.add(rendezVous);
                            }
                        }
                        return userRendezVous;
                    });
                }).thenAccept(userRendezVous -> {
                    getActivity().runOnUiThread(() -> {
                        rendezVousList.clear();
                        rendezVousList.addAll(userRendezVous);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    });
                }).exceptionally(throwable -> {
                    Log.e(TAG, "Erreur lors du chargement des données", throwable);
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                    });
                    return null;
                });
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors de l'analyse du JSON utilisateur", e);
                progressBar.setVisibility(View.GONE);
            }
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}