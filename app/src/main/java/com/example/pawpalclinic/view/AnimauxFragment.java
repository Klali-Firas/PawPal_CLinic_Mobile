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
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.controller.AnimauxController;
import com.example.pawpalclinic.model.Animaux;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AnimauxFragment extends Fragment {

    private static final String TAG = "AnimauxFragment";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String SHARED_PREFS_NAME = "user_prefs";
    private static final String USER_KEY = "user";
    private int mColumnCount = 1;
    private AnimauxController animauxController;
    private MyAnimauxRecyclerViewAdapter adapter;
    private List<Animaux> animauxList = new ArrayList<>();
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    public AnimauxFragment() {
    }

    public static AnimauxFragment newInstance(int columnCount) {
        AnimauxFragment fragment = new AnimauxFragment();
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
        animauxController = new AnimauxController(this.getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animaux_list, container, false);

        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list_animaux);
        SearchView searchView = view.findViewById(R.id.search_view);
        progressBar = view.findViewById(R.id.progress_bar);

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        adapter = new MyAnimauxRecyclerViewAdapter(getContext(), animauxList);
        recyclerView.setAdapter(adapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        ImageButton addPetButton = view.findViewById(R.id.add_pet_button);
        addPetButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditAnimal.class);
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(getContext());
    }
    private void loadData(Context context) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String userJsonString = sharedPreferences.getString(USER_KEY, null);
        if (userJsonString != null) {
            try {
                JSONObject userJson = new JSONObject(userJsonString);
                int proprietaireId = userJson.getInt("id");

                CompletableFuture<List<Animaux>> future = animauxController.getAnimauxByProprietaireId(proprietaireId);
                future.thenAccept(animaux -> {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        animauxList.clear();
                        animauxList.addAll(animaux);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    });
                }).exceptionally(throwable -> {
                    Log.e(TAG, "Error loading data", throwable);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressBar.setVisibility(View.GONE);
                        // Optionally show an error message to the user
                    });
                    return null;
                });
            } catch (Exception e) {
                Log.e(TAG, "Error parsing user JSON", e);
                progressBar.setVisibility(View.GONE);
            }
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void filter(String text) {
        List<Animaux> filteredList = new ArrayList<>();
        for (Animaux item : animauxList) {
            if (item.getNom().toLowerCase().contains(text.toLowerCase()) ||
                    item.getRace().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }
}